package com.hhrzc.papaya.ppi.model;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.function.Function;

/**
 * @author hhrzc
 * @date 2024/6/17 17:45
 */
@FunctionalInterface
public interface LocatorSupplier extends Function<Page, Locator> {
}
