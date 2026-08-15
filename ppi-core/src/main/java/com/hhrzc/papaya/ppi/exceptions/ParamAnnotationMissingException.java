package com.hhrzc.papaya.ppi.exceptions;

/**
 * @author hhrzc
 * @since 2025/6/17 17:44
 */
public class ParamAnnotationMissingException extends RuntimeException {

    private int paramsOrder;
    public ParamAnnotationMissingException(int paramsOrder) {
        this.paramsOrder = paramsOrder;
    }

    public ParamAnnotationMissingException(String message){
        super(message);
    }

    public int getParamsOrder(){
        return paramsOrder;
    }
}