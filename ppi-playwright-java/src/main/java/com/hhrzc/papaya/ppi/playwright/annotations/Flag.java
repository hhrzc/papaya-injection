package com.hhrzc.papaya.ppi.playwright.annotations;

/**
 * A three-state option for annotation attributes.
 *
 * <p>Java annotations can't use {@code null} as a default, so a plain {@code boolean}
 * can't say "not set". Playwright options like {@code checked} have three meanings:
 * <ul>
 *   <li>{@link #ANY} - option not set, matches both states (default)</li>
 *   <li>{@link #TRUE} - only elements where the state is true</li>
 *   <li>{@link #FALSE} - only elements where the state is false</li>
 * </ul>
 */
public enum Flag {
    ANY,
    TRUE,
    FALSE
}
