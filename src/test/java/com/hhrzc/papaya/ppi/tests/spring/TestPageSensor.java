package com.hhrzc.papaya.ppi.tests.spring;

import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;
import com.hhrzc.papaya.ppi.tests.pages.NoFileDefinedPage;
import com.hhrzc.papaya.ppi.tests.pages.OverrideOkBtnPage;
import com.hhrzc.papaya.ppi.tests.pages.TestPage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;


@SpringBootTest
public class TestPageSensor {

    @Autowired
    private TestPage testPage;
    @Autowired
    private NoFileDefinedPage noFileDefinedPage;
    @Autowired
    private ApplicationContext applicationContext;

    private static Page mockPage;
    private static ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    public void initMock(){
        mockPage = mock(Page.class);
        stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    public void byMethodNameTestTest(){
        LocatorSupplier actual = testPage.byMethodName();
        actual.apply(mockPage);

        verify(mockPage).locator(stringArgumentCaptor.capture());
        String capturedSelector = stringArgumentCaptor.getValue();

        Assertions.assertThat(capturedSelector)
                .isEqualTo("by-method-name-selector");
    }

    @Test
    public void byTelemetryPropertyTest(){
        LocatorSupplier actual = testPage.bySensorProperty();
        actual.apply(mockPage);

        verify(mockPage).locator(stringArgumentCaptor.capture());
        String capturedSelector = stringArgumentCaptor.getValue();

        Assertions.assertThat(capturedSelector)
                .isEqualTo("by-sensor-property-selector");
    }

    @Test
    public void overrideMethodNameTest(){
        OverrideOkBtnPage overrideOkBtnPage = applicationContext.getBean(OverrideOkBtnPage.class);
        LocatorSupplier actual = overrideOkBtnPage.byMethodName();
        actual.apply(mockPage);

        verify(mockPage).locator(stringArgumentCaptor.capture());
        String capturedSelector = stringArgumentCaptor.getValue();

        Assertions.assertThat(capturedSelector)
                .isEqualTo("override-method-name-selector");
    }

    @Test
    public void byTelemetryValueTest(){
        LocatorSupplier actual = testPage.bySensorValue();
        actual.apply(mockPage);

        verify(mockPage).locator(stringArgumentCaptor.capture());
        String capturedSelector = stringArgumentCaptor.getValue();

        Assertions.assertThat(capturedSelector)
                .isEqualTo("by-sensor-value");
    }

    @Test
    public void useMethodParametersTest(){
        LocatorSupplier actual = testPage.methodParameters("bar");
        actual.apply(mockPage);

        verify(mockPage).locator(stringArgumentCaptor.capture());
        String capturedSelector = stringArgumentCaptor.getValue();

        Assertions.assertThat(capturedSelector)
                .isEqualTo("button[class*=[bar]");
    }

    @Test
    public void testPostfixParameter(){
        Locator mokLocator = mock(Locator.class);
        when(mockPage.locator(anyString())).thenReturn(mokLocator);
        when(mokLocator.nth(anyInt())).thenReturn(mokLocator);

        PostfixLocator postfix = locator -> locator.nth(1);
        LocatorSupplier actual = testPage.postfixParameterTest("baz", postfix);
        Locator apply = actual.apply(mockPage);

        verify(mockPage).locator(stringArgumentCaptor.capture());
        String capturedSelector = stringArgumentCaptor.getValue();

        Assertions.assertThat(capturedSelector)
                .isEqualTo("button[class*=[baz]");
    }

    @Test
    public void methodWithoutFile(){
        LocatorSupplier actual = noFileDefinedPage.withoutFileSupplier();
        actual.apply(mockPage);

        verify(mockPage).locator(stringArgumentCaptor.capture());
        String capturedSelector = stringArgumentCaptor.getValue();

        Assertions.assertThat(capturedSelector)
                .isEqualTo("//hello-world-value");
    }
}