package com.hhrzc.papaya.ppi.playwright.annotations;

import com.hhrzc.papaya.ppi.annotations.Locating;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Finds elements by their test id, using Playwright's {@code page.getByTestId(...)}.
 *
 * <p>The attribute is {@code data-testid} by default. Playwright changes it globally, for the whole
 * Playwright instance: {@code playwright.selectors().setTestIdAttribute("data-pw")}.
 * Test ids always match exactly, so there is no {@code exact} option.
 *
 * <p>{@link #value()} works like in {@code @Sensor}: {@code ""} = method name as key,
 * {@code "$..."} = literal, anything else = key in the .telemetry file.
 *
 * <pre>{@code
 * @ByTestId("$submit-button")
 * LocatorSupplier submit();
 * }</pre>
 */
@Locating
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByTestId {

    /** Key in the .telemetry file, or {@code "$literal"}; {@code ""} = method name as key. */
    String value() default "";

    /** {@code true}: the value is a regular expression ({@code Pattern.compile(value)}). */
    boolean regex() default false;

    /** Readable name for logs and reports; {@code ""} = method name. */
    String description() default "";
}
