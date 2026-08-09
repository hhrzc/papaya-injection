package com.hhrzc.papaya.ppi.tests.pages;


import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.core.PpiRepository;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;

@Telemetry("bar.foo")
public interface FileNotExistsPage extends PpiRepository {
    @Sensor
    LocatorSupplier testException();
}
