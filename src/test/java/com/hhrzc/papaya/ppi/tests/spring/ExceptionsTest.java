package com.hhrzc.papaya.ppi.tests.spring;

import com.hhrzc.papaya.ppi.exceptions.TelemetryFileImproprietyException;
import com.hhrzc.papaya.ppi.exceptions.ParamAnnotationMissingException;
import com.hhrzc.papaya.ppi.exceptions.SensorAnnotationMissedException;
import com.hhrzc.papaya.ppi.exceptions.TelemetryFileNotDeterminedException;
import com.hhrzc.papaya.ppi.tests.pages.FileNotExistsPage;
import com.hhrzc.papaya.ppi.tests.pages.NoFileDefinedPage;
import com.hhrzc.papaya.ppi.tests.pages.PageNotExtendsPpiRepository;
import com.hhrzc.papaya.ppi.tests.pages.TestPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class ExceptionsTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test()
    public void negativeFileDefinedTest(){
        Assertions.assertThatThrownBy(
                () -> applicationContext.getBean(FileNotExistsPage.class))
                .hasRootCauseInstanceOf(TelemetryFileImproprietyException.class);
    }

    @Test()
    public void ppiRepoNotExtendedTest(){
        Assertions.assertThatThrownBy(
                () -> applicationContext.getBean(PageNotExtendsPpiRepository.class))
                .isInstanceOf(NoSuchBeanDefinitionException.class);
    }

    @Test
    public void paramAnnotationNotExistTest(){
        TestPage page = applicationContext.getBean(TestPage.class);
        Assertions.assertThatThrownBy(() -> page.paramAnnotationMissing("foo"))
                .isInstanceOf(ParamAnnotationMissingException.class);
    }

    @Test
    public void sensorAnnotationNotExistTest(){
        TestPage page = applicationContext.getBean(TestPage.class);
        Assertions.assertThatThrownBy(() -> page.sensorAnnotationMissing("foo"))
                .isInstanceOf(SensorAnnotationMissedException.class);
    }

    @Test
    public void fileRequiredTest(){
        NoFileDefinedPage page = applicationContext.getBean(NoFileDefinedPage.class);
        Assertions.assertThatThrownBy(page::noPropertyBySensorSupplier)
                .isInstanceOf(TelemetryFileNotDeterminedException.class);
        Assertions.assertThatThrownBy(page::noPropertySupplier)
                .isInstanceOf(TelemetryFileNotDeterminedException.class);

    }



}