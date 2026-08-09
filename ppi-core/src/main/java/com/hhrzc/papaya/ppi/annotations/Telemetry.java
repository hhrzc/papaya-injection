package com.hhrzc.papaya.ppi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hhrzc
 * @date 2024/6/11 17:28
 * @description This annotation is used to mark a class as a Papaya unit, which can be injected by the Papaya framework.
 * The value of the annotation is the name of the telemetry file, which should match the filename of the unit file (with the .selectors extension) in the classpath.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Telemetry {
    String value() default "";
}
