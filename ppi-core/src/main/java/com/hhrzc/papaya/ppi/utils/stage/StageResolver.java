package com.hhrzc.papaya.ppi.utils.stage;

import java.util.Optional;

/**
 * Plain-Java stage resolution:
 * <ol>
 *   <li>ask {@link PpiOptions} for the name of the user's variable;
 *       no name means stages are off</li>
 *   <li>read that variable: -D exact name, env exact name, env style (MY_STAGE)</li>
 * </ol>
 */
public final class StageResolver {

    private StageResolver() {
    }

    public static Optional<String> resolve() {
        String stageFrom = PpiOptions.getStageFrom();
        if (stageFrom == null) {
            return Optional.empty();           // stages are off
        }
        return SystemLookup.find(stageFrom);
    }

    /** For PropertyLoader.load(path, stage), which accepts null as "no stage". */
    public static String resolveOrNull() {
        return resolve().orElse(null);
    }
}
