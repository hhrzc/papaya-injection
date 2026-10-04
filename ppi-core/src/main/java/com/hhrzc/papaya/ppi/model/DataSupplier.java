package com.hhrzc.papaya.ppi.model;

public interface DataSupplier {
    default String getVariableName(){
        return "not defined";
    }

    default String getSelectorValue(){
        return "not defined";
    }
}
