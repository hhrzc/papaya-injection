package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.ppi.utils.PpiIgniter;

import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * @author hhrzc
 * @since 2025/6/17 17:44
 * This class serves as the ignition point for creating proxy instances of interfaces
 * annotated with @Telemetry for Selenium-based testing.
 * It utilizes Java's dynamic proxy mechanism to create instances that handle method invocations
 * using the {@link SeleniumHandler}, which retrieves
 * selectors from the telemetry file using @Telemetry annotation.
 */

public class WebElementIgniter extends PpiIgniter {
    public static <T> T ignite(Class<T> interfaceType) {
        return ignite(interfaceType, SeleniumHandler.class);
    }
}