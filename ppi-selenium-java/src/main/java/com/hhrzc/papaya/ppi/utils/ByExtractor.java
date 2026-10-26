package com.hhrzc.papaya.ppi.utils;

import com.hhrzc.papaya.ppi.model.PostfixWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ByExtractor {

    private final static ThreadLocal<List<String>> CALLS = new ThreadLocal<>();

    @SuppressWarnings("PMD.UseProperClassLoader")
    private static final WebElement RECORDER = (WebElement) Proxy.newProxyInstance(
            WebElement.class.getClassLoader(),
            new Class<?>[]{WebElement.class},
            (proxy, method, args) -> {
                if (method.getDeclaringClass() == Object.class) {
                    return switch (method.getName()) {
                        case "toString" -> "ByExtractor";
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy.equals(args[0]);
                        default -> null;
                    };
                }
                if (args != null) {
                    for (Object arg : args) {
                        if (arg instanceof By) {
                            CALLS.get().add(arg.toString());
                        }
                    }
                }
                Class<?> type = method.getReturnType();
                if (type == WebElement.class) return proxy;      // findElement(...) → chain continues
                if (type == List.class) return Collections.nCopies(100, proxy);   // findElements(...).get(i) works                return null;
                return null;
            });

    public static String extractByFromPostfix(PostfixWebElement postfix) {
        if (postfix == null) return null;                    // no postfix → no description
        if (postfix instanceof PostfixWebElement.Named) {
            return postfix.postfixValue();                   // user's own text wins
        }
        List<String> calls = new ArrayList<>();
        CALLS.set(calls);
        try {
            postfix.apply(RECORDER);
            return calls.isEmpty() ? "custom" : String.join(", ", calls);
        } finally {
            CALLS.remove();
        }
    }

}

