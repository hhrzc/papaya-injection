package com.hhrzc.papaya.ppi.utils;

import org.openqa.selenium.By;
import java.lang.reflect.Field;

public class LocatorExtractor {

    public static By extractByFromArg(Object arg) {
        if (arg == null) return null;
        try {
            Field byField = arg.getClass().getDeclaredField("arg$1");
            byField.setAccessible(true);
            Object byValue = byField.get(arg);

            if (byValue instanceof By) {
                return (By) byValue;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {//todo: mage logging
            System.err.println("Can't get By through reflection:: " + e.getMessage());
        }
        return null;
    }
}

