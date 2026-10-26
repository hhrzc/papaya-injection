package com.hhrzc.papaya.ppi.tests.spring;

import com.hhrzc.papaya.ppi.tests.pages.TestStagesPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * stageFrom is set, but the property has an empty value -> no stage, base selectors.
 */
@SpringBootTest
@TestPropertySource(properties = "ppi.test.spring.stage=")
class SpringNoStageTest {

    @Autowired
    private TestStagesPage testStagesPage;

    @Test
    void emptyStageProperty_usesBaseSelectors() {
        assertThat(testStagesPage.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector");
        assertThat(testStagesPage.byMethodName().getSelectorValue())
                .isEqualTo("by-method-name-selector");
    }
}
