package com.hhrzc.papaya.ppi.tests.unit;

import com.hhrzc.papaya.ppi.utils.stage.PpiOptions;
import com.hhrzc.papaya.ppi.utils.stage.StageResolver;
//import com.hhrzc.papaya.ppi.utils.stage.SystemLookup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariables;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Only system properties are used here: a running JVM can't change its own
 * environment variables. Run without PI_STAGE / PPI_STAGE_FROM in the environment
 * and without ppi.properties on the test classpath.
 */
class StageResolverTest {

    @AfterEach
    void clear() {
        System.clearProperty("pi.stage");
        System.clearProperty("my.project.stage");
        System.clearProperty(PpiOptions.STAGE_FROM_PROPERTY);
        System.clearProperty("my.awesome.property");
        PpiOptions.dropStageFrom();
    }

    @Test
    void ppiStageFromValueTest() {
        assertThat(PpiOptions.getStageFrom()).isEqualTo("my.awesome.project");
    }

    // ---------- default variable ----------
    @Test
    void defaultVariableIsPiStage() {
        System.setProperty("my.awesome.project", "dev");

        assertThat(StageResolver.resolveOrNull()).isEqualTo("dev");
    }

    @Test
    void nothingSetMeansNoStage() {
        PpiOptions.setStageFrom("non-defined");
        assertThat(StageResolver.resolve()).isEmpty();
        assertThat(StageResolver.resolveOrNull()).isNull();
    }

    @Test
    void valueIsTrimmed() {
        System.setProperty("my.awesome.project", "  prod ");
        assertThat(StageResolver.resolve()).contains("prod");
    }

    // ---------- user's own variable ----------

    @Test
    void stageFromSetInCode() {
        PpiOptions.setStageFrom("my.project.stage");
        System.setProperty("my.project.stage", "prod");
        System.setProperty("pi.stage", "dev");   // must be ignored now

        assertThat(StageResolver.resolve()).contains("prod");
    }

    @Test
    void stageFromSetAsSystemProperty() {
        System.setProperty(PpiOptions.STAGE_FROM_PROPERTY, "my.project.stage");
        System.setProperty("my.project.stage", "prod");

        Optional<String> actual = StageResolver.resolve();

        assertThat(actual).contains("prod");
    }

    // ---------- set-once rules ----------

    @Test
    @DisabledIfEnvironmentVariable(named = "MY_AWESOME_PROJECT", matches = ".*")
    void sameStageFromTwiceIsAllowed() {
        PpiOptions.setStageFrom("my.project.stage");
        PpiOptions.setStageFrom("my.project.stage");

        assertThat(PpiOptions.getStageFrom()).isEqualTo("my.project.stage");
    }

    @Test
    void differentStageFromIsRejected() {
        PpiOptions.setStageFrom("my.project.stage");

        assertThatThrownBy(() -> PpiOptions.setStageFrom("other.stage"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void stageFromCannotChangeAfterFreeze() {
        PpiOptions.freeze();

        assertThatThrownBy(() -> PpiOptions.setStageFrom("my.project.stage"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @EnabledIfEnvironmentVariables({
            @EnabledIfEnvironmentVariable(named = "MY_AWESOME_PROJECT", matches = ".*"),
    })
    void testEnvFromConfiguration() {
        System.clearProperty(PpiOptions.STAGE_FROM_PROPERTY);
        System.clearProperty("my.awesome.project");
        System.out.println("debug, stageFrom: " + PpiOptions.getStageFrom());
        String actual = StageResolver.resolveOrNull();
        String expected = "dev";
        assertThat(actual).isEqualTo(expected);
    }
}