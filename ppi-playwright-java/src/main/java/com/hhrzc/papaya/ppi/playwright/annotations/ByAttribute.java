package com.hhrzc.papaya.ppi.playwright.annotations;

import com.hhrzc.papaya.ppi.annotations.Locating;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Finds elements by any HTML attribute. Playwright has no {@code getByAttribute}, so Papaya builds a
 * CSS attribute selector and calls {@code page.locator(...)}:
 * <ul>
 *   <li>{@code exact = true} (default): {@code [name='email']}</li>
 *   <li>{@code exact = false}: {@code [name*='email']} (contains)</li>
 * </ul>
 *
 * <p>{@link #value()} is the attribute value: {@code ""} = method name as key,
 * {@code "$..."} = literal, anything else = key in the .telemetry file.
 *
 * <pre>{@code
 * @ByAttribute(name = "name", value = "$email")
 * LocatorSupplier emailInput();                 // [name='email']
 *
 * @ByAttribute(name = "href", value = "$/products", exact = false)
 * LocatorSupplier productsLink();               // [href*='/products']
 * }</pre>
 */
@Locating
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByAttribute {

    /** The attribute name: {@code name}, {@code href}, {@code type}, {@code aria-label}, ... */
    String name();

    /** The attribute value: key in the .telemetry file, or {@code "$literal"}; {@code ""} = method name as key. */
    String value() default "";

    /**
     * {@code true} (default): the attribute equals the value ({@code [a='v']}).
     * {@code false}: the attribute contains the value ({@code [a*='v']}).
     */
    boolean exact() default true;

    /** Readable name for logs and reports; {@code ""} = method name. */
    String description() default "";
}
