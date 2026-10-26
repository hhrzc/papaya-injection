package com.hhrzc.papaya.ppi.utils.stage;

/**
 * Global stage option of the library for plain-Java usage. One per JVM.
 *
 * <p>Stages are <b>off</b> by default: only base selectors are used.
 * To turn them on, tell the library the NAME of YOUR variable that holds the stage,
 * once, before the first {@code ignite()}:
 *
 * <pre>{@code
 * PpiOptions.setStageFrom("my.stage");
 * // run with -Dmy.stage=prod  or env MY_STAGE=prod  ->  %prod.* selectors are used
 * }</pre>
 *
 * <p>The library never stores the stage itself, only the variable name.
 * The stage value is read at every {@code ignite()}, so set it before the first
 * {@code ignite()} and don't change it during the run.
 *
 * <p>Spring (0.3.x): call {@code setStageFrom(...)} before the Spring context starts,
 * for example in a {@code static {}} block of the test base class.
 * A {@code @ConfigTelemetry(stageFrom = ...)} attribute is planned for 0.4.0.
 */

@SuppressWarnings("PMD.AvoidUsingVolatile")
public final class PpiOptions {

    private static final Object LOCK = new Object();

    private static volatile String stageFrom;   // name of the user's variable; null = stages are off
    private static volatile boolean frozen;     // true after the first ignite()

    private PpiOptions() {
    }

    /**
     * Sets the name of the variable that holds the stage, written in property style
     * ({@code my.stage}); its env form {@code MY_STAGE} is found automatically.
     *
     * <p>Must be called before the first {@code ignite()}.
     * Repeating the same value is allowed, a different value is an error.
     *
     * @throws IllegalArgumentException if the name is null or blank
     * @throws IllegalStateException    if called after the first {@code ignite()},
     *                                  or with a different name than before
     */
    public static void setStageFrom(String name) {
        synchronized (LOCK) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("PPI Config Fault: stage variable name must not be blank");
            }
            String trimmed = name.trim();
            if (trimmed.equals(stageFrom)) {
                return;
            }
            if (frozen) {
                throw new IllegalStateException(
                        "PPI Config Fault: setStageFrom() must be called before the first ignite()");
            }
            if (stageFrom != null) {
                throw new IllegalStateException(
                        "PPI Config Fault: stage variable is already set to [" + stageFrom
                                + "], cannot change it to [" + trimmed + "]");
            }
            stageFrom = trimmed;
        }
    }

    /**
     * Name of the variable to read the stage from.
     *
     * @return the name given to {@link #setStageFrom(String)}, or {@code null} if stages are off
     */
    public static String getStageFrom() {
        synchronized (LOCK) {
            return stageFrom;
        }
    }

    /**
     * Internal: called by the library on every {@code ignite()}.
     * From the first call on, {@link #setStageFrom(String)} can't change the name anymore.
     */
    public static void freeze() {
        synchronized (LOCK) {
            frozen = true;
        }
    }
}
