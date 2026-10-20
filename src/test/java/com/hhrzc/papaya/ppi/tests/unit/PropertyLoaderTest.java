package com.hhrzc.papaya.ppi.tests.unit;

import com.hhrzc.papaya.ppi.utils.PropertyLoader;
import com.hhrzc.papaya.ppi.utils.stage.StageResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for PropertyLoader.load(path, stage) against login_page.locators:
 *
 *   okBtn=button.SignIn
 *   registerBtn=button.SignUp
 *   %dev.okBtn=button.SignIn.dev
 *   %prod.okBtn=button.SignIn.prod
 *   %prod.registerBtn=button.SignUp.prod
 */
class PropertyLoaderTest {

    private static final String PATH = "login_page.locators";

    // ---------- no stage ----------
    @ParameterizedTest(name = "stage = [{0}] -> defaults only")
    @NullAndEmptySource
    void noStage_returnsOnlyDefaults(String stage) {
        Properties props = PropertyLoader.load(PATH, stage);

        assertThat(props).containsOnly(
                entry("okBtn", "button.SignIn"),
                entry("registerBtn", "button.SignUp"));
    }

    // ---------- known stages ----------
    @Test
    void devStage_overridesOnlyKeysDefinedForDev() {
        Properties props = PropertyLoader.load(PATH, "dev");

        assertThat(props).containsOnly(
                entry("okBtn", "button.SignIn.dev"),      // overridden by %dev
                entry("registerBtn", "button.SignUp"));   // no %dev version -> default
    }

    @Test
    void prodStage_overridesAllKeysDefinedForProd() {
        Properties props = PropertyLoader.load(PATH, "prod");

        assertThat(props).containsOnly(
                entry("okBtn", "button.SignIn.prod"),
                entry("registerBtn", "button.SignUp.prod"));
    }

    // ---------- unknown stage ----------
    @Test
    void unknownStage_fallsBackToDefaults() {
        Properties props = PropertyLoader.load(PATH, "qa");

        assertThat(props).containsOnly(
                entry("okBtn", "button.SignIn"),
                entry("registerBtn", "button.SignUp"));
    }

    // ---------- prefix must match exactly ----------
    @Test
    void stagePrefixOfAnotherStage_doesNotMatch() {
        // "pro" is a prefix of "prod": without the dot in "%pro." it would wrongly match "%prod.okBtn"
        Properties props = PropertyLoader.load(PATH, "pro");

        assertThat(props).containsOnly(
                entry("okBtn", "button.SignIn"),
                entry("registerBtn", "button.SignUp"));
    }

    @Test
    void stageIsCaseSensitive() {
        Properties props = PropertyLoader.load(PATH, "PROD");

        assertThat(props).containsOnly(
                entry("okBtn", "button.SignIn"),
                entry("registerBtn", "button.SignUp"));
    }

    // ---------- staged keys never leak into the result ----------
    @ParameterizedTest(name = "stage = [{0}] -> no %-keys in result")
    @ValueSource(strings = {"dev", "prod", "qa"})
    void stagedKeysAreNeverExposed(String stage) {
        Properties props = PropertyLoader.load(PATH, stage);

        assertThat(props.stringPropertyNames())
                .noneMatch(key -> key.startsWith("%"));
    }

    // ---------- result is a fresh object ----------
    @Test
    void eachCallReturnsIndependentProperties() {
        Properties dev = PropertyLoader.load(PATH, "dev");
        Properties prod = PropertyLoader.load(PATH, "prod");

        assertThat(dev).isNotSameAs(prod);
        assertThat(dev.getProperty("okBtn")).isEqualTo("button.SignIn.dev");
        assertThat(prod.getProperty("okBtn")).isEqualTo("button.SignIn.prod");
    }

    // ------------ load ppi.stage.from value ---------
    @Test
    void ppiStageFromTest(){
        final String expected = "my.awesome.project";
        final String actual = StageResolver.resolveOrNull();

        assertThat(actual).isEqualTo(expected);
    }
}