package com.hhrzc.papaya.ppi.utils.stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Global options of the library for plain-Java usage. One set per JVM.
 *
 * The only option so far is "stage from": the NAME of the user's own variable
 * that holds the stage. The library never stores the stage itself.
 *
 * Where the name comes from (first match wins):
 *   1. PpiOptions.setStageFrom("my.project.stage")        - in code, before the first ignite()
 *   2. -Dppi.stage.from=... / env PPI_STAGE_FROM=...       - same lookup rule as everything else
 *   3. ppi.stage.from=... in ppi.properties on the classpath
 *   4. "pi.stage" - default (-Dpi.stage=dev or env PI_STAGE=dev)
 *
 * Spring users configure ppi.stage.from in application.properties instead;
 * ppi.properties is not read in Spring mode.
 */
public final class PpiOptions {

    public static final String STAGE_FROM_PROPERTY = "ppi.stage.from";
    public static final String DEFAULT_STAGE_FROM = "pi.stage";
    public static final String CONFIG_FILE = "ppi.properties";

    private static volatile String stageFrom;   // set in code; null = not set
    private static volatile boolean frozen;     // true after the first ignite()

    private PpiOptions() {
    }

    /**
     * Name of the variable that holds the stage, written in property style
     * (my.project.stage); its env form MY_PROJECT_STAGE is found automatically.
     * Must be called before the first ignite(). Repeating the same value is allowed,
     * a different value is an error.
     */
    public static synchronized void setStageFrom(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("PPU Config Fault: stage variable name must not be blank");
        }
        String trimmed = name.trim();
        if (trimmed.equals(stageFrom)) {
            return;
        }
        if (frozen) {
            throw new IllegalStateException(
                    "PPU Config Fault: options must be set before the first ignite(); stage is already read from: "
                            + getStageFrom());
        }
        if (stageFrom != null) {
            throw new IllegalStateException(
                    "PPU Config Fault: stage variable is already set to [" + stageFrom
                            + "], cannot change it to [" + trimmed + "]");
        }
        stageFrom = trimmed;
    }

    /** Name of the variable to read the stage from. Never null. */
    public static String getStageFrom() {
        String fromCode = stageFrom;
        if (fromCode != null) {
            return fromCode;
        }
        String debug = SystemLookup.find(STAGE_FROM_PROPERTY)
                .or(() -> SystemLookup.firstNonBlank(FileConfig.STAGE_FROM))
                .orElse(DEFAULT_STAGE_FROM);
        return debug;
    }

    /** Called by the library on the first ignite(): from now on options can't change. */
    public static void freeze() {
        frozen = true;
    }

    /** For the library's own tests only. */
    static synchronized void resetForTests() {
        stageFrom = null;
        frozen = false;
    }

    /**
     * Lazy holder: ppi.properties is read once, on first access,
     * and the JVM guarantees that happens exactly once, even with parallel threads.
     * STAGE_FROM is null when the file or the property is missing.
     */
    private static final class FileConfig {
        static final String STAGE_FROM = readStageFromFile();

        private static String readStageFromFile() {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try (InputStream in = loader.getResourceAsStream(CONFIG_FILE)) {
                if (in == null) {
                    return null;
                }
                Properties props = new Properties();
                props.load(in);
                return props.getProperty(STAGE_FROM_PROPERTY);
            } catch (IOException e) {
                throw new IllegalStateException("PPU Config Fault: cannot read " + CONFIG_FILE, e);
            }
        }
    }
}