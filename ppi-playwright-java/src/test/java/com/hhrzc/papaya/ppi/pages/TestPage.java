package com.hhrzc.papaya.ppi.pages;

import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;

@Telemetry("test.telemetry")
public interface TestPage {
    @Sensor("by-property-element")
    LocatorSupplier byPropertyLocator();
    @Sensor("by-property-with-param")
    LocatorSupplier byPropertyWithParamLocator(@Param("param") int p);
    @Sensor("$//by-value-element")
    LocatorSupplier byValueElement();
    @Sensor("$//xpath-determined")
    LocatorSupplier byXpathDetermined();
    @Sensor("$div.css-determined")
    LocatorSupplier byCssDetermined();
    @Sensor("$//div.first")
    LocatorSupplier withPostfix(PostfixLocator postfix);
    @Sensor(name = "Overrided")
    LocatorSupplier byMethodName();
}
