package com.hhrzc.papaya.ppi.utils.stage;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The property-name -> env-name rule (same rule Spring uses).
 * Same package as SystemLookup, because toEnvName() is package-private.
 */
class SystemLookupTest {

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "my.stage,                 MY_STAGE",
            "my.great-project.stage,   MY_GREAT_PROJECT_STAGE",
            "MY_STAGE,                 MY_STAGE",
            "stage,                    STAGE"
    })
    void toEnvName(String propertyName, String envName) {
        assertThat(SystemLookup.toEnvName(propertyName)).isEqualTo(envName);
    }
}
