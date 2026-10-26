package com.hhrzc.papaya.ppi.utils;

import com.hhrzc.papaya.ppi.model.PostfixLocator;
import com.microsoft.playwright.Locator;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class LocatorExtractor {

    private static final ThreadLocal<List<String>> CALLS = new ThreadLocal<>();

    @SuppressWarnings("PMD.UseProperClassLoader")
    private static final Locator RECORDER = (Locator) Proxy.newProxyInstance(
            Locator.class.getClassLoader(),
            new Class<?>[]{Locator.class},
            (proxy, method, args) -> {
                if (method.getDeclaringClass() == Object.class) {
                    return switch (method.getName()) {
                        case "toString" -> "LocatorExtractor";
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy.equals(args[0]);
                        default -> null;
                    };
                }
                CALLS.get().add(method.getName() + "(" + formatArgs(args) + ")");
                return method.getReturnType() == Locator.class ? proxy : null;
            });

    private LocatorExtractor() {
    }

    public static String extractLocator(PostfixLocator postfix) {
        List<String> calls = new ArrayList<>();
        CALLS.set(calls);
        try {
            postfix.apply(RECORDER);
            return String.join(".", calls);
        } finally {
            CALLS.remove();
        }
    }

    private static String formatArgs(Object ... args) {
        if (args == null) return "";
        return Arrays.stream(args)
                .map(a -> a instanceof String s ? "\"" + s + "\"" : String.valueOf(a))
                .collect(Collectors.joining(", "));
    }
}