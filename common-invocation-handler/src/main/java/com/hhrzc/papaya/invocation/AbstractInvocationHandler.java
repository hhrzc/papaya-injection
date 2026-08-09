package com.hhrzc.papaya.invocation;


import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.exceptions.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author hhrzc
 * @date 2024/6/11 17:28
 * @description This class is responsible for handling the method invocations on the proxy instances created by the Papaya framework.
 * It processes the {@link Sensor} annotations on the methods and retrieves the corresponding selectors from the .telemetry file.
 * It also handles the parameter injection for dynamic selectors using the {@link Param} annotation.
 * L - the object that represents the locator (Playwright's Locator, Selenium's WebElement, etc.)
 * S - the supplier type that the invocation handler will return (e.g., LocatorSupplier, WebElementSupplier, etc.)
 * P - the postfix locator type that can be optionally passed to the method (e.g., PostfixLocator for Playwright,
 * PostfixElement for Selenium etc.)
 */
public abstract class AbstractInvocationHandler<L, S extends Function<?, L>, P extends UnaryOperator<L>> implements InvocationHandler {

    private final Properties selectors;

    public AbstractInvocationHandler(Properties selectors) {
        this.selectors = selectors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        if (method.isDefault()) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        }

        Sensor sensor = method.getAnnotation(Sensor.class);
        if (sensor == null) {
            throw new SensorAnnotationMissedException(
                    """
                            PPU Violation: Method [%s] requires @%s.
                            """.formatted(method.getName(), Sensor.class.getName()));
        }

        String rawSelector = resolveSelector(method, sensor);
        P postfix = extractSinglePostfix(method, args);
        final String finalSelector;

        try {
            finalSelector = injectParameters(method, args, rawSelector);
            verifyFinalSelector(finalSelector);
        } catch (ParamAnnotationMissingException e) {
            throw new ParamAnnotationMissingException(
                    "The @Param annotation missing for the method " + method.getName() +
                            ". Order number of the parameter: " + e.getParamsOrder());
        }

        return createSupplier(method, finalSelector, postfix, (Object) null);
    }

    protected abstract String injectParameters(Method method, Object[] args, String rawSelector);

    protected abstract S createSupplier(Method method, String finalSelector, P postfix, Object... args);

    protected abstract Class<P> getPostfixLocatorClass();

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

    private String resolveSelector(Method method, Sensor sensor) {
        String selector = null;
        String key = sensor.value();

        if (key.isEmpty()) {//return method name as key to look up selector in unit file
            selector = getProperty(method.getName(), method);
        } else if (key.startsWith("$")) {//return the literal selector defined in the annotation, removing the "$" prefix
            selector = key.substring(1);
        } else {//return the selector from the unit file using the key defined in the annotation
            selector = getProperty(key, method);
        }

        if (selector == null) {
            throw new RuntimeException("PPU Telemetry Fault: Key [" + key + "] not found in unit file.");
        }

        return selector;
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

