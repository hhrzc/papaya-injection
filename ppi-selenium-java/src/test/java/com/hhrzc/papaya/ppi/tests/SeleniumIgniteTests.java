package com.hhrzc.papaya.ppi.tests;

import com.hhrzc.papaya.ppi.core.WebElementIgniter;
import com.hhrzc.papaya.ppi.model.BY;
import com.hhrzc.papaya.ppi.model.PostfixWebElement;
import com.hhrzc.papaya.ppi.model.WebElementSupplier;
import com.hhrzc.papaya.ppi.pages.TestPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.*;

public class SeleniumIgniteTests {

    private WebDriver mockDriver;
    private static ArgumentCaptor<By> byArgumentCaptor;

    @BeforeEach
    public void initMock(){
        mockDriver = mock(WebDriver.class);
        byArgumentCaptor = ArgumentCaptor.forClass(By.class);
    }

    @Test
    public void igniteTest(){
        TestPage ignite = WebElementIgniter.ignite(TestPage.class);
        WebElementSupplier webElementSupplier = ignite.byPropertyElement();

        webElementSupplier.apply(mockDriver);

        verify(mockDriver).findElement(byArgumentCaptor.capture());
        By capturedSelector = byArgumentCaptor.getValue();

        By expected = By.xpath("//by-property-selector");

        Assertions.assertEquals(expected, capturedSelector);
    }

    @Test
    public void igniteWithParamTest(){
        TestPage testPage = WebElementIgniter.ignite(TestPage.class);
        WebElementSupplier webElementSupplier = testPage.byPropertyElement(123);

        webElementSupplier.apply(mockDriver);

        verify(mockDriver).findElement(byArgumentCaptor.capture());
        By capturedSelector = byArgumentCaptor.getValue();

        By expected = By.xpath("//by-property-selector[123]");

        Assertions.assertEquals(expected, capturedSelector);
    }

    @Test
    public void igniteValueTest(){
        TestPage ignite = WebElementIgniter.ignite(TestPage.class);
        WebElementSupplier webElementSupplier = ignite.byValueElement();

        webElementSupplier.apply(mockDriver);

        verify(mockDriver).findElement(byArgumentCaptor.capture());
        By capturedSelector = byArgumentCaptor.getValue();

        By expected = By.xpath("//by-value-element");

        Assertions.assertEquals(expected, capturedSelector);
    }

    @Test
    public void igniteByTest(){
        TestPage ignite = WebElementIgniter.ignite(TestPage.class);
        WebElementSupplier webElementSupplier = ignite.byByElement(BY.CSS);

        webElementSupplier.apply(mockDriver);

        verify(mockDriver).findElement(byArgumentCaptor.capture());
        By capturedSelector = byArgumentCaptor.getValue();

        By expected = By.cssSelector("by-by-element");

        Assertions.assertEquals(expected, capturedSelector);
    }

    @Test
    public void determineCssTest(){
        TestPage ignite = WebElementIgniter.ignite(TestPage.class);
        WebElementSupplier webElementSupplier = ignite.byCssDetermined();

        webElementSupplier.apply(mockDriver);

        verify(mockDriver).findElement(byArgumentCaptor.capture());
        By capturedSelector = byArgumentCaptor.getValue();

        By expected = By.cssSelector("div.css-determined");

        Assertions.assertEquals(expected, capturedSelector);
    }

    @Test
    public void determineXpathTest(){
        TestPage ignite = WebElementIgniter.ignite(TestPage.class);
        WebElementSupplier webElementSupplier = ignite.byXpathDetermined();

        webElementSupplier.apply(mockDriver);

        verify(mockDriver).findElement(byArgumentCaptor.capture());
        By capturedSelector = byArgumentCaptor.getValue();

        By expected = By.xpath("//xpath-determined");

        Assertions.assertEquals(expected, capturedSelector);
    }

    @Test
    public void testPostfix() {
        final String prefixValue = "//div.first";
        final String postfixValue = "//div.second";
        final By mainSelector = By.xpath(prefixValue);
        final By postfixSelector = By.xpath(postfixValue);

        WebElement rootElement = mock(WebElement.class);
        WebElement postfixElement = mock(WebElement.class);

        when(mockDriver.findElement(mainSelector)).thenReturn(rootElement);
        when(rootElement.findElement(postfixSelector)).thenReturn(postfixElement);

        PostfixWebElement postfix = e -> e.findElement(postfixSelector);
        TestPage ignite = WebElementIgniter.ignite(TestPage.class);

        WebElementSupplier suplier = ignite.withPostfix(postfix);
        String selectorValue = suplier.getSelectorValue();
        String variableName = suplier.getVariableName();
        WebElement result = ignite.withPostfix(postfix).apply(mockDriver);

        Assertions.assertSame(postfixElement, result);
        verify(mockDriver).findElement(mainSelector);
        verify(rootElement).findElement(postfixSelector);

        Assertions.assertEquals( mainSelector + ", " + postfixSelector, selectorValue);
        Assertions.assertEquals("withPostfix", variableName);
    }
}