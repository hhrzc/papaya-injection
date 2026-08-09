package tests;

import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pages.HealthCheckPage;

@SpringBootTest
public class HealthCheckTest {

    @Autowired
    HealthCheckPage healthCheckPage;

    @Test
    void testHealthCheck() {
        LocatorSupplier helloWorldValue = healthCheckPage.getHelloWorldValue();
        System.out.println(helloWorldValue);
    }
}
