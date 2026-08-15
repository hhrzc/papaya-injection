package com.hhrzc.papaya.ppi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hhrzc
 * @since 2025/6/11 17:29
 * This annotation is used to mark a method parameter as a placeholder for a value that will be injected by the Papaya framework.
 * The value of the annotation is the name of the placeholder in the selector.
 * For example, if the selector is "button[name='Submit']"
 * then the method can be defined as:
 * <pre>{@code
 *     @Telemetry("button[name='{btnName}']")
 *     LocatorSupplier clickButton(@Param("btnName") String buttonName);
 * }</pre>
 * In this case, the value of the "btnName" parameter will be injected into the selector at runtime, replacing the "{btnName}" placeholder with the actual value of the parameter.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * The name of the placeholder in the selector (e.g., "btnName" for {btnName}).
     */
    String value();
}
