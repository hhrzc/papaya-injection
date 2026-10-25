package com.hhrzc.papaya.ppi.utils;

import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.exceptions.TelemetryFileImproprietyException;
import com.hhrzc.papaya.ppi.utils.stage.PpiOptions;
import com.hhrzc.papaya.ppi.utils.stage.StageResolver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Properties;

/*
    * Serves as a utility for loading properties based on the @PapayaUnit annotation.
 */

public abstract class PpiIgniter {

    private static Properties getProperties(Class<?> interfaceType) {
        Telemetry unit = interfaceType.getAnnotation(Telemetry.class);
        if (unit == null) {
            throw new RuntimeException("PPU Fault: Missing @PapayaUnit on " + interfaceType.getSimpleName());
        }
        return PropertyLoader.load(unit.value(), StageResolver.resolveOrNull());
    }

    @SuppressWarnings("unchecked")
    public static <T> T ignite(Class<T> interfaceType,
                                  Class<? extends InvocationHandler> handlerType) {

        verifyInterface(interfaceType);
        Properties selectors = getProperties(interfaceType);

        InvocationHandler handler;
        try {
            handler = handlerType
                    .getConstructor(Properties.class)
                    .newInstance(selectors);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "PPU Engine Fault: " + handlerType.getName()
                            + " must have a public constructor (Properties)", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(
                    "PPU Engine Fault: constructor of " + handlerType.getName() + " failed",
                    e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "PPU Engine Fault: cannot create " + handlerType.getName(), e);
        }

        T t = (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                handler
        );

        // Freeze the PpiOptions to prevent further modifications
        // Means that user can't change stage (environment) during pages instantiation, which could lead to inconsistent behavior.
        PpiOptions.freeze();

        return t;
    }

    private static <T> void verifyInterface(Class<T> interfaceType) {
        Telemetry telemetryAnnotation = interfaceType.getAnnotation(Telemetry.class);
        String unitPath = telemetryAnnotation.value();

        // Check if the value is blank/empty
        if (!unitPath.isBlank()) {
            if (!unitPath.endsWith(".telemetry")) {
                throw new TelemetryFileImproprietyException(interfaceType.getName(), unitPath);
            }

            URL resource = Thread.currentThread().getContextClassLoader().getResource(unitPath);

            if (resource == null) {
                throw new TelemetryFileImproprietyException(interfaceType.getName(), unitPath);
            }
        }
    }
}

