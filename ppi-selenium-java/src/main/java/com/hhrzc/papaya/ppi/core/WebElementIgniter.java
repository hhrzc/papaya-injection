package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.ppi.utils.PpiIgniter;

import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * @author hhrzc
 * @date 2024/6/17 17:44
 * @description This class serves as the ignition point for creating proxy instances of interfaces
 * annotated with @PapayaUnit for Selenium-based testing.
 * It utilizes Java's dynamic proxy mechanism to create instances that handle method invocations
 * using the WebElementHandler, which retrieves
 * selectors from the telemetry file using @Telemetry annotation.
 */

public class WebElementIgniter extends PpiIgniter {

    @SuppressWarnings("unchecked")
    public static <T> T ignite(Class<T> interfaceType) {
        Properties selectors = getProperties(interfaceType);
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                new TestAbstractSeleniumHandler(selectors)
        );
    }
}