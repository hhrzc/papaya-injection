package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.exceptions.ParamAnnotationMissingException;
import com.hhrzc.papaya.ppi.exceptions.SelectorMismatchKeyException;
import com.hhrzc.papaya.ppi.model.BY;
import com.hhrzc.papaya.ppi.model.WebElementSupplier;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Properties;

/**
 * {@link Sensor} handler
 */
public class WebElementInvocationHandler implements InvocationHandler {

    public static BY defaultBy = BY.CSS;

    private final Properties selectors;

    public WebElementInvocationHandler(Properties selectors) {
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

        String key = sensor.value();
        String selector = null;

        if (key.isEmpty()) {
            selector = selectors.getProperty(method.getName());
        } else if (key.startsWith("$")) {
            selector = key.substring(1);
        } else {
            selector = selectors.getProperty(key);
        }

        if (selector == null) {
            throw new RuntimeException("PPU Telemetry Fault: Key [" + key + "] not found in unit file.");
        }

        BY processedBy = defaultBy;
        if (args != null && args.length > 0) {
            int start = 0;
            if (method.getParameters()[0].getType() == BY.class) {
                processedBy = (BY) args[0];
                start = 1;
            }
            try {
                if (args.length > start) {
                    selector = addParams(selector, method.getParameters(), args, start);
                }
            } catch (ParamAnnotationMissingException e) {
                throw new ParamAnnotationMissingException(
                        "The @Param annotation missing for the method " + method.getName() +
                                ". Order number of the parameter: " + e.getParamsOrder());
            }
        }

        final BY staticBy = processedBy;
        final String staticSelector = selector;
        return (WebElementSupplier) (driver) -> {
            if (driver == null) {
                throw new IllegalArgumentException("PPU Engine Fault: WebDriver cannot be null when calling " + method.getName());
            }
            return driver.findElement(staticBy.by().apply(staticSelector));
        };

    }

    private String addParams(String selector, Parameter[] parameters, Object[] args, int start) {
        String processedSelector = selector;
        for (int i = start; i < args.length; i++) {
            Param paramAnnotation = parameters[i].getAnnotation(Param.class);
            if (paramAnnotation == null) {
                throw new ParamAnnotationMissingException(i);
            }
            String key = "{" + paramAnnotation.value() + "}";
            if (!selector.contains(key)) {
                throw new SelectorMismatchKeyException(key, selector);
            }
            processedSelector = processedSelector.replace(key, String.valueOf(args[i]));
        }

        if ((processedSelector.contains("{") && processedSelector.contains("}")) &&
                processedSelector.indexOf("{") < processedSelector.indexOf("}")
        ) {
            throw new SelectorMismatchKeyException("Unresolved placeholders found", processedSelector);
        }

        return processedSelector;
    }
}