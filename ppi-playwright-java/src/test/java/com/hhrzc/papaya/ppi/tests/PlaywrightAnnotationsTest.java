package com.hhrzc.papaya.ppi.tests;

import com.hhrzc.papaya.ppi.core.LocatorIgniter;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.pages.AnnotationsPage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Playwright locating annotations: each one must call the right Playwright method with the right options.
 * annotations-page.telemetry:
 * <pre>
 *   emailInput=Email address
 *   sign-in-button=Sign in
 *   error-message=Invalid email or password
 * </pre>
 */
class PlaywrightAnnotationsTest {

    private Page page;
    private AnnotationsPage annotationsPage;

    @BeforeEach
    void init() {
        page = mock(Page.class);
        annotationsPage = LocatorIgniter.ignite(AnnotationsPage.class);
    }

    // ---------- @ByLabel ----------

    @Test
    void byLabel_emptyValue_usesMethodNameAsKey() {
        ArgumentCaptor<Page.GetByLabelOptions> options = ArgumentCaptor.forClass(Page.GetByLabelOptions.class);

        annotationsPage.emailInput().apply(page);

        verify(page).getByLabel(eq("Email address"), options.capture());
        Assertions.assertNotEquals(Boolean.TRUE, options.getValue().exact);
    }

    @Test
    void byLabel_exact() {
        ArgumentCaptor<Page.GetByLabelOptions> options = ArgumentCaptor.forClass(Page.GetByLabelOptions.class);

        annotationsPage.passwordInput().apply(page);

        verify(page).getByLabel(eq("Password"), options.capture());
        Assertions.assertEquals(Boolean.TRUE, options.getValue().exact);
    }

    // ---------- @ByText ----------

    @Test
    void byText_keyFromFile() {
        annotationsPage.errorMessage().apply(page);

        verify(page).getByText(eq("Invalid email or password"), any(Page.GetByTextOptions.class));
    }

    @Test
    void byText_paramAndExact() {
        ArgumentCaptor<Page.GetByTextOptions> options = ArgumentCaptor.forClass(Page.GetByTextOptions.class);

        annotationsPage.welcomeMessage("John").apply(page);

        verify(page).getByText(eq("Welcome, John"), options.capture());
        Assertions.assertEquals(Boolean.TRUE, options.getValue().exact);
    }

    @Test
    void byText_regex_usesPattern() {
        ArgumentCaptor<Pattern> pattern = ArgumentCaptor.forClass(Pattern.class);

        annotationsPage.sessionExpired().apply(page);

        verify(page).getByText(pattern.capture());
        Assertions.assertEquals("^Session expired", pattern.getValue().pattern());
    }

    // ---------- @ByPlaceholder / @ByAltText / @ByTitle / @ByTestId ----------

    @Test
    void byPlaceholder() {
        annotationsPage.emailByPlaceholder().apply(page);
        verify(page).getByPlaceholder(eq("name@example.com"), any(Page.GetByPlaceholderOptions.class));
    }

    @Test
    void byAltText() {
        annotationsPage.logo().apply(page);
        verify(page).getByAltText(eq("Company logo"), any(Page.GetByAltTextOptions.class));
    }

    @Test
    void byTitle() {
        annotationsPage.showPassword().apply(page);
        verify(page).getByTitle(eq("Show password"), any(Page.GetByTitleOptions.class));
    }

    @Test
    void byTestId() {
        annotationsPage.loginForm().apply(page);
        verify(page).getByTestId("login-form");
    }

    // ---------- @ByRole ----------

    @Test
    void byRole_nameFromFile_andDescription() {
        ArgumentCaptor<Page.GetByRoleOptions> options = ArgumentCaptor.forClass(Page.GetByRoleOptions.class);
        LocatorSupplier supplier = annotationsPage.signInButton();

        supplier.apply(page);

        verify(page).getByRole(eq(AriaRole.BUTTON), options.capture());
        Assertions.assertEquals("Sign in", options.getValue().name);
        Assertions.assertEquals("Sign In button", supplier.getVariableName());
        Assertions.assertEquals("getByRole(BUTTON, name=\"Sign in\")", supplier.getSelectorValue());
    }

    @Test
    void byRole_level() {
        ArgumentCaptor<Page.GetByRoleOptions> options = ArgumentCaptor.forClass(Page.GetByRoleOptions.class);

        annotationsPage.pageHeading().apply(page);

        verify(page).getByRole(eq(AriaRole.HEADING), options.capture());
        Assertions.assertEquals("Welcome back", options.getValue().name);
        Assertions.assertEquals(1, options.getValue().level);
    }

    @Test
    void byRole_flagAndExact() {
        ArgumentCaptor<Page.GetByRoleOptions> options = ArgumentCaptor.forClass(Page.GetByRoleOptions.class);

        annotationsPage.rememberMeChecked().apply(page);

        verify(page).getByRole(eq(AriaRole.CHECKBOX), options.capture());
        Assertions.assertEquals(Boolean.TRUE, options.getValue().checked);
        Assertions.assertEquals(Boolean.TRUE, options.getValue().exact);
        Assertions.assertNull(options.getValue().disabled);           // Flag.ANY = not set
    }

    @Test
    void byRole_nameFromParam() {
        ArgumentCaptor<Page.GetByRoleOptions> options = ArgumentCaptor.forClass(Page.GetByRoleOptions.class);

        annotationsPage.socialLogin("Continue with Google").apply(page);

        verify(page).getByRole(eq(AriaRole.BUTTON), options.capture());
        Assertions.assertEquals("Continue with Google", options.getValue().name);
    }

    @Test
    void byRole_emptyValue_meansNoName() {
        ArgumentCaptor<Page.GetByRoleOptions> options = ArgumentCaptor.forClass(Page.GetByRoleOptions.class);

        annotationsPage.listItems().apply(page);                     // no key "listItems" in the file: not looked up

        verify(page).getByRole(eq(AriaRole.LISTITEM), options.capture());
        Assertions.assertNull(options.getValue().name);
    }

    @Test
    void byRole_withPostfix() {
        Locator all = mock(Locator.class);
        Locator second = mock(Locator.class);
        when(page.getByRole(eq(AriaRole.LISTITEM), any(Page.GetByRoleOptions.class))).thenReturn(all);
        when(all.nth(1)).thenReturn(second);
        LocatorSupplier supplier = annotationsPage.listItem(l -> l.nth(1));

        Assertions.assertSame(second, supplier.apply(page));
        Assertions.assertEquals("getByRole(LISTITEM) -> nth(1)", supplier.getSelectorValue());
    }

    @Test
    void byRole_regexName() {
        ArgumentCaptor<Page.GetByRoleOptions> options = ArgumentCaptor.forClass(Page.GetByRoleOptions.class);

        annotationsPage.forgotLink().apply(page);

        verify(page).getByRole(eq(AriaRole.LINK), options.capture());
        Assertions.assertInstanceOf(Pattern.class, options.getValue().name);
        Assertions.assertEquals("^Forgot", ((Pattern) options.getValue().name).pattern());
    }

    // ---------- @ByAttribute ----------

    @Test
    void byAttribute_exact() {
        annotationsPage.csrfToken().apply(page);
        verify(page).locator("[name='csrf_token']");
    }

    @Test
    void byAttribute_contains() {
        annotationsPage.registerLink().apply(page);
        verify(page).locator("[href*='/register']");
    }

    @Test
    void byAttribute_escapesQuotes() {
        annotationsPage.quotedValue().apply(page);
        verify(page).locator("[title='it\\'s']");
    }

    // ---------- defaults and mistakes ----------

    @Test
    void variableName_defaultsToMethodName() {
        Assertions.assertEquals("pageHeading", annotationsPage.pageHeading().getVariableName());
    }

    @Test
    void twoLocatingAnnotations_areRejected() {
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class,
                annotationsPage::twoAnnotations);
        Assertions.assertTrue(e.getMessage().contains("more than one locating annotation"), e.getMessage());
    }

    @Test
    void sensorTogetherWithByText_isRejected() {
        Assertions.assertThrows(IllegalStateException.class, annotationsPage::sensorAndByText);
    }
}
