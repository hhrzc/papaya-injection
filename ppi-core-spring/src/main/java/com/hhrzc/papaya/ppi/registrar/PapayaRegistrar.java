package com.hhrzc.papaya.ppi.registrar;

import com.hhrzc.papaya.ppi.annotations.ConfigTelemetry;
import com.hhrzc.papaya.ppi.core.PapayaFactoryBean;
import com.hhrzc.papaya.ppi.core.PpiRepository;
import com.hhrzc.papaya.ppi.exceptions.TelemetryConfigurationMissingException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

/**
 * @author hhrzc
 * @since 2025/6/17 17:45
 * Registrar for Papaya framework.
 * Scans the specified packages for interfaces that extend PpiRepository and registers
 * them as beans in the Spring context using PapayaFactoryBean to create proxy instances
 */
public class PapayaRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Set<BeanDefinition> definitionSet;
    private Environment environment;

    public PapayaRegistrar() {
        definitionSet = new HashSet<>();
    }

    /**
     * {@link ConfigTelemetry} handler.
     * Scans the specified packages for interfaces that extend PpiRepository and registers
     * them as beans in the Spring context using PapayaFactoryBean to create proxy instances.
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @NonNull BeanDefinitionRegistry registry) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(ConfigTelemetry.class.getName());
        if(attrs == null){
            throw new TelemetryConfigurationMissingException("Missing @ConfigTelemetry annotation on configuration class. " +
                    "Please add @ConfigTelemetry to your configuration class and specify the package(s) to scan for telemetry interfaces.");
        }
        String stage = defineStage(attrs);
        String[] basePackages = (String[]) attrs.get("value");
        List<String> cleaned = Arrays.stream(basePackages)
                .map(p -> p.replace('/', '.').replaceAll("^\\.+|\\.+$", ""))   // "com/x/" -> "com.x"
                .distinct()
                .sorted(Comparator.comparingInt(String::length))                 // parents first
                .toList();

        List<String> excludedSubPackages = new ArrayList<>();

        //exclude subpackages to avoid scanning the same interfaces multiple times
        for (String pkg : cleaned) {
            boolean isSubPackage = excludedSubPackages.stream()
                    .anyMatch(parent -> pkg.equals(parent) || pkg.startsWith(parent + "."));

            if (!isSubPackage) {
                excludedSubPackages.add(pkg);
            }
        }

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface();
            }
        };
        scanner.addIncludeFilter(new AssignableTypeFilter(PpiRepository.class));

        for (String pkg : excludedSubPackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(pkg);
            if(candidateComponents.isEmpty()){
                throw new IllegalStateException("No interfaces found in package: " + pkg + ". " +
                        "Ensure that the package is correct and contains interfaces that extend PpiRepository.");
            }
            for (BeanDefinition bd : candidateComponents) {
                if (definitionSet.contains(bd)) {
                    continue;
                }

                if (bd == null || bd.getBeanClassName() == null) {
                    continue;
                }

                if (registry.containsBeanDefinition(bd.getBeanClassName())) {
                    continue;
                }

                definitionSet.add(bd);
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PapayaFactoryBean.class);
                builder.addConstructorArgValue(bd.getBeanClassName());
                builder.addConstructorArgValue(determineInvocationHandlerClass());
                builder.addConstructorArgValue(stage);
                registry.registerBeanDefinition(bd.getBeanClassName(), builder.getBeanDefinition());
            }
        }
    }

    private String defineStage(Map<String, Object> attrs) {
        String stageFrom = (String) attrs.get("stageFrom");
        if (stageFrom == null || stageFrom.isBlank()) {
            return null;                                    // stages are off
        }
        String stage = environment.getProperty(stageFrom.trim());
        return (stage == null || stage.isBlank()) ? null : stage.trim();
    }

    /**
     * Known limitation (see GitHub issue #1): one invocation handler per application context.
     * Every META-INF/papaya.properties on the classpath declares a handler. If both
     * ppi-playwright-spring and ppi-selenium-spring are present, we fail fast instead of
     * silently giving all @Telemetry interfaces the same handler.
     */
    private Class<? extends InvocationHandler> determineInvocationHandlerClass() {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Properties props = PropertiesLoaderUtils.loadAllProperties("META-INF/papaya.properties", cl);
            String handlerClassName = props.getProperty("papaya.handler");

            if (handlerClassName == null || handlerClassName.isEmpty()) {
                throw new IllegalStateException(
                        "No 'papaya.handler' found in META-INF/papaya.properties. " +
                                "Ensure ppi-playwright-spring or ppi-selenium-spring is in your dependencies.");
            }
            return (Class<? extends InvocationHandler>) Class.forName(handlerClassName, true, cl);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to initialize Papaya Telemetry handler", e);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
