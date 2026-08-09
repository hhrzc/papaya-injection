package com.hhrzc.papaya.ppi.exceptions;

import java.lang.reflect.Method;

public class SensorAnnotationMissedException extends RuntimeException {
    public SensorAnnotationMissedException(Method methodName) {
        super("Method " + methodName.getName() + " is missing @Sensor annotation.");
    }

    public SensorAnnotationMissedException(String message) {
        super(message);
    }
}
