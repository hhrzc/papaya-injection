package com.hhrzc.papaya.ppi.utils;

import com.hhrzc.papaya.ppi.annotations.Sensor;

import java.lang.reflect.Method;

public class Utils {
    public static boolean isXPath(String locator) {
        if (locator == null || locator.trim().isEmpty()) {
            return false;
        }

        String clean = locator.trim();

        if (clean.startsWith("/")
                || clean.startsWith("./")
                || clean.startsWith("../")
                || clean.startsWith("(/")
                || clean.startsWith("(./")
                || clean.startsWith("(../")
                || clean.contains("[@")) {
            return true;
        }

        if (clean.contains("[") && clean.contains("]")) {
            if (clean.contains("[@")
                    || clean.contains("text()")
                    || clean.contains("contains(")
                    || clean.contains("last()")) {
                return true;
            }
        }

        return false;
    }

    public static String getName(Method method){
        Sensor sensor = method.getAnnotation(Sensor.class);
        if (sensor != null && !sensor.name().isEmpty()) {
            return sensor.name();
        } else {
            return method.getName();
        }
    }
}
