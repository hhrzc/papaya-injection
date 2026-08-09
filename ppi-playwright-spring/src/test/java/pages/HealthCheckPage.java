package pages;

import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.core.PpiRepository;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;

@Telemetry("health-check-page.telemetry")
public interface HealthCheckPage extends PpiRepository{
    @Sensor("$hello-world-value")
    LocatorSupplier getHelloWorldValue();
}
