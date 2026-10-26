package com.hhrzc.papaya.ppi.tests.unit;

import com.hhrzc.papaya.ppi.tests.support.PpiOptionsReset;
import com.hhrzc.papaya.ppi.utils.stage.PpiOptions;
import com.hhrzc.papaya.ppi.utils.stage.StageResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PpiOptions + StageResolver rules. Only system properties are used here:
 * a running JVM can't change its own environment variables.
 */
class StageResolverTest {

    @BeforeEach
    @AfterEach
    void reset() {
        PpiOptionsReset.reset();
        System.clearProperty("my.project.stage");
        System.clearProperty("ppi.stage.from");
        System.clearProperty("pi.stage");
    }

    // ---------- stages are off by default ----------

    @Test
    void noStageFromMeansStagesAreOff() {
        System.setProperty("my.project.stage", "prod");   // set, but nobody points to it

        assertThat(PpiOptions.getStageFrom()).isNull();
        assertThat(StageResolver.resolve()).isEmpty();
        assertThat(StageResolver.resolveOrNull()).isNull();
    }

    @Test
    void oldConfigWaysAreIgnored() {
        // 0.3.0 ways to configure the variable name: no longer supported
        System.setProperty("ppi.stage.from", "my.project.stage");
        System.setProperty("my.project.stage", "prod");
        System.setProperty("pi.stage", "dev");               // old default variable

        assertThat(PpiOptions.getStageFrom()).isNull();
        assertThat(StageResolver.resolveOrNull()).isNull();
    }

    // ---------- user's own variable ----------

    @Test
    void stageIsReadFromUsersVariable() {
        PpiOptions.setStageFrom("my.project.stage");
        System.setProperty("my.project.stage", "prod");

        assertThat(StageResolver.resolve()).contains("prod");
    }

    @Test
    void variableNotSetMeansNoStage() {
        PpiOptions.setStageFrom("my.project.stage");

        assertThat(StageResolver.resolve()).isEmpty();
        assertThat(StageResolver.resolveOrNull()).isNull();
    }

    @Test
    void valueIsTrimmed() {
        PpiOptions.setStageFrom("my.project.stage");
        System.setProperty("my.project.stage", "  prod ");

        assertThat(StageResolver.resolve()).contains("prod");
    }

    @Test
    void nameIsTrimmed() {
        PpiOptions.setStageFrom("  my.project.stage  ");

        assertThat(PpiOptions.getStageFrom()).isEqualTo("my.project.stage");
    }

    // ---------- set-once rules ----------

    @Test
    void blankNameIsRejected() {
        assertThatThrownBy(() -> PpiOptions.setStageFrom("  "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PpiOptions.setStageFrom(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
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
    void stageFromCannotBeSetAfterFreeze() {
        PpiOptions.freeze();

        assertThatThrownBy(() -> PpiOptions.setStageFrom("my.project.stage"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("before the first ignite()");
    }

    @Test
    void sameStageFromAfterFreezeIsAllowed() {
        PpiOptions.setStageFrom("my.project.stage");
        PpiOptions.freeze();

        PpiOptions.setStageFrom("my.project.stage");        // same value: no error

        assertThat(PpiOptions.getStageFrom()).isEqualTo("my.project.stage");
    }
}
