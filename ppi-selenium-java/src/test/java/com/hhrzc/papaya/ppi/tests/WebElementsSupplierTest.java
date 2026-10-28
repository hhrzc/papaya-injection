package com.hhrzc.papaya.ppi.tests;

import com.hhrzc.papaya.ppi.core.WebElementIgniter;
import com.hhrzc.papaya.ppi.model.BY;
import com.hhrzc.papaya.ppi.model.WebElementSupplier;
import com.hhrzc.papaya.ppi.model.WebElementsSupplier;
import com.hhrzc.papaya.ppi.pages.ListPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.mockito.Mockito.*;

class WebElementsSupplierTest {

    private WebDriver mockDriver;
    private ListPage page;

    @BeforeEach
    void init() {
        mockDriver = mock(WebDriver.class);
        page = WebElementIgniter.ignite(ListPage.class);
    }

    @Test
    void listMethod_usesFindElements() {
        List<WebElement> expected = List.of(mock(WebElement.class), mock(WebElement.class));
        when(mockDriver.findElements(By.xpath("//li"))).thenReturn(expected);

        List<WebElement> actual = page.allItems().apply(mockDriver);

        Assertions.assertSame(expected, actual);
        verify(mockDriver, never()).findElement(any());
    }

    @Test
    void singleMethod_stillUsesFindElement() {
        WebElement expected = mock(WebElement.class);
        when(mockDriver.findElement(By.xpath("//li"))).thenReturn(expected);

        WebElement actual = page.firstItem().apply(mockDriver);

        Assertions.assertSame(expected, actual);
        verify(mockDriver, never()).findElements(any());
    }

    @Test
    void sameSelector_differentReturnTypes_getDifferentSuppliers() {
        Assertions.assertInstanceOf(WebElementSupplier.class, page.firstItem());
        Assertions.assertInstanceOf(WebElementsSupplier.class, page.allItems());
    }

    @Test
    void listWithParam_injectsParameter() {
        page.itemsByType("active").apply(mockDriver);

        verify(mockDriver).findElements(By.cssSelector("li.active"));
    }

    @Test
    void listWithBy_usesGivenBy() {
        page.itemsByClassName(BY.CLASS_NAME).apply(mockDriver);

        verify(mockDriver).findElements(By.className("item"));
    }

    @Test
    void noMatches_givesEmptyList() {
        when(mockDriver.findElements(any())).thenReturn(List.of());

        Assertions.assertTrue(page.allItems().apply(mockDriver).isEmpty());
    }

    @Test
    void listMetadata() {
        WebElementsSupplier supplier = page.allItems();

        Assertions.assertEquals("allItems", supplier.getVariableName());
        Assertions.assertEquals("By.xpath: //li", supplier.getSelectorValue());
    }

    @Test
    void listWithPostfix_isRejected() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> page.itemsWithPostfix(e -> e));
    }
}
