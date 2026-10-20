package com.hhrzc.papaya.ppi.utils.stage;

import java.util.Locale;
import java.util.Optional;

/**
 * One lookup rule for every name the library reads from the system,
 * the same rule Spring uses for environment variables:
 *
 *   1. JVM system property with the exact name        -Dmy.project.stage=dev
 *   2. environment variable with the exact name         my.project.stage (rare)
 *   3. environment variable in env style                MY_PROJECT_STAGE=dev
 */
final class SystemLookup {

    private SystemLookup() {
    }

    static Optional<String> find(String name) {
        return firstNonBlank(
                System.getProperty(name),
                System.getenv(name),
                System.getenv(toEnvName(name)));
    }

    /** my.great-project.stage -> MY_GREAT_PROJECT_STAGE */
    static String toEnvName(String name) {
        return name.replace('.', '_')
                .replace('-', '_')
                .toUpperCase(Locale.ROOT);
    }

    static Optional<String> firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return Optional.of(value.trim());
            }
        }
        return Optional.empty();
    }
}