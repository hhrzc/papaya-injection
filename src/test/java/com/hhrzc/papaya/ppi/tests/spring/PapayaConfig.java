package com.hhrzc.papaya.ppi.tests.spring;

import com.hhrzc.papaya.ppi.annotations.ConfigTelemetry;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Stage comes from the Spring property "ppi.test.spring.stage"
 * (src/test/resources/application.properties: ppi.test.spring.stage=prod).
 */
@SpringBootApplication
@ConfigTelemetry(value = "com/hhrzc/papaya/ppi/tests/pages", stageFrom = "ppi.test.spring.stage")
public class PapayaConfig {
}
