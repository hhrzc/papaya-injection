package com.hhrzc.papaya.ppi.tests;

import com.hhrzc.papaya.ppi.core.WebElementIgniter;
import com.hhrzc.papaya.ppi.model.BY;
import com.hhrzc.papaya.ppi.model.WebElementSupplier;
import com.hhrzc.papaya.ppi.pages.TestPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class IgniteTests {

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
}