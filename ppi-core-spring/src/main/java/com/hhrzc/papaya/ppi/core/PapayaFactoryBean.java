package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.exceptions.PpiRepositoryNotExtendetException;
import com.hhrzc.papaya.ppi.exceptions.TelemetryFileImproprietyException;
import com.hhrzc.papaya.ppi.utils.PpiIgniter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.net.URL;

/**
 * @author hhrzc
 * @since 2025/6/11 17:28
 * This FactoryBean is responsible for creating proxy instances of interfaces
 * annotated with @Telemetry.
 * It reads the selectors from the specified .telemetry file and uses them to create a
 * dynamic proxy that implements the interface.
 */
public class PapayaFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> interfaceType;
    private final Class<? extends InvocationHandler> invocationHandler;
    private final String stage;

    public PapayaFactoryBean(Class<T> interfaceType, Class<? extends InvocationHandler> invocationHandler, String stage) {
        this.interfaceType = interfaceType;
        this.invocationHandler = invocationHandler;
        this.stage = stage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        Telemetry telemetryAnnotation = interfaceType.getAnnotation(Telemetry.class);
        if(telemetryAnnotation == null) {
            throw new TelemetryFileImproprietyException(interfaceType.getName(), "Missing @Telemetry annotation");
        }

        if (!PpiRepository.class.isAssignableFrom(interfaceType)) {
            throw new PpiRepositoryNotExtendetException(interfaceType.getName());
        }

        return PpiIgniter.ignite(interfaceType, invocationHandler, stage);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }
}

