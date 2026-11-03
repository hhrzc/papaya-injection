package com.hhrzc.papaya.invocation;


import com.hhrzc.papaya.ppi.annotations.Locating;
import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.exceptions.ParamAnnotationMissingException;
import com.hhrzc.papaya.ppi.exceptions.SelectorMismatchKeyException;
import com.hhrzc.papaya.ppi.exceptions.SensorAnnotationMissedException;
import com.hhrzc.papaya.ppi.exceptions.TelemetryFileNotDeterminedException;
import com.hhrzc.papaya.ppi.exceptions.UnexpectedParameterException;
import com.hhrzc.papaya.ppi.model.DataSupplier;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author hhrzc
 * This class is responsible for handling the method invocations on the proxy instances created by the Papaya framework.
 * It finds the locating annotation on the method ({@link Sensor}, or any other annotation marked with {@link Locating}),
 * resolves its value (literal, .telemetry key or method name) and injects {@link Param} parameters.
 * The concrete handler decides HOW to locate the element in {@link #createSupplier}.
 * L - the object that represents the locator (Playwright's Locator, Selenium's WebElement, etc.)
 * S - the supplier type that the invocation handler will return (e.g., LocatorSupplier, WebElementSupplier,
 * WebElementsSupplier, etc.). Only DataSupplier is required, so one handler can return several supplier types.
 * P - the postfix locator type that can be optionally passed to the method (e.g., PostfixLocator for Playwright,
 * PostfixElement for Selenium etc.)
 */
public abstract class AbstractInvocationHandler<L, S extends DataSupplier, P extends UnaryOperator<L>> implements InvocationHandler {

    private final Properties selectors;

    public AbstractInvocationHandler(Properties selectors) {
        this.selectors = selectors;
    }


    protected abstract String injectParameters(Method method, Object[] args, String rawSelector);

    /**
     * Builds the supplier.
     *
     * @param source        the locating annotation found on the method
     * @param finalSelector the resolved value with parameters injected; {@code null} if the annotation
     *                      allows "no value" and none was given (e.g. {@code @ByRole} without a name)
     */
    protected abstract S createSupplier(Method method, SelectorSource source, String finalSelector,
                                        P postfix, Object... args);

    protected abstract Class<P> getPostfixLocatorClass();

    /** Which locating annotations this handler can build. */
    protected abstract Set<Class<? extends Annotation>> supportedAnnotations();


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        if (method.isDefault()) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        }

        SelectorSource source = findSource(method);
        if (source == null) {
            throw new SensorAnnotationMissedException(
                    """
                            PPU Violation: Method [%s] requires a locating annotation, one of: %s
                            """.formatted(method.getName(), supportedNames()));
        }

        String rawSelector = resolveSelector(method, source);
        P postfix = extractSinglePostfix(method, args);
        String finalSelector = null;

        if (rawSelector != null) {
            try {
                finalSelector = injectParameters(method, args, rawSelector);
                verifyFinalSelector(finalSelector);
            } catch (ParamAnnotationMissingException e) {
                throw new ParamAnnotationMissingException(
                        "The @Param annotation missing for the method " + method.getName() +
                                ". Order number of the parameter: " + e.getParamsOrder());
            }
        }

        return createSupplier(method, source, finalSelector, postfix, args);
    }

    /**
     * Finds the one locating annotation on the method: any annotation whose type is marked with {@link Locating}.
     *
     * @return the annotation in a common form, or {@code null} if the method has none
     * @throws IllegalStateException if the method has more than one, or one this handler doesn't support
     */
    protected SelectorSource findSource(Method method) {
        List<Annotation> found = Arrays.stream(method.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(Locating.class))
                .toList();

        if (found.isEmpty()) {
            return null;
        }
        if (found.size() > 1) {
            throw new IllegalStateException("PPI Violation: Method [" + method.getName()
                    + "] has more than one locating annotation: "
                    + found.stream().map(a -> "@" + a.annotationType().getSimpleName()).collect(Collectors.joining(", "))
                    + ". Use exactly one.");
        }

        Annotation annotation = found.get(0);
        if (!supportedAnnotations().contains(annotation.annotationType())) {
            throw new IllegalStateException("PPI Violation: @" + annotation.annotationType().getSimpleName()
                    + " on method [" + method.getName() + "] is not supported by " + getClass().getSimpleName()
                    + ". Supported: " + supportedNames());
        }

        Locating locating = annotation.annotationType().getAnnotation(Locating.class);
        return new SelectorSource(
                annotation,
                stringAttribute(annotation, "value"),
                locating.emptyMeansNoValue(),
                stringAttribute(annotation, "description", "name"));   // @Sensor calls it "name"
    }

    /** Readable name of the element: the annotation's description, or the method name. */
    protected static String displayName(Method method, SelectorSource source) {
        String description = source.description();
        return (description == null || description.isBlank()) ? method.getName() : description;
    }

    private String supportedNames() {
        return supportedAnnotations().stream()
                .map(type -> "@" + type.getSimpleName())
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /** Reads a String attribute by name; the first name that exists wins; "" if none exists. */
    private static String stringAttribute(Annotation annotation, String... names) {
        for (String name : names) {
            try {
                return (String) annotation.annotationType().getMethod(name).invoke(annotation);
            } catch (NoSuchMethodException e) {
                // try the next name
            } catch (ReflectiveOperationException | ClassCastException e) {
                throw new IllegalStateException("PPI Fault: cannot read " + name + "() of " + annotation, e);
            }
        }
        return "";
    }

    private P extractSinglePostfix(Method method, Object[] args) {

        if (args == null) {
            return null;
        }

        Set<P> postfixLocators = Arrays.stream(args).sequential()
                .filter(arg -> getPostfixLocatorClass().isInstance(arg))
                .map(arg -> (P) arg)
                .collect(Collectors.toSet());

        if (postfixLocators.size() > 1) {
            throw new IllegalArgumentException("PPU Violation: Only one PostfixLocator is allowed in the method " + method.getName());
        }

        if (postfixLocators.isEmpty()) {
            return null;
        }

        return postfixLocators.iterator().next();
    }

    /**
     * Resolves the annotation's value:
     * {@code ""} -> method name as key (or {@code null} if the annotation allows "no value"),
     * {@code "$..."} -> literal, anything else -> key in the .telemetry file.
     */
    private String resolveSelector(Method method, SelectorSource source) {
        String key = source.value();

        if (key.isEmpty()) {
            if (source.emptyMeansNoValue()) {
                return null;                                   // e.g. @ByRole without a name
            }
            return getProperty(method.getName(), method);      // method name as key
        }
        if (key.startsWith("$")) {
            return key.substring(1);                           // literal
        }
        return getProperty(key, method);                       // key in the .telemetry file
    }

    //check if selectors file contains the key, if not, check if @Telemetry annotation defines
    // file name to look up the key, if not, throw exception
    private String getProperty(String key, Method method) {
        String value = selectors.getProperty(key);
        if (value == null) {
            Class<?> declaringClass = method.getDeclaringClass();
            Telemetry annotation = declaringClass.getAnnotation(Telemetry.class);
            String telemetryValue = annotation.value();
            if(telemetryValue == null || telemetryValue.isEmpty()){
                throw new TelemetryFileNotDeterminedException(declaringClass.getName(), key, method);
            }
            throw new UnexpectedParameterException("PPU Telemetry Fault: Key [" + key + "] not found in unit file.");
        }
        return value;
    }

    private String verifyFinalSelector(String processedSelector) {

        //verify if there are any unresolved parameters in the selector after processing
        if ((processedSelector.contains("{") && processedSelector.contains("}")) &&
                processedSelector.indexOf("{") < processedSelector.indexOf("}")
        ) {
            throw new SelectorMismatchKeyException("Unresolved placeholders found", processedSelector);
        }

        return processedSelector;
    }

}
