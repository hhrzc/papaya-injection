package com.hhrzc.papaya.ppi.utils;

import com.hhrzc.papaya.ppi.annotations.Telemetry;

import java.util.Properties;

/*
    * Serves as a utility for loading properties based on the @PapayaUnit annotation.
 */

//I made it abstract, since this class is not assumed to be initialized. Just provides getProperty method for subclasses.
public abstract class PpiIgniter {
    protected static Properties getProperties(Class<?> interfaceType) {
        Telemetry unit = interfaceType.getAnnotation(Telemetry.class);
        if (unit == null) {
            throw new RuntimeException("PPU Fault: Missing @PapayaUnit on " + interfaceType.getSimpleName());
        }
        return PropertyLoader.load(unit.value());
    }
}

