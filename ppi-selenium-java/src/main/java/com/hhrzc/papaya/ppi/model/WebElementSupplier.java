package com.hhrzc.papaya.ppi.model;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.function.Function;

public interface WebElementSupplier extends Function<WebDriver, WebElement>, DataSupplier {

}
