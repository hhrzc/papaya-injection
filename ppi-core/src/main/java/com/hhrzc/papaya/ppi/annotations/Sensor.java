package com.hhrzc.papaya.ppi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hhrzc
 * @since 2025/6/11 17:30
 * This annotation is used to mark a method as a telemetry method, which can be used to collect telemetry data for the Papaya framework.
 * The value of the annotation might be:
 * - a key that corresponds to a selector in the unit file, for example:
 * <pre> {@code
 *     @Telemetry("submitButton")
 *     LocatorSupplier clickSubmitButton();
 * }</pre>
 * In this case, the selector will be looked up in the unit file using the key "submitButton".
 * - a literal selector string prefixed with "$" (e.g., "$button[name='Submit']" for a selector defined directly in the annotation), or
 * - an empty string, in which case the method name will be used as the key to look up the selector in the unit
 * For example, if the selector is "button[name='btnName']" then the method can be defined as:
 * <pre>{@code
 *     @Telemetry("$button[name='btnName']")
 *     LocatorSupplier clickButton();
 * }</pre>
 *
 * For the empty string case, if the method name is "clickSubmitButton", then the selector will be looked up in the unit file using the key "clickSubmitButton=path-to-btn".
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensor {
    String value() default "";
}
