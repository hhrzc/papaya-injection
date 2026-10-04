package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.invocation.AbstractInvocationHandler;
import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.exceptions.ParamAnnotationMissingException;
import com.hhrzc.papaya.ppi.exceptions.SelectorMismatchKeyException;
import com.hhrzc.papaya.ppi.model.BY;
import com.hhrzc.papaya.ppi.model.PostfixWebElement;
import com.hhrzc.papaya.ppi.model.WebElementSupplier;
import com.hhrzc.papaya.ppi.utils.LocatorExtractor;
import com.hhrzc.papaya.ppi.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Properties;

public class SeleniumHandler extends AbstractInvocationHandler<WebElement, WebElementSupplier, PostfixWebElement> {

    private static final BY DEFAULT_BY = BY.XPATH;
    private BY processedBy;

    public SeleniumHandler(Properties selectors) {
        super(selectors);
    }

     @Override
    protected String injectParameters(Method method, Object[] args, String selector) {
        String processedSelector = selector;
         processedBy = DEFAULT_BY;
         if (args != null && args.length > 0) {
             int start = 0;
             if (method.getParameters()[0].getType() == BY.class) {
                 processedBy = (BY) args[0];
                 start = 1;//offset if first parameter is BY - selenium might have only one (and first) parameter for BY and the rest for @Param
             } else {
                 //if BY is not defined, we will try to determine it based on the selector format (XPath or CSS)
                 processedBy = Utils.isXPath(selector) ? BY.XPATH : BY.CSS;
             }
             try {
                 if (args.length > start) {
                     processedSelector = addParams(selector, method.getParameters(), args, start);
                 }
             } catch (ParamAnnotationMissingException e) {
                 throw new ParamAnnotationMissingException(
                         "The @Param annotation missing for the method " + method.getName() +
                                 ". Order number of the parameter: " + e.getParamsOrder());
             }
         } else {
                processedBy = Utils.isXPath(selector) ? BY.XPATH : BY.CSS;
         }

         return processedSelector;
    }

    @Override
    protected WebElementSupplier createSupplier(Method method, String finalSelector, PostfixWebElement postfix, Object... args) {
        final By postfixBy = LocatorExtractor.extractByFromArg(postfix);
        return new WebElementSupplier() {
            @Override
            public WebElement apply(WebDriver driver) {
                if (driver == null) {
                    throw new IllegalArgumentException("PPU Engine Fault: WebDriver cannot be null when calling " + method.getName());
                }
                WebElement element = driver.findElement(processedBy.by().apply(finalSelector));
                return postfix == null ? element : postfix.apply(element);
            }

            @Override
            public String getVariableName() {
                return Utils.getName(method);
            }

            @Override
            public String getSelectorValue() {
                return processedBy.prefixValue() + ": " + finalSelector + (postfixBy == null ? "" :
                        ", " + postfixBy);
            }

            @Override
            public String toString() {
                return "WebElementSupplier{" +
                        "variableName='" + getVariableName() + '\'' +
                        ", selectorValue='" + getSelectorValue() + '\'' +
                        '}';
            }
        };
    }

    @Override
    protected Class<PostfixWebElement> getPostfixLocatorClass() {
        return PostfixWebElement.class;
    }

    private String addParams(String selector, Parameter[] parameters, Object[] args, int start) {
        String processedSelector = selector;
        for (int i = start; i < args.length; i++) {
            Parameter parameter = parameters[i];
            if(PostfixWebElement.class.isAssignableFrom(parameter.getType())) {
                continue; //skip PostfixLocator parameters
            }
            Param paramAnnotation = parameters[i].getAnnotation(Param.class);
            if (paramAnnotation == null) {
                throw new ParamAnnotationMissingException(i);
            }
            String key = "{" + paramAnnotation.value() + "}";
            if (!selector.contains(key)) {
                throw new SelectorMismatchKeyException(key, selector);
            }
            processedSelector = processedSelector.replace(key, String.valueOf(args[i]));
        }

        if ((processedSelector.contains("{") && processedSelector.contains("}")) &&
                processedSelector.indexOf("{") < processedSelector.indexOf("}")
        ) {
            throw new SelectorMismatchKeyException("Unresolved placeholders found", processedSelector);
        }

        return processedSelector;
    }

    private String resolveSelector(String selector){
        return Utils.isXPath(selector) ? "XPath: " + selector : "css: " + selector;
    }
}
