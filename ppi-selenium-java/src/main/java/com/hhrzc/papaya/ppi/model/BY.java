package com.hhrzc.papaya.ppi.model;

import org.openqa.selenium.By;
import java.util.function.Function;

public enum BY {
    XPATH(By::xpath),
    CSS(By::cssSelector);

    private final Function<String, By> byFunction;

    BY(Function<String, By> byFunction){
        this.byFunction = byFunction;
    }

    public Function<String, By> by(){
        return byFunction;
    }
}
