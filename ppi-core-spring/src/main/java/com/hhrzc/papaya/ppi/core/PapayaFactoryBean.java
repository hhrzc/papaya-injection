package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.exceptions.TelemetryFileImproprietyException;
import com.hhrzc.papaya.ppi.exceptions.PpiRepositoryNotExtendetException;
import com.hhrzc.papaya.ppi.utils.PropertyLoader;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import java.net.URL;
import java.util.Properties;

/**
 * @author hhrzc
 * @since 2025/6/11 17:28
 * This FactoryBean is responsible for creating proxy instances of interfaces
 * annotated with @Telemetry.
 * It reads the selectors from the specified .telemetry file and uses them to create a
 * dynamic proxy that implements the interface.
 */
public class PapayaFactoryBean<T> implements FactoryBean<T> {

    private final static String EXT = ".telemetry";
    private final Class<T> interfaceType;
    private final Class<? extends InvocationHandler> invocationHandler;

    public PapayaFactoryBean(Class<T> interfaceType, Class<? extends InvocationHandler> invocationHandler) {
        this.interfaceType = interfaceType;
        this.invocationHandler = invocationHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String unitPath = interfaceType.getAnnotation(Telemetry.class).value();

        if (!PpiRepository.class.isAssignableFrom(interfaceType)) {
            throw new PpiRepositoryNotExtendetException(interfaceType.getName());
        }

        Properties selectors;

        // Check if the value is blank/empty
        if (unitPath == null || unitPath.trim().isEmpty()) {
            selectors = new Properties(); // Create empty properties instead of loading
        } else {
            if (!unitPath.endsWith(EXT)) {
                throw new TelemetryFileImproprietyException(interfaceType.getName(), unitPath);
            }

            URL resource = Thread.currentThread().getContextClassLoader().getResource(unitPath);

            if (resource == null) {
                throw new TelemetryFileImproprietyException(interfaceType.getName(), unitPath);
            }
        }
        selectors = PropertyLoader.load(unitPath);

        InvocationHandler handler = invocationHandler
                .getConstructor(Properties.class)
                .newInstance(selectors);

        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                handler
        );
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }
}

