package com.hhrzc.papaya.ppi.tests.java;

import com.hhrzc.papaya.ppi.core.LocatorIgniter;
import com.hhrzc.papaya.ppi.tests.pages.OverrideOkBtnPage;
import com.hhrzc.papaya.ppi.tests.pages.TestStagesPage;
import com.hhrzc.papaya.ppi.tests.support.PpiOptionsReset;
import com.hhrzc.papaya.ppi.utils.stage.PpiOptions;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * End-to-end stage tests, from the user's point of view (plain Java):
 *
 * <pre>
 *   PpiOptions.setStageFrom("my.awesome.project");   // once, before the first ignite()
 *   -Dmy.awesome.project=dev                          // the stage itself
 * </pre>
 *
 * The stage is applied at ignite(), so every test sets it BEFORE igniting.
 * test_stage.telemetry:
 * <pre>
 *   byMethodName=by-method-name-selector
 *   by-sensor-property=by-sensor-property-selector
 *   %dev.byMethodName=by-method-name-selector.dev
 *   %dev.by-sensor-property=by-sensor-property-selector.dev
 *   %prod.by-sensor-property=by-sensor-property-selector.prod
 * </pre>
 */
class StageSelectorsTest {

    private static final String STAGE_VARIABLE = "my.awesome.project";

    @BeforeEach
    @AfterEach
    void reset() {
        PpiOptionsReset.reset();
        System.clearProperty(STAGE_VARIABLE);
    }

    private static void useStage(String stage) {
        PpiOptions.setStageFrom(STAGE_VARIABLE);
        System.setProperty(STAGE_VARIABLE, stage);
    }

    // ---------- dev ----------

    @Test
    void dev_methodNameKey_usesDevSelector() {
        useStage("dev");
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        assertThat(page.byMethodName().getSelectorValue())
                .isEqualTo("by-method-name-selector.dev");
    }

    @Test
    void dev_sensorKey_usesDevSelector() {
        useStage("dev");
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        assertThat(page.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector.dev");
    }

    @Test
    void dev_keyWithoutDevVariant_fallsBackToDefault() {
        useStage("dev");
        OverrideOkBtnPage page = LocatorIgniter.ignite(OverrideOkBtnPage.class);

        assertThat(page.byMethodName().getSelectorValue())
                .isEqualTo("override-method-name-selector");
    }

    @Test
    void dev_literalSelector_isNotAffectedByStage() {
        useStage("dev");
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        assertThat(page.bySensorValue().getSelectorValue())
                .isEqualTo("by-sensor-value");
    }

    @Test
    void dev_selectorIsReallyPassedToPlaywright() {
        useStage("dev");
        Page mockPage = mock(Page.class);
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        page.byMethodName().apply(mockPage);

        verify(mockPage).locator("by-method-name-selector.dev");
    }

    // ---------- prod ----------

    @Test
    void prod_sensorKey_usesProdSelector() {
        useStage("prod");
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        assertThat(page.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector.prod");
    }

    @Test
    void prod_keyWithoutProdVariant_fallsBackToDefault() {
        useStage("prod");
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        // %dev.byMethodName exists, but there is no %prod.byMethodName
        assertThat(page.byMethodName().getSelectorValue())
                .isEqualTo("by-method-name-selector");
    }

    // ---------- unknown stage ----------

    @Test
    void unknownStage_usesDefaults() {
        useStage("qa");
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        assertThat(page.byMethodName().getSelectorValue()).isEqualTo("by-method-name-selector");
        assertThat(page.bySensorProperty().getSelectorValue()).isEqualTo("by-sensor-property-selector");
    }

    // ---------- no stage ----------

    @Test
    void stagesOff_usesDefaults() {
        System.setProperty(STAGE_VARIABLE, "dev");     // set, but setStageFrom() was never called
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        assertThat(page.byMethodName().getSelectorValue()).isEqualTo("by-method-name-selector");
        assertThat(page.bySensorProperty().getSelectorValue()).isEqualTo("by-sensor-property-selector");
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "MY_AWESOME_PROJECT", matches = ".+",
            disabledReason = "MY_AWESOME_PROJECT is set in your environment, so the variable is not 'unset'")
    void stageVariableNotSet_usesDefaults() {
        PpiOptions.setStageFrom(STAGE_VARIABLE);       // name given, but the variable has no value
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);

        assertThat(page.byMethodName().getSelectorValue()).isEqualTo("by-method-name-selector");
        assertThat(page.bySensorProperty().getSelectorValue()).isEqualTo("by-sensor-property-selector");
    }

    // ---------- ignite timing ----------

    @Test
    void stageIsFixedAtIgnite() {
        useStage("dev");
        TestStagesPage page = LocatorIgniter.ignite(TestStagesPage.class);   // ignited with dev

        System.setProperty(STAGE_VARIABLE, "prod");                          // changed too late

        assertThat(page.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector.dev");
    }

    @Test
    void setStageFromAfterIgnite_isRejected() {
        LocatorIgniter.ignite(TestStagesPage.class);                         // first ignite() freezes

        assertThatThrownBy(() -> PpiOptions.setStageFrom(STAGE_VARIABLE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("before the first ignite()");
    }
}
