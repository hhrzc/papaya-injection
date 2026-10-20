package com.hhrzc.papaya.ppi.tests.java;

import com.hhrzc.papaya.ppi.core.LocatorIgniter;
import com.hhrzc.papaya.ppi.tests.pages.OverrideOkBtnPage;
import com.hhrzc.papaya.ppi.tests.pages.TestPage;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * End-to-end stage tests, from the user's point of view.
 *
 * Setup they rely on:
 *   src/test/resources/ppi.properties   ->  ppi.stage.from=my.awesome.project
 *   environment                         ->  MY_AWESOME_PROJECT=dev
 *
 * The env variable can't be changed from a running JVM, so:
 *   - "dev" comes from the environment,
 *   - "prod" uses -D (System.setProperty), which beats env,
 *   - "no stage" points ppi.stage.from to a variable that doesn't exist.
 *
 * The stage is applied at ignite(), so every test ignites AFTER setting properties.
 */
@EnabledIfEnvironmentVariable(named = "MY_AWESOME_PROJECT", matches = "dev")
class StageSelectorsTest {

    @AfterEach
    void clearSystemProperties() {
        System.clearProperty("my.awesome.project");
        System.clearProperty("ppi.stage.from");
    }

    // ---------- dev: from the environment variable ----------
    @Test
    void dev_methodNameKey_usesDevSelector() {
        TestPage page = LocatorIgniter.ignite(TestPage.class);

        assertThat(page.byMethodName().getSelectorValue())
                .isEqualTo("by-method-name-selector.dev");
    }

    @Test
    void dev_sensorKey_usesDevSelector() {
        TestPage page = LocatorIgniter.ignite(TestPage.class);

        assertThat(page.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector.dev");
    }

    @Test
    void dev_keyWithoutDevVariant_fallsBackToDefault() {
        OverrideOkBtnPage page = LocatorIgniter.ignite(OverrideOkBtnPage.class);

        assertThat(page.byMethodName().getSelectorValue())
                .isEqualTo("override-method-name-selector");
    }

    @Test
    void dev_literalSelector_isNotAffectedByStage() {
        TestPage page = LocatorIgniter.ignite(TestPage.class);

        assertThat(page.bySensorValue().getSelectorValue())
                .isEqualTo("by-sensor-value");
    }

    @Test
    void dev_selectorIsReallyPassedToPlaywright() {
        Page mockPage = mock(Page.class);
        TestPage page = LocatorIgniter.ignite(TestPage.class);

        page.byMethodName().apply(mockPage);

        verify(mockPage).locator("by-method-name-selector.dev");
    }

    // ---------- prod: -D beats the environment ----------
    @Test
    void prod_sensorKey_usesProdSelector() {
        System.setProperty("my.awesome.project", "prod");
        TestPage page = LocatorIgniter.ignite(TestPage.class);

        assertThat(page.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector.prod");
    }

    @Test
    void prod_keyWithoutProdVariant_fallsBackToDefault() {
        System.setProperty("my.awesome.project", "prod");
        TestPage page = LocatorIgniter.ignite(TestPage.class);

        // %dev.byMethodName exists, but there is no %prod.byMethodName
        assertThat(page.byMethodName().getSelectorValue())
                .isEqualTo("by-method-name-selector");
    }

    // ---------- unknown stage ----------
    @Test
    void unknownStage_usesDefaults() {
        System.setProperty("my.awesome.project", "qa");
        TestPage page = LocatorIgniter.ignite(TestPage.class);

        assertThat(page.byMethodName().getSelectorValue()).isEqualTo("by-method-name-selector");
        assertThat(page.bySensorProperty().getSelectorValue()).isEqualTo("by-sensor-property-selector");
    }

    // ---------- no stage at all ----------
    @Test
    void noStage_usesDefaults() {
        // point the library to a variable that is set nowhere
        System.setProperty("ppi.stage.from", "not.existing.stage");
        TestPage page = LocatorIgniter.ignite(TestPage.class);

        assertThat(page.byMethodName().getSelectorValue()).isEqualTo("by-method-name-selector");
        assertThat(page.bySensorProperty().getSelectorValue()).isEqualTo("by-sensor-property-selector");
    }

    // ---------- ignite timing ----------
    @Test
    void stageIsFixedAtIgnite() {
        TestPage page = LocatorIgniter.ignite(TestPage.class);   // ignited with dev

        System.setProperty("my.awesome.project", "prod");          // changed too late

        assertThat(page.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector.dev");
    }
}