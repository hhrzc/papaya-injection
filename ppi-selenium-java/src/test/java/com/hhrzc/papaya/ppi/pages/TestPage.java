package com.hhrzc.papaya.ppi.pages;

import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.model.BY;
import com.hhrzc.papaya.ppi.model.PostfixWebElement;
import com.hhrzc.papaya.ppi.model.WebElementSupplier;

@Telemetry("test.telemetry")
public interface TestPage {
    @Sensor("by-property-element")
    WebElementSupplier byPropertyElement();
    @Sensor("by-property-with-param")
    WebElementSupplier byPropertyElement(@Param("param") int p);
    @Sensor("$//by-value-element")
    WebElementSupplier byValueElement();
    @Sensor("$by-by-element")
    WebElementSupplier byByElement(BY by);
    @Sensor("$//xpath-determined")
    WebElementSupplier byXpathDetermined();
    @Sensor("$div.css-determined")
    WebElementSupplier byCssDetermined();
    @Sensor("$//div.first")
    WebElementSupplier withPostfix(PostfixWebElement postfix);
    @Sensor("$//div[{x}]")
    WebElementSupplier secondParamNotAnnotated(@Param("x") String x, String y);
}
