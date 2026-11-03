package com.hhrzc.papaya.ppi.tests.realtest;

import com.hhrzc.papaya.ppi.core.LocatorIgniter;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.Test;

public class ExampleTest {

    @Test
    public void exampleTest() {

        ExamplePage examplePage = LocatorIgniter.ignite(ExamplePage.class);
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setChannel("chrome"));

            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://selectorshub.com");

            PostfixLocator postfixLocator = p -> p.nth(1);
            LocatorSupplier locatorSupplier = examplePage.studioPro(postfixLocator);

            locatorSupplier.apply(page).click();
            examplePage.addToOtherBrowsers(e -> e.nth(0)).apply(page).click();
            context.close();
            browser.close();
        }
    }
}
