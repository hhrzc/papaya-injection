package com.hhrzc.papaya.ppi.tests.pages;


import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.core.PpiRepository;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;

@SuppressWarnings("all")
@Telemetry("test_page.telemetry")
public interface TestPage extends PpiRepository {

    @Sensor
    LocatorSupplier byMethodName();

    @Sensor("$by-sensor-value")
    LocatorSupplier bySensorValue();

    @Sensor("by-sensor-property")
    LocatorSupplier bySensorProperty();

    @Sensor("$button[class*=[{btnName}]")
    LocatorSupplier methodParameters(@Param("btnName") String classAttr);

    @Sensor("$bar")
    LocatorSupplier paramAnnotationMissing(String classAttr);
    LocatorSupplier sensorAnnotationMissing(String classAttr);

    @Sensor("$button[class*=[{btnName}]")
    LocatorSupplier postfixParameterTest(@Param("btnName") String classAttr, PostfixLocator postfix);
}
