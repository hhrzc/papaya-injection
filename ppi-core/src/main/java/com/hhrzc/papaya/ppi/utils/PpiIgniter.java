package com.hhrzc.papaya.ppi.utils;

import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.utils.stage.StageResolver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
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

        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                handler
        );
    }
}

