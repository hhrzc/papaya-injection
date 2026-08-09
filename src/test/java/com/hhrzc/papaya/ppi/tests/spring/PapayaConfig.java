package com.hhrzc.papaya.ppi.tests.spring;

import com.hhrzc.papaya.ppi.annotations.ConfigTelemetry;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigTelemetry("com/hhrzc/papaya/ppi/tests/pages")
public class PapayaConfig {
}
