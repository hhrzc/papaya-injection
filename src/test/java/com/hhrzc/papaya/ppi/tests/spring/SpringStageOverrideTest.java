package com.hhrzc.papaya.ppi.tests.spring;

import com.hhrzc.papaya.ppi.tests.pages.TestStagesPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Same PapayaConfig (stageFrom = "ppi.test.spring.stage"), but the value is overridden to "dev".
 * @TestPropertySource beats application.properties, and it makes Spring create a SEPARATE context,
 * so this test and TestPageSensor (prod) run side by side in one JVM.
 */
@SpringBootTest
@TestPropertySource(properties = "ppi.test.spring.stage=dev")
class SpringStageOverrideTest {

    @Autowired
    private TestStagesPage testStagesPage;

    @Test
    void stageFromTestPropertySource_isUsed() {
        assertThat(testStagesPage.bySensorProperty().getSelectorValue())
                .isEqualTo("by-sensor-property-selector.dev");
        assertThat(testStagesPage.byMethodName().getSelectorValue())
                .isEqualTo("by-method-name-selector.dev");
    }
}
