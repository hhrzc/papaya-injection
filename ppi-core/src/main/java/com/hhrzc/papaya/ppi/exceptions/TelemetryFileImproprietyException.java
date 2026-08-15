package com.hhrzc.papaya.ppi.exceptions;

/**
 * @author hhrzc
 * @since 2025/6/17 17:44
 */
public class TelemetryFileImproprietyException extends RuntimeException {
    public TelemetryFileImproprietyException(String interfaceName, String fileName) {
        super("Filename " + fileName +
                " has impropriety name or not found for " + interfaceName);
    }
}
