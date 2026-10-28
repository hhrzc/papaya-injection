package com.hhrzc.papaya.ppi.pages;

import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
import com.hhrzc.papaya.ppi.model.BY;
import com.hhrzc.papaya.ppi.model.PostfixWebElement;
import com.hhrzc.papaya.ppi.model.WebElementSupplier;
import com.hhrzc.papaya.ppi.model.WebElementsSupplier;

@Telemetry
public interface ListPage {
    @Sensor("$//li")
    WebElementSupplier firstItem();

    @Sensor("$//li")
    WebElementsSupplier allItems();

    @Sensor("$li.{type}")
    WebElementsSupplier itemsByType(@Param("type") String type);

    @Sensor("$item")
    WebElementsSupplier itemsByClassName(BY by);

    @Sensor("$//li")
    WebElementsSupplier itemsWithPostfix(PostfixWebElement postfix);
}
