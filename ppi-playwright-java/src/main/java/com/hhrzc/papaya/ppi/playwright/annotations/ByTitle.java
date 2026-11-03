package com.hhrzc.papaya.ppi.playwright.annotations;

import com.hhrzc.papaya.ppi.annotations.Locating;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Finds elements by their {@code title} attribute, using Playwright's {@code page.getByTitle(...)}.
 *
 * <p>{@link #value()} works like in {@code @Sensor}:
 * <ul>
 *   <li>{@code ""} (default) - the method name is the key in the .telemetry file</li>
 *   <li>{@code "$..."} - a literal value, written directly in the annotation</li>
 *   <li>anything else - a key in the .telemetry file</li>
 * </ul>
 * Placeholders like {@code {name}} are filled from {@code @Param} parameters.
 *
 * <pre>{@code
 * @ByTitle("$Issues count")
 * LocatorSupplier issuesCounter();
 * }</pre>
 */
@Locating
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByTitle {

    /** Key in the .telemetry file, or {@code "$literal"}; {@code ""} = method name as key. */
    String value() default "";

    /**
     * {@code false} (Playwright default): case-insensitive, matches a substring.
     * {@code true}: case-sensitive, matches the whole string. Ignored when {@link #regex()} is true.
     */
    boolean exact() default false;

    /** {@code true}: the value is a regular expression ({@code Pattern.compile(value)}). */
    boolean regex() default false;

    /** Readable name for logs and reports; {@code ""} = method name. */
    String description() default "";
}
