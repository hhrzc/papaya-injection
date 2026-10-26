package com.hhrzc.papaya.ppi.model;

import org.openqa.selenium.By;
import java.util.function.Function;

public enum BY {
    XPATH(By::xpath, "By.xpath"),
    CSS(By::cssSelector, "By.cssSelector"),
    ID(By::id, "By.id"),
    NAME(By::name, "By.name"),
    CLASS_NAME(By::className, "By.className"),
    TAG_NAME(By::tagName, "By.tagName"),
    LINK_TEXT(By::linkText, "By.linkText"),
    PARTIAL_LINK_TEXT(By::partialLinkText, "By.partialLinkText");

    private final Function<String, By> byFunction;
    private final String prefixVal;

    BY(Function<String, By> byFunction, String prefixValue){
        this.byFunction = byFunction;
        this.prefixVal = prefixValue;
    }

    public Function<String, By> by(){
        return byFunction;
    }

    public String prefixValue(){
        return prefixVal;
    }
}
