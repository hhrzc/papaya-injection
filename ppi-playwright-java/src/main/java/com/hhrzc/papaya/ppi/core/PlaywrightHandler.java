package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.invocation.AbstractInvocationHandler;
import com.hhrzc.papaya.invocation.SelectorSource;
import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.exceptions.ParamAnnotationMissingException;
import com.hhrzc.papaya.ppi.exceptions.SelectorMismatchKeyException;
import com.hhrzc.papaya.ppi.exceptions.UnexpectedParameterException;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;
import com.hhrzc.papaya.ppi.playwright.annotations.ByAltText;
import com.hhrzc.papaya.ppi.playwright.annotations.ByAttribute;
import com.hhrzc.papaya.ppi.playwright.annotations.ByLabel;
import com.hhrzc.papaya.ppi.playwright.annotations.ByPlaceholder;
import com.hhrzc.papaya.ppi.playwright.annotations.ByRole;
import com.hhrzc.papaya.ppi.playwright.annotations.ByTestId;
import com.hhrzc.papaya.ppi.playwright.annotations.ByText;
import com.hhrzc.papaya.ppi.playwright.annotations.ByTitle;
import com.hhrzc.papaya.ppi.playwright.annotations.Flag;
import com.hhrzc.papaya.ppi.utils.LocatorExtractor;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public class PlaywrightHandler extends AbstractInvocationHandler<Locator, LocatorSupplier, PostfixLocator> {

    public PlaywrightHandler(Properties selectors) {
        super(selectors);
    }

    @Override
    protected String injectParameters(Method method, Object[] args, String selector) {

        Parameter[] parameters = method.getParameters();
        if (args == null) {
            return selector;
        }

        String processedSelector = selector;
        for (int i = 0; i < args.length; i++) {
            Param paramAnnotation = parameters[i].getAnnotation(Param.class);
            if (paramAnnotation == null) {
                if ((args[i] instanceof String) || (args[i] instanceof Integer)) {
                    throw new ParamAnnotationMissingException(i);
                }

                if (getPostfixLocatorClass() != null && getPostfixLocatorClass().isInstance(args[i])) {
                    continue; //skip PostfixLocator parameters
                }

                throw new UnexpectedParameterException(
                        """
                                Unexpected parameter type at index %d in method with selector [%s].
                                Expected a String or Integer parameter with @Param annotation or a PostfixLocator.");
                                """.formatted(i, selector)
                );
            }

            String key = "{" + paramAnnotation.value() + "}";
            if (!processedSelector.contains(key)) {
                throw new SelectorMismatchKeyException(key, selector);
            }
            processedSelector = processedSelector.replace(key, String.valueOf(args[i]));
        }
        return processedSelector;
    }


    @Override
    protected LocatorSupplier createSupplier(Method method, SelectorSource source, String finalSelector,
                                             PostfixLocator postfix, Object... args) {
        Strategy strategy = strategy(source.annotation(), finalSelector);
        return new LocatorSupplier() {
            @Override
            public Locator apply(Page page) {
                if (page == null) {
                    throw new IllegalArgumentException("PPU Engine Fault: Page cannot be null when calling " + method.getName());
                }
                Locator locator = strategy.locate().apply(page);
                return postfix == null ? locator : postfix.apply(locator);
            }

            @Override
            public String getVariableName() {
                return displayName(method, source);
            }

            @Override
            public String getSelectorValue() {
                return strategy.description() + (postfix == null ? "" : " -> " + LocatorExtractor.extractLocator(postfix));
            }
        };
    }

    @Override
    protected Set<Class<? extends Annotation>> supportedAnnotations() {
        return Set.of(Sensor.class, ByText.class, ByLabel.class, ByPlaceholder.class, ByAltText.class,
                ByTitle.class, ByTestId.class, ByRole.class, ByAttribute.class);
    }

    // ---------------------------------------------------------------------------------------------
    // HOW to locate: one branch per annotation. The value is already resolved (file / literal / params).
    // Pattern.compile(...) runs here, once, so a wrong regex fails when the page method is called.
    // ---------------------------------------------------------------------------------------------

    /** How to build the Locator, and how to describe it for logs. */
    private record Strategy(Function<Page, Locator> locate, String description) {
    }

    private static Strategy strategy(Annotation annotation, String value) {
        if (annotation instanceof Sensor) {
            return new Strategy(page -> page.locator(value), value);
        }
        if (annotation instanceof ByText a) {
            Object text = text(value, a.regex());
            return new Strategy(page -> text instanceof Pattern pattern
                    ? page.getByText(pattern)
                    : page.getByText((String) text, new Page.GetByTextOptions().setExact(a.exact())),
                    describe("getByText", text, a.exact()));
        }
        if (annotation instanceof ByLabel a) {
            Object text = text(value, a.regex());
            return new Strategy(page -> text instanceof Pattern pattern
                    ? page.getByLabel(pattern)
                    : page.getByLabel((String) text, new Page.GetByLabelOptions().setExact(a.exact())),
                    describe("getByLabel", text, a.exact()));
        }
        if (annotation instanceof ByPlaceholder a) {
            Object text = text(value, a.regex());
            return new Strategy(page -> text instanceof Pattern pattern
                    ? page.getByPlaceholder(pattern)
                    : page.getByPlaceholder((String) text, new Page.GetByPlaceholderOptions().setExact(a.exact())),
                    describe("getByPlaceholder", text, a.exact()));
        }
        if (annotation instanceof ByAltText a) {
            Object text = text(value, a.regex());
            return new Strategy(page -> text instanceof Pattern pattern
                    ? page.getByAltText(pattern)
                    : page.getByAltText((String) text, new Page.GetByAltTextOptions().setExact(a.exact())),
                    describe("getByAltText", text, a.exact()));
        }
        if (annotation instanceof ByTitle a) {
            Object text = text(value, a.regex());
            return new Strategy(page -> text instanceof Pattern pattern
                    ? page.getByTitle(pattern)
                    : page.getByTitle((String) text, new Page.GetByTitleOptions().setExact(a.exact())),
                    describe("getByTitle", text, a.exact()));
        }
        if (annotation instanceof ByTestId a) {
            Object id = text(value, a.regex());
            return new Strategy(page -> id instanceof Pattern pattern
                    ? page.getByTestId(pattern)
                    : page.getByTestId((String) id),
                    describe("getByTestId", id, false));
        }
        if (annotation instanceof ByRole a) {
            return byRole(a, value);
        }
        if (annotation instanceof ByAttribute a) {
            String selector = attributeSelector(a.name(), value, a.exact());
            return new Strategy(page -> page.locator(selector), selector);
        }
        throw new IllegalStateException("PPI Fault: no strategy for @" + annotation.annotationType().getSimpleName());
    }

    private static Strategy byRole(ByRole a, String name) {
        Object accessibleName = name == null ? null : text(name, a.regex());
        List<String> parts = new ArrayList<>();
        parts.add(a.role().name());
        if (accessibleName != null) {
            parts.add("name=" + quote(accessibleName));
            if (a.exact() && !a.regex()) parts.add("exact");
        }
        if (a.level() > 0) parts.add("level=" + a.level());
        addFlag(parts, "checked", a.checked());
        addFlag(parts, "disabled", a.disabled());
        addFlag(parts, "expanded", a.expanded());
        addFlag(parts, "pressed", a.pressed());
        addFlag(parts, "selected", a.selected());
        if (a.includeHidden()) parts.add("includeHidden");

        return new Strategy(page -> page.getByRole(a.role(), roleOptions(a, accessibleName)),
                "getByRole(" + String.join(", ", parts) + ")");
    }

    /** A new options object per apply(): Playwright options are mutable, so they are never shared. */
    private static Page.GetByRoleOptions roleOptions(ByRole a, Object accessibleName) {
        Page.GetByRoleOptions options = new Page.GetByRoleOptions();
        if (accessibleName instanceof Pattern pattern) {
            options.setName(pattern);
        } else if (accessibleName != null) {
            options.setName((String) accessibleName);
            if (a.exact()) {
                options.setExact(true);
            }
        }
        if (a.level() > 0) {
            options.setLevel(a.level());
        }
        if (a.checked() != Flag.ANY) options.setChecked(a.checked() == Flag.TRUE);
        if (a.disabled() != Flag.ANY) options.setDisabled(a.disabled() == Flag.TRUE);
        if (a.expanded() != Flag.ANY) options.setExpanded(a.expanded() == Flag.TRUE);
        if (a.pressed() != Flag.ANY) options.setPressed(a.pressed() == Flag.TRUE);
        if (a.selected() != Flag.ANY) options.setSelected(a.selected() == Flag.TRUE);
        if (a.includeHidden()) {
            options.setIncludeHidden(true);
        }
        return options;
    }

    /** The value as a String, or as a compiled Pattern when regex = true. */
    private static Object text(String value, boolean regex) {
        return regex ? Pattern.compile(value) : value;
    }

    /** CSS attribute selector: [name='value'] or [name*='value']; quotes and backslashes are escaped. */
    static String attributeSelector(String attribute, String value, boolean exact) {
        if (attribute == null || attribute.isBlank()) {
            throw new IllegalArgumentException("PPI Fault: @ByAttribute name must not be blank");
        }
        String escaped = value.replace("\\", "\\\\").replace("'", "\\'");
        return "[" + attribute.trim() + (exact ? "=" : "*=") + "'" + escaped + "']";
    }

    private static String describe(String method, Object value, boolean exact) {
        return method + "(" + quote(value) + (exact && !(value instanceof Pattern) ? ", exact" : "") + ")";
    }

    private static String quote(Object value) {
        return value instanceof Pattern pattern ? "/" + pattern.pattern() + "/" : "\"" + value + "\"";
    }

    private static void addFlag(List<String> parts, String name, Flag flag) {
        if (flag != Flag.ANY) {
            parts.add(name + "=" + (flag == Flag.TRUE));
        }
    }

    @Override
    protected Class<PostfixLocator> getPostfixLocatorClass() {
        return PostfixLocator.class;
    }
}


