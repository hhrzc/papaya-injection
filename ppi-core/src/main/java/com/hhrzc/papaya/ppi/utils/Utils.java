package com.hhrzc.papaya.ppi.utils;

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
}
