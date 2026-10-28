package com.hhrzc.papaya.ppi.model;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Function;

/**
 * Supplier for ALL elements matching the selector: {@code driver.findElements(...)}.
 * Returns an empty list if there are none (unlike {@link WebElementSupplier}, which throws).
 *
 * <pre>{@code
 * @Sensor("product-card")
 * WebElementsSupplier allProducts();
 *
 * List<WebElement> products = productsPage.allProducts().apply(driver);
 * }</pre>
 *
 * Postfixes are not supported for lists.
 */
public interface WebElementsSupplier extends Function<WebDriver, List<WebElement>>, DataSupplier {
}
