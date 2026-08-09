package com.hhrzc.papaya.ppi.model;

import com.microsoft.playwright.Locator;
import java.util.function.UnaryOperator;

/**
 * @author hhrzc
 * @date 2024/6/17 17:45
 */
@FunctionalInterface
public interface PostfixLocator extends UnaryOperator<Locator> {
}
