package com.hhrzc.papaya.ppi.exceptions;

public class PpiRepositoryNotExtendetException extends RuntimeException {
    public PpiRepositoryNotExtendetException(String name) {
        super("The class " + name + " is annotated with @Telemetry but is not extending PpiRepository, please make sure it extends PpiRepository and try again.");
    }
}
