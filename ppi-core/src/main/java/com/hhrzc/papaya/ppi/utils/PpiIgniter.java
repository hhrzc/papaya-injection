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

/**
 * Creates proxy instances of interfaces annotated with {@link Telemetry}.
 * Loads the selectors from the .telemetry file, applying the current stage (see {@link PpiOptions}).
 */

public class PpiIgniter {

    private static Properties getProperties(Class<?> interfaceType, String stage) {
        Telemetry unit = interfaceType.getAnnotation(Telemetry.class);
        return PropertyLoader.load(unit.value(), stage);
    }

    @SuppressWarnings("unchecked")
    public static <T> T ignite(Class<T> interfaceType,
                                  Class<? extends InvocationHandler> handlerType) {
        T t = create(interfaceType, handlerType, StageResolver.resolveOrNull());
        PpiOptions.freeze();
        return t;
    }

    public static <T> T ignite(Class<T> interfaceType,
                               Class<? extends InvocationHandler> handlerType,
                               String stage) {
        return create(interfaceType, handlerType, stage);
    }

    @SuppressWarnings({"unchecked", "PMD.UseProperClassLoader"})
    private static <T> T create(Class<T> interfaceType,
                                Class<? extends InvocationHandler> handlerType,
                                String stage) {
        verifyInterface(interfaceType);

        Properties selectors = getProperties(interfaceType, stage);
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

        // From now on the stage variable name can't change:
        // all interfaces of this run read the stage from the same variable.
        PpiOptions.freeze();

        return t;
    }

    private static <T> void verifyInterface(Class<T> interfaceType) {
        Telemetry telemetryAnnotation = interfaceType.getAnnotation(Telemetry.class);
        if (telemetryAnnotation == null) {
            throw new IllegalStateException("PPI Fault: Missing @Telemetry on " + interfaceType.getName());
        }
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

