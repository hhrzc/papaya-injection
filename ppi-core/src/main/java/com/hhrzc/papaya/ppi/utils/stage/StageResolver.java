package com.hhrzc.papaya.ppi.utils.stage;

import java.util.Optional;

/**
 * Plain-Java stage resolution:
 *   1. ask PpiOptions which variable holds the stage (default "pi.stage")
 *   2. read that variable: -D exact name, env exact name, env style (PI_STAGE)
 *
 * Spring uses SpringStageResolver with the Spring Environment instead.
 */
public final class StageResolver {

    private StageResolver() {
    }

    public static Optional<String> resolve() {
        String debug = PpiOptions.getStageFrom();
        Optional<String> debug2 = SystemLookup.find(debug);
        return debug2;
    }

    /** For PropertyLoader.load(path, stage), which accepts null as "no stage". */
    public static String resolveOrNull() {
        return resolve().orElse(null);
    }
}