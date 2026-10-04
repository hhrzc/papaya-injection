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
        // 1. Initialize the Playwright core driver instance
        try (Playwright playwright = Playwright.create()) {

            // 2. Launch Chromium (Headed mode so you can visually see the browser open)
            // If you want to use your actual installed Google Chrome instead of Playwright's bundled Chromium,
            // you can append: .setChannel("chrome")
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setChannel("chrome")); // Force it to use your local Chrome instance

            // 3. Initialize a clean BrowserContext session
            BrowserContext context = browser.newContext();

            // 4. Initialize your Page instance
            Page page = context.newPage();

            // 5. Navigate to the requested target URL
            page.navigate("https://selectorshub.com");

            // --- Your automation / element verification code goes here ---
            System.out.println("Current Page Title: " + page.title());

            PostfixLocator postfixLocator = p -> p.nth(1);
            LocatorSupplier locatorSupplier = examplePage.studioPro(postfixLocator);

            locatorSupplier.apply(page).click();
            System.out.println("Clicked on Studio Pro link!");

            // 6. Graceful cleanup (Close contexts and browser to clear memory)
            context.close();
            browser.close();
        } // The try-with-resources block automatically handles playwright.close() safely
    }
}
