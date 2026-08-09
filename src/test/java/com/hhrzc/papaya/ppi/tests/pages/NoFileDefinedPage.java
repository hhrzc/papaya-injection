package com.hhrzc.papaya.ppi.tests.pages;

import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.core.PpiRepository;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;

@SuppressWarnings("all")
@Telemetry
public interface NoFileDefinedPage extends PpiRepository {
    @Sensor("$//hello-world-value")
    LocatorSupplier withoutFileSupplier();
    @Sensor("property-key")
    LocatorSupplier noPropertyBySensorSupplier();
    @Sensor
    LocatorSupplier noPropertySupplier();
}
