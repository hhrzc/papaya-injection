package com.hhrzc.papaya.ppi.model;

import org.openqa.selenium.WebElement;

import java.util.function.UnaryOperator;

/**
 * @author hhrzc
 * @date 2024/6/17 17:45
 */
@FunctionalInterface
public interface PostfixWebElement extends UnaryOperator<WebElement> {
}
