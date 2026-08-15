package com.hhrzc.papaya.ppi.model;

import org.openqa.selenium.By;
import java.util.function.Function;

public enum BY {
    XPATH(By::xpath),
    CSS(By::cssSelector),
    ID(By::id),
    NAME(By::name),
    CLASS_NAME(By::className),
    TAG_NAME(By::tagName),
    LINK_TEXT(By::linkText),
    PARTIAL_LINK_TEXT(By::partialLinkText);

    private final Function<String, By> byFunction;

    BY(Function<String, By> byFunction){
        this.byFunction = byFunction;
    }

    public Function<String, By> by(){
        return byFunction;
    }
}
