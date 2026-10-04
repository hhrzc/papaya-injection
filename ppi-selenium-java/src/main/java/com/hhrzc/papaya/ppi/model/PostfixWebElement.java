package com.hhrzc.papaya.ppi.model;

import org.openqa.selenium.WebElement;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * @author hhrzc
 * @since 2025/6/17 17:45
 */
@FunctionalInterface
public interface PostfixWebElement extends UnaryOperator<WebElement> {
    default String postfixValue(){
        return "/postfix_undefined";
    }

    static PostfixWebElement defineSelectorValue(String postfixValue, UnaryOperator<WebElement> delegate) {
        Objects.requireNonNull(postfixValue, "name");
        Objects.requireNonNull(delegate, "delegate");
        return new Named(postfixValue, delegate);
    }

    record Named(String name, UnaryOperator<WebElement> delegate) implements PostfixWebElement {


        @Override
        public String postfixValue() {
            return name;
        }

        @Override
        public WebElement apply(WebElement webElement) {
            return delegate.apply(webElement);
        }
    }
}