package com.hhrzc.papaya.ppi.exceptions;

import java.lang.reflect.Method;

public class TelemetryFileNotDeterminedException extends RuntimeException {
    public TelemetryFileNotDeterminedException(String className, String key, Method method) {
        super("""
                The key [%s] in method [%s] of class [%s] is supposes to be determined in property file, 
                but @Telemetry annotation is not define the filename. 
                """.formatted(key, method.getName(), className));
    }
}
