package com.hhrzc.papaya.ppi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author hhrzc
 * @since 2025/6/17 17:44
 */
public class PropertyLoader {

    /**
     * Loads the .telemetry file from the classpath.
     *
     * @param path The path provided in @PapayaUnit (e.g., "pages/login.telemetry")
     * @return A Properties object containing the selectors.
     */
    public static Properties load(String path) {
        Properties props = new Properties();
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("PPI PropertyLoader Fault: File [" + path + "] not found in resources!");
            }
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("PPI Ignition Failed: Could not read telemetry file [" + path + "]", e);
        }
    }

    public static Properties load(String path, String stage) {
        if (stage == null) {
            return load(path);
        }
        Properties raw = load(path);
        Properties result = new Properties();

        for (String key : raw.stringPropertyNames()) {
            if (!key.startsWith("%")) {
                result.setProperty(key, raw.getProperty(key));
            }
        }

        String prefix = "%" + stage + ".";
        for (String key : raw.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                result.setProperty(key.substring(prefix.length()), raw.getProperty(key));
            }
        }

        return result;
    }
}