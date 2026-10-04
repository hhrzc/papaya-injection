package com.hhrzc.papaya.ppi.tests;

import com.hhrzc.papaya.ppi.core.LocatorIgniter;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;
import com.hhrzc.papaya.ppi.pages.TestPage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class PlaywrightIgniteTests {
    private Page mockPage;

    @BeforeEach
    public void initMock(){
        mockPage = mock(Page.class);
    }

    @Test
    public void igniteTest(){
        TestPage ignite = LocatorIgniter.ignite(TestPage.class);
        LocatorSupplier locatorSupplier = ignite.byPropertyLocator();

        Locator actual = locatorSupplier.apply(mockPage);
        Locator expected = verify(mockPage).locator("//by-property-selector");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void igniteWithParamTest(){
        TestPage ignite = LocatorIgniter.ignite(TestPage.class);
        LocatorSupplier locatorSupplier = ignite.byPropertyWithParamLocator(2);

        Locator actual = locatorSupplier.apply(mockPage);
        Locator expected = verify(mockPage).locator("//by-property-selector[2]");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void igniteWithPostfixTest() {
        Locator baseLocator = mock(Locator.class);
        Locator expected = mock(Locator.class);

        when(mockPage.locator("//div.first")).thenReturn(baseLocator);
        when(baseLocator.locator("//div.second")).thenReturn(expected);

        TestPage ignite = LocatorIgniter.ignite(TestPage.class);
        PostfixLocator postfixLocator = l -> l.locator("//div.second");
        LocatorSupplier locatorSupplier = ignite.withPostfix(postfixLocator);

        Locator actual = locatorSupplier.apply(mockPage);

        Assertions.assertSame(expected, actual);
        verify(mockPage).locator("//div.first");
        verify(baseLocator).locator("//div.second");
    }

    @Test
    public void nameOverridingTest(){
        TestPage ignite = LocatorIgniter.ignite(TestPage.class);
        LocatorSupplier locatorSupplier = ignite.byMethodName();

        Locator actual = locatorSupplier.apply(mockPage);
        Locator expected = verify(mockPage).locator("by-method-name-selector");

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals("Overrided", locatorSupplier.getVariableName());
    }
}
