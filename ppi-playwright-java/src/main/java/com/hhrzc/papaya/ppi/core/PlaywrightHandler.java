package com.hhrzc.papaya.ppi.core;

import com.hhrzc.papaya.invocation.AbstractInvocationHandler;
import com.hhrzc.papaya.ppi.annotations.Param;
import com.hhrzc.papaya.ppi.exceptions.ParamAnnotationMissingException;
import com.hhrzc.papaya.ppi.exceptions.SelectorMismatchKeyException;
import com.hhrzc.papaya.ppi.exceptions.UnexpectedParameterException;
import com.hhrzc.papaya.ppi.model.LocatorSupplier;
import com.hhrzc.papaya.ppi.model.PostfixLocator;
import com.hhrzc.papaya.ppi.utils.LocatorExtractor;
import com.hhrzc.papaya.ppi.utils.Utils;
import com.microsoft.playwright.Locator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Properties;

public class PlaywrightHandler extends AbstractInvocationHandler<Locator, LocatorSupplier, PostfixLocator> {

    public PlaywrightHandler(Properties selectors) {
        super(selectors);
    }

    @Override
    protected String injectParameters(Method method, Object[] args, String selector) {

        Parameter[] parameters = method.getParameters();
        if (args == null) {
            return selector;
        }

        String processedSelector = selector;
        for (int i = 0; i < args.length; i++) {
            Param paramAnnotation = parameters[i].getAnnotation(Param.class);
            if (paramAnnotation == null) {
                if ((args[i] instanceof String) || (args[i] instanceof Integer)) {
                    throw new ParamAnnotationMissingException(i);
                }

                if (getPostfixLocatorClass() != null && getPostfixLocatorClass().isInstance(args[i])) {
                    continue; //skip PostfixLocator parameters
                }

                throw new UnexpectedParameterException(
                        """
                                Unexpected parameter type at index %d in method with selector [%s].
                                Expected a String or Integer parameter with @Param annotation or a PostfixLocator.");
                                """.formatted(i, selector)
                );
            }

            String key = "{" + paramAnnotation.value() + "}";
            if (!processedSelector.contains(key)) {
                throw new SelectorMismatchKeyException(key, selector);
            }
            processedSelector = processedSelector.replace(key, String.valueOf(args[i]));
        }
        return processedSelector;
    }


    @Override
    protected LocatorSupplier createSupplier(Method method, String finalSelector, PostfixLocator postfix, Object... args) {
        return new LocatorSupplier() {
            @Override
            public Locator apply(com.microsoft.playwright.Page page) {
                if (page == null) {
                    throw new IllegalArgumentException("PPU Engine Fault: Page cannot be null when calling " + method.getName());
                }
                Locator locator = page.locator(finalSelector);
                return postfix == null ? locator : postfix.apply(locator);
            }

            @Override
            public String getVariableName() {
                return Utils.getName(method);
            }

            @Override
            public String getSelectorValue() {
                return finalSelector + (postfix == null ? "" : " -> " + LocatorExtractor.extractLocator(postfix));
            }
        };
    }

    @Override
    protected Class<PostfixLocator> getPostfixLocatorClass() {
        return PostfixLocator.class;
    }
}


