package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.exceptions.ParamAnnotationMissingException;
import com.hhrzc.papaya.ppi.exceptions.SelectorMismatchKeyException;
import com.hhrzc.papaya.ppi.exceptions.UnexpectedParameterException;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;
import com.microsoft.playwright.Locator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hhrzc
 * @date 2024/6/11 17:28
 * @description This class is responsible for handling the method invocations on the proxy instances created by the Papaya framework.
 * It processes the {@link Sensor} annotations on the methods and retrieves the corresponding selectors from the properties file.
 * It also handles the parameter injection for dynamic selectors using the {@link Param} annotation.
 */
public class PlaywrightInvocationHandler implements InvocationHandler {

    private final Properties selectors;

    public PlaywrightInvocationHandler(Properties selectors) {
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
            throw new UnsupportedOperationException("PPU Violation: Method [" + method.getName() + "] requires @Telemetry.");
        }

        String rawSelector = resolveSelector(method, sensor);
        PostfixLocator postfix = extractSinglePostfix(method, args);
        String finalSelector;

        try {
            finalSelector = injectParameters(method, args, rawSelector);
        } catch (ParamAnnotationMissingException e) {
            throw new ParamAnnotationMissingException(
                    "The @Param annotation missing for the method " + method.getName() +
                            ". Order number of the parameter: " + e.getParamsOrder());
        }

        final String staticSelector = finalSelector;
        return (LocatorSupplier) (page) -> {
            if (page == null) {
                throw new IllegalArgumentException("PPU Engine Fault: Page cannot be null when calling " + method.getName());
            }
            Locator locator = page.locator(staticSelector);
            return postfix == null ? locator : postfix.apply(locator);
        };
    }

    private PostfixLocator extractSinglePostfix(Method method, Object[] args) {

        if (args == null) {
            return null;
        }

        Set<PostfixLocator> postfixLocators = Arrays.stream(args).sequential()
                .filter(arg -> arg instanceof PostfixLocator)
                .map(arg -> (PostfixLocator) arg)
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
            selector = selectors.getProperty(method.getName());
        } else if (key.startsWith("$")) {//return the literal selector defined in the annotation, removing the "$" prefix
            selector = key.substring(1);
        } else {//return the selector from the unit file using the key defined in the annotation
            selector = selectors.getProperty(key);
        }

        if (selector == null) {
            throw new RuntimeException("PPU Telemetry Fault: Key [" + key + "] not found in unit file.");
        }

        return selector;
    }

    private String injectParameters(Method method, Object[] args, String selector) {
        Parameter[] parameters = method.getParameters();
        String processedSelector = injectAndGet(args, selector, parameters);

        //verify if there are any unresolved parameters in the selector after processing
        if ((processedSelector.contains("{") && processedSelector.contains("}")) &&
                processedSelector.indexOf("{") < processedSelector.indexOf("}")
        ) {
            throw new SelectorMismatchKeyException("Unresolved placeholders found", processedSelector);
        }

        return processedSelector;
    }

    private String injectAndGet(Object[] args, String selector, Parameter[] parameters) {

        if (args == null) {
            return selector;
        }

        String processedSelector = selector;
        for (int i = 0; i < args.length; i++) {
            Param paramAnnotation = parameters[i].getAnnotation(Param.class);
            if (paramAnnotation == null) {
                if ((args[i] instanceof String) || (args[i] instanceof Integer)) {
                    throw new ParamAnnotationMissingException(i);
                }

                if (args[i] instanceof PostfixLocator) {
                    continue; //skip PostfixLocator parameters
                }

                throw new UnexpectedParameterException(
                        """
                                Unexpected parameter type at index %d in method with selector [%s].
                                Expected a String parameter with @Param annotation or a PostfixLocator.");
                                """.formatted(i, selector)
                );
            }

            String key = "{" + paramAnnotation.value() + "}";
            if (!processedSelector.contains(key)) {
                throw new SelectorMismatchKeyException(key, selector);
            }
            processedSelector = processedSelector.replace(key, String.valueOf(args[i]));
        }
        return processedSelector;
    }

}

