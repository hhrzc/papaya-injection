package com.hhrzc.papaya.ppi.tests;

import com.hhrzc.papaya.ppi.core.WebElementIgniter;
import com.hhrzc.papaya.ppi.exceptions.ParamAnnotationMissingException;
import com.hhrzc.papaya.ppi.model.BY;
import com.hhrzc.papaya.ppi.model.WebElementSupplier;
import com.hhrzc.papaya.ppi.pages.TestPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ComplexTest {

    private WebDriver mockDriver;

    @BeforeEach
    public void initMock(){
        mockDriver = mock(WebDriver.class);
    }

    @Test
    public void eachSupplierKeepsItsOwnByType() {
        TestPage page = WebElementIgniter.ignite(TestPage.class);

        // Create both first, resolve later. This is the normal way to use lazy suppliers.
        WebElementSupplier xpath = page.byXpathDetermined();
        WebElementSupplier css = page.byCssDetermined();

        xpath.apply(mockDriver);
        css.apply(mockDriver);

        verify(mockDriver).findElement(By.xpath("//xpath-determined"));
        verify(mockDriver).findElement(By.cssSelector("div.css-determined"));
        Assertions.assertEquals("By.xpath: //xpath-determined", xpath.getSelectorValue());
        Assertions.assertEquals("By.cssSelector: div.css-determined", css.getSelectorValue());
    }

    @Test
    public void explicitByDoesNotLeakToOtherSuppliers() {
        TestPage page = WebElementIgniter.ignite(TestPage.class);

        WebElementSupplier byCss = page.byByElement(BY.CSS);
        WebElementSupplier autoXpath = page.byXpathDetermined();
        WebElementSupplier byXpath = page.byByElement(BY.XPATH);

        // resolve in a different order than created
        byXpath.apply(mockDriver);
        byCss.apply(mockDriver);
        autoXpath.apply(mockDriver);

        verify(mockDriver).findElement(By.cssSelector("by-by-element"));
        verify(mockDriver).findElement(By.xpath("by-by-element"));
        verify(mockDriver).findElement(By.xpath("//xpath-determined"));
    }

    @Test
    public void suppliersFromParallelThreadsAreIndependent() throws Exception {
        TestPage page = WebElementIgniter.ignite(TestPage.class);   // one shared proxy, like a Spring bean
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<String>> results = new ArrayList<>();
            for (int i = 0; i < 2000; i++) {
                boolean xpath = i % 2 == 0;
                results.add(pool.submit(() -> xpath
                        ? page.byXpathDetermined().getSelectorValue()
                        : page.byCssDetermined().getSelectorValue()));
            }
            for (int i = 0; i < results.size(); i++) {
                String expected = i % 2 == 0
                        ? "By.xpath: //xpath-determined"
                        : "By.cssSelector: div.css-determined";
                Assertions.assertEquals(expected, results.get(i).get(), "task " + i);
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    public void paramMissingReportsRealIndex() {
        TestPage page = WebElementIgniter.ignite(TestPage.class);

        ParamAnnotationMissingException e = Assertions.assertThrows(ParamAnnotationMissingException.class,
                () -> page.secondParamNotAnnotated("a", "b"));

        // the second parameter (index 1) has no @Param
        Assertions.assertTrue(e.getMessage().contains("parameter: 1"), e.getMessage());
    }
}
