package com.hhrzc.papaya.ppi.exceptions;

/**
 * @author hhrzc
 * @since 2025/6/17 17:44
 */
public class SelectorMismatchKeyException extends RuntimeException {
    public SelectorMismatchKeyException(String key, String selector) {
        super("The selector " + selector + " does not match the key " + key);
    }
}
