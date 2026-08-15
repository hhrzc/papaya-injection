package unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UtilsTest {

    // This acts as your DataProvider matrix
    private static Stream<Arguments> xpathTestData() {
        return Stream.of(
                // --- Standard Absolute & Relative XPaths ---
                Arguments.of("//div[@id='main']", true),
                Arguments.of("/html/body/div", true),
                Arguments.of("./span/a", true),
                Arguments.of("../div/input", true),

                // --- Parentheses Index / Complex XPaths ---
                Arguments.of("(//div)", true),
                Arguments.of("(./span)[last()]", true),
                Arguments.of("(../input)", true),
                Arguments.of("(/html/body/div)", true),

                // --- Relative Tag-Name XPaths (No starting slashes) ---
                Arguments.of("input[@id='username']", true),
                Arguments.of("a[@href='/home']", true),
                Arguments.of("div[contains(text(), ' > ')]", true),

                // --- CSS Selectors (Should return FALSE) ---
                Arguments.of("div.bar", false),
                Arguments.of("div.css-determined", false),
                Arguments.of("#submit-button", false),
                Arguments.of("input[id='username']", false),       // CSS syntax (no @)
                Arguments.of("div > span", false),                   // CSS child combinator
                Arguments.of("(div.bar)", false),                   // Parentheses wrapped CSS

                // --- Edge Cases / Invalid Inputs ---
                Arguments.of("", false),
                Arguments.of("   ", false),
                Arguments.of(null, false)
        );
    }

    @ParameterizedTest(name = "[{index}] Testing locator: {0} -> Expected: {1}")
    @MethodSource("xpathTestData")
    public void isXpathTest(String locator, boolean expected) {
        boolean result = com.hhrzc.papaya.ppi.utils.Utils.isXPath(locator);

        Assertions.assertEquals(expected, result,
                () -> String.format("Failed for locator '%s'. Expected %b but got %b", locator, expected, result));
    }
}

