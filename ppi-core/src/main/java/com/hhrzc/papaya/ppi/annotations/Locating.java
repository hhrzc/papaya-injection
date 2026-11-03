package com.hhrzc.papaya.ppi.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation: marks an annotation as a <b>locating annotation</b>, like {@link Sensor},
 * {@code @ByText} or {@code @ByRole}.
 *
 * <p>The library finds every annotation marked with {@code @Locating} on a page method, so a method
 * must have exactly one of them. A locating annotation must have a {@code String value()} attribute;
 * a readable name for logs is read from {@code String description()} (or {@code name()} for {@link Sensor}).
 *
 * <p>Users never write {@code @Locating} themselves; it is placed on the library's annotations.
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Locating {

    /**
     * What an empty {@code value()} means:
     * <ul>
     *   <li>{@code false} (default): the method name is the key in the .telemetry file (like {@link Sensor})</li>
     *   <li>{@code true}: no value at all (for {@code @ByRole}: search by role only, without a name)</li>
     * </ul>
     */
    boolean emptyMeansNoValue() default false;
}
