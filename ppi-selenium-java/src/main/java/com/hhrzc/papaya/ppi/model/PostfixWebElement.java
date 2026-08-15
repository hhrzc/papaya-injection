package com.hhrzc.papaya.ppi.model;

import org.openqa.selenium.WebElement;

import java.util.function.UnaryOperator;

/**
 * @author hhrzc
 * @since 2025/6/17 17:45
 */
@FunctionalInterface
public interface PostfixWebElement extends UnaryOperator<WebElement> {
}