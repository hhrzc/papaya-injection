package com.hhrzc.papaya.invocation;

import java.lang.annotation.Annotation;

/**
 * The locating annotation found on a page method, in a common form.
 *
 * <p>Every locating annotation ({@code @Sensor}, {@code @ByText}, {@code @ByRole}, ...) answers the same
 * questions, so the common code in {@link AbstractInvocationHandler} works with this record only and
 * never needs to know the concrete annotation type.
 *
 * @param annotation        the annotation itself; {@code createSupplier} uses it to decide HOW to locate
 * @param value             its {@code value()}: {@code ""}, {@code "$literal"} or a key in the .telemetry file
 * @param emptyMeansNoValue {@code true}: an empty value means "no value" (for {@code @ByRole}: no name);
 *                          {@code false}: the method name is the key
 * @param description       readable name for logs and reports; {@code ""} = method name
 */
public record SelectorSource(
        Annotation annotation,
        String value,
        boolean emptyMeansNoValue,
        String description
) {
}
