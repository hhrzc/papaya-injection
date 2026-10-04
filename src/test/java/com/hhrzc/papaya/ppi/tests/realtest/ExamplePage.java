package com.hhrzc.papaya.ppi.tests.realtest;

import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;

@Telemetry
public interface ExamplePage {
    @Sensor("$div.tool-group a[href*=testcase-studio]")
    LocatorSupplier studioPro(PostfixLocator postfixLocator);
}
