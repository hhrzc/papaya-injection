package com.hhrzc.papaya.ppi.model;

import com.microsoft.playwright.Locator;
import java.util.function.UnaryOperator;

/**
 * @author hhrzc
 * @since 2025/6/17 17:45
 */
@FunctionalInterface
public interface PostfixLocator extends UnaryOperator<Locator> {
}
