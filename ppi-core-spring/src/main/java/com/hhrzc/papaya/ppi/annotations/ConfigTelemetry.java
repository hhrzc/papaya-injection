package com.hhrzc.papaya.ppi.annotations;

import com.hhrzc.papaya.ppi.registrar.PapayaRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hhrzc
 * @since 2025/6/17 17:44
 * This annotation is used to mark a package on configuration class.
 * Included to increase a speed of scanning telemetry interfaces by the Papaya framework
 * and to avoid scanning the whole classpath.
 * The value of the annotation is the path to the package where the telemetry interfaces are
 * located, which should be scanned by the Papaya framework to find the telemetry interfaces
 * and register them as beans in the Spring context.
 * For example, if the telemetry interfaces are located in the package "com.example.telemetry",
 * then the configuration class can be defined as:
 * <pre>{@code
 *     @Configuration
 *     @ConfigTelemetry("com.example.telemetry")
 *     public class AppConfig {
 *     }
 * }</pre>
 *
 * In this case, the Papaya framework will scan the "com.example.telemetry" package to find the
 * telemetry interfaces and register them as beans in the Spring context, allowing them to be
 * injected into other components of the application.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PapayaRegistrar.class)
public @interface ConfigTelemetry {
    String[] value();
    String stageFrom() default "";
}
