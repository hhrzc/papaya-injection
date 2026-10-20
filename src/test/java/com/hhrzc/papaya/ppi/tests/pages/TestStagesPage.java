package com.hhrzc.papaya.ppi.tests.pages;


import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.core.PpiRepository;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;

@SuppressWarnings("all")
@Telemetry("test_stage.telemetry")
public interface TestStagesPage extends PpiRepository {
    @Sensor
    LocatorSupplier byMethodName();
    @Sensor("by-sensor-property")
    LocatorSupplier bySensorProperty();
    @Sensor("$by-sensor-value")
    LocatorSupplier bySensorValue();
}
