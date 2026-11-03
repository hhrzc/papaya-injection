package com.hhrzc.papaya.ppi.playwright.annotations;

import com.microsoft.playwright.options.AriaRole;

import com.hhrzc.papaya.ppi.annotations.Locating;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Finds elements by their ARIA role and accessible name, using Playwright's
 * {@code page.getByRole(role, options)}. This is how assistive technology sees the page,
 * and Playwright's recommended way to locate elements.
 *
 * <p>{@link #value()} is the <b>accessible name</b> (for a button: its text or {@code aria-label}).
 * Unlike {@code @Sensor}, {@code ""} here means <b>no name</b>: many role searches don't need one,
 * for example all list items. Otherwise {@code "$..."} = literal, anything else = key in the .telemetry file.
 *
 * <p>All other attributes are Playwright's {@code GetByRoleOptions}. Their defaults mean "not set",
 * exactly like Playwright's own defaults.
 *
 * <pre>{@code
 * @ByRole(role = AriaRole.BUTTON, value = "$Sign in")
 * LocatorSupplier signIn();
 *
 * @ByRole(role = AriaRole.HEADING, value = "products-heading", level = 2)
 * LocatorSupplier productsHeading();
 *
 * @ByRole(role = AriaRole.CHECKBOX, value = "$Remember me", checked = Flag.TRUE)
 * LocatorSupplier rememberMeChecked();
 *
 * @ByRole(role = AriaRole.LISTITEM)
 * LocatorSupplier allListItems();
 * }</pre>
 */
@Locating(emptyMeansNoValue = true)
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ByRole {

    /** The ARIA role: BUTTON, LINK, TEXTBOX, CHECKBOX, HEADING, LISTITEM, ... */
    AriaRole role();

    /** Accessible name: key in the .telemetry file, or {@code "$literal"}; {@code ""} = no name. */
    String value() default "";

    /**
     * {@code false} (Playwright default): name matches case-insensitive, as a substring.
     * {@code true}: case-sensitive, whole string. Ignored when {@link #regex()} is true.
     */
    boolean exact() default false;

    /** {@code true}: the accessible name is a regular expression ({@code Pattern.compile(value)}). */
    boolean regex() default false;

    /** Heading level 1..6 ({@code <h1>} = 1); {@code 0} = not set. */
    int level() default 0;

    /** Checked state of checkboxes and radio buttons; {@link Flag#ANY} = not set. */
    Flag checked() default Flag.ANY;

    /** Disabled state; {@link Flag#ANY} = not set. */
    Flag disabled() default Flag.ANY;

    /** Expanded state ({@code aria-expanded}); {@link Flag#ANY} = not set. */
    Flag expanded() default Flag.ANY;

    /** Pressed state of toggle buttons ({@code aria-pressed}); {@link Flag#ANY} = not set. */
    Flag pressed() default Flag.ANY;

    /** Selected state ({@code aria-selected}); {@link Flag#ANY} = not set. */
    Flag selected() default Flag.ANY;

    /** {@code true}: also match elements hidden from assistive technology. Playwright default: false. */
    boolean includeHidden() default false;

    /** Readable name for logs and reports; {@code ""} = method name. */
    String description() default "";
}
