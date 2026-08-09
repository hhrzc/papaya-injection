package com.hhrzc.papaya.ppi.tests.pages;

import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.core.PpiRepository;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;

@Telemetry("test_page.telemetry")
public interface OverrideOkBtnPage extends PpiRepository {
    @Sensor("overrideMethodName")
    LocatorSupplier byMethodName();
}
