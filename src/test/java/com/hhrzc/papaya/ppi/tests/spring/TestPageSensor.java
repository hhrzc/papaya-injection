package com.hhrzc.papaya.ppi.tests.spring;

import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;
import com.hhrzc.papaya.ppi.tests.pages.NoFileDefinedPage;
import com.hhrzc.papaya.ppi.tests.pages.OverrideOkBtnPage;
import com.hhrzc.papaya.ppi.tests.pages.TestPage;
import com.hhrzc.papaya.ppi.utils.stage.PpiOptions;
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
    public void testPostfixParameter() {
        Locator baseLocator = mock(Locator.class);
        Locator expectedWithPostfix = mock(Locator.class);

        when(mockPage.locator("button[class*=[baz]")).thenReturn(baseLocator);
        when(baseLocator.locator("//xpath")).thenReturn(expectedWithPostfix);

        PostfixLocator postfix = locator -> locator.locator("//xpath");
        LocatorSupplier supplier = testPage.postfixParameterTest("baz", postfix);

        Locator actual = supplier.apply(mockPage);

        Assertions.assertThat(supplier.getSelectorValue()).isEqualTo("button[class*=[baz] -> locator(\"//xpath\")");
        Assertions.assertThat(actual).isEqualTo(expectedWithPostfix);
        verify(mockPage).locator("button[class*=[baz]");
        verify(baseLocator).locator("//xpath");
    }

    @Test
    public void testPostfixParameter2(){
        // 1. Setup the mocks
        Locator mokLocator = mock(Locator.class);

        // 2. STUB BOTH: Make sure both page and locator return a mock when .locator() is called
        when(mockPage.locator(anyString())).thenReturn(mokLocator);
        when(mokLocator.locator(anyString())).thenReturn(mokLocator); // <-- ADD THIS
        when(mokLocator.nth(anyInt())).thenReturn(mokLocator);

        // 3. Define the test inputs
        PostfixLocator postfix = locator -> locator.locator("//xpath");

        // 4. Execute the production code
        LocatorSupplier actual = testPage.postfixParameterTest("baz", postfix);

        // Ensure the factory method actually returned a supplier object
        Assertions.assertThat(actual).as("LocatorSupplier should not be null").isNotNull();

        // 5. Trigger the supplier evaluation
        Locator apply = actual.apply(mockPage);

        // 6. Verify and Assert
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