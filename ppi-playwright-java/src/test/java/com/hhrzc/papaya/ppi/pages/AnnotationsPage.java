package com.hhrzc.papaya.ppi.pages;

import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.annotations.Sensor;
import com.hhrzc.papaya.ppi.annotations.Telemetry;
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
import com.microsoft.playwright.options.AriaRole;

@Telemetry("annotations-page.telemetry")
public interface AnnotationsPage {

    // ---------- text-like ----------
    @ByLabel
    LocatorSupplier emailInput();

    @ByLabel(value = "$Password", exact = true)
    LocatorSupplier passwordInput();

    @ByText("error-message")
    LocatorSupplier errorMessage();

    @ByText(value = "$Welcome, {user}", exact = true)
    LocatorSupplier welcomeMessage(@Param("user") String user);

    @ByText(value = "$^Session expired", regex = true)
    LocatorSupplier sessionExpired();

    @ByText(value = "${text}", exact = true)
    LocatorSupplier byText(@Param("text") String text);

    @ByPlaceholder("$name@example.com")
    LocatorSupplier emailByPlaceholder();

    @ByAltText("$Company logo")
    LocatorSupplier logo();

    @ByTitle("$Show password")
    LocatorSupplier showPassword();

    @ByTestId("$login-form")
    LocatorSupplier loginForm();

    // ---------- role ----------
    @ByRole(role = AriaRole.BUTTON, value = "sign-in-button", description = "Sign In button")
    LocatorSupplier signInButton();

    @ByRole(role = AriaRole.HEADING, value = "$Welcome back", level = 1)
    LocatorSupplier pageHeading();

    @ByRole(role = AriaRole.CHECKBOX, value = "$Remember me", checked = Flag.TRUE, exact = true)
    LocatorSupplier rememberMeChecked();

    @ByRole(role = AriaRole.BUTTON, value = "${provider}")
    LocatorSupplier socialLogin(@Param("provider") String provider);

    @ByRole(role = AriaRole.LISTITEM)
    LocatorSupplier listItems();

    @ByRole(role = AriaRole.LISTITEM)
    LocatorSupplier listItem(PostfixLocator postfix);

    @ByRole(role = AriaRole.LINK, value = "$^Forgot", regex = true)
    LocatorSupplier forgotLink();

    // ---------- attribute ----------
    @ByAttribute(name = "name", value = "$csrf_token")
    LocatorSupplier csrfToken();

    @ByAttribute(name = "href", value = "$/register", exact = false)
    LocatorSupplier registerLink();

    @ByAttribute(name = "title", value = "$it's")
    LocatorSupplier quotedValue();

    // ---------- mistakes ----------
    @ByRole(role = AriaRole.BUTTON, value = "$x")
    @ByText("$x")
    LocatorSupplier twoAnnotations();

    @Sensor("$//legacy")
    @ByText("$x")
    LocatorSupplier sensorAndByText();
}
