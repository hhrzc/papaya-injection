package com.hhrzc.papaya.ppi.tests.java;

import com.hhrzc.papaya.ppi.core.LocatorIgniter;
import com.hhrzc.papaya.ppi.tests.pages.TestStagesPage;
import com.hhrzc.papaya.ppi.tests.support.PpiOptionsReset;
import com.hhrzc.papaya.ppi.utils.stage.PpiOptions;
import com.hhrzc.papaya.ppi.utils.stage.StageResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Stage from ENVIRONMENT variables (not -D properties).
 *
 * A running JVM can't set its own environment, so the variables are set by Gradle
 * for the test JVM (root build.gradle, test { environment ... }):
 * <pre>
 *   PPI_TEST_ENV_STAGE=prod        env style of the name "ppi.test.env.stage"
 *   ppi.test.exact.stage=dev       exact name
 * </pre>
 * Run these tests through Gradle (IntelliJ does it by default for Gradle projects).
 * Without the variables the class is skipped, with the reason in the report.
 */
@EnabledIfEnvironmentVariable(named = "PPI_TEST_ENV_STAGE", matches = "prod",
        disabledReason = "run through Gradle: build.gradle sets PPI_TEST_ENV_STAGE for the test JVM")
class StageEnvironmentTest {

    @BeforeEach
    @AfterEach
    void reset() {
        PpiOptionsReset.reset();
        System.clearProperty("ppi.test.env.stage");
        System.clearProperty("ppi.test.exact.stage");
    }

    @Test
    void envStyleName_isFound() {
        // user writes the property-style name, the library also checks MY_NAME style env
        PpiOptions.setStageFrom("ppi.test.env.stage");

        assertThat(StageResolver.resolve()).contains("prod");
    }

    @Test
    void exactEnvName_isFound() {
        PpiOptions.setStageFrom("ppi.test.exact.stage");

        assertThat(StageResolver.resolve()).contains("dev");
    }

    @Test
    void systemProperty_beatsEnvironment() {
        PpiOptions.setStageFrom("ppi.test.env.stage");
        System.setProperty("ppi.test.env.stage", "qa");    // -D wins over env PPI_TEST_ENV_STAGE=prod

        assertThat(StageResolver.resolve()).contains("qa");
    }

    @Test
    void envIsIgnored_whenStagesAreOff() {
        // PPI_TEST_ENV_STAGE=prod exists, but setStageFrom() was never called

        assertThat(StageResolver.resolveOrNull()).isNull();
    }

    @Test
    void envStage_selectsStageSelectors() {
        PpiOptions.setStageFrom("ppi.test.env.stage");     // -> env PPI_TEST_ENV_STAGE=prod
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        assertThat(page.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector.prod");
        assertThat(page.byMethodName().getSelectorValue())   // no %prod. version -> base
                .isEqualTo("by-method-name-selector");
    }
}
