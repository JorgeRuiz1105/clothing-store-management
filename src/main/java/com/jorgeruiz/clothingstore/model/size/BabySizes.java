package com.jorgeruiz.clothingstore.model.size;

public enum BabySizes implements Size {
    M0_3("0-3M"),
    M3_6("3-6M"),
    M6_12("6-12M"),
    T2("2T"),
    T4("4T");

    private final String value;

    BabySizes(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    @Override
    public String getRepresentation() {
        return this.value;
    }
}
