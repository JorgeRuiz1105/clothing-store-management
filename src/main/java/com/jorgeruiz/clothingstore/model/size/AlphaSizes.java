package com.jorgeruiz.clothingstore.model.size;

public enum AlphaSizes implements Size {
    XS,
    S,
    M,
    L,
    XL,
    XXL;

    @Override
    public String getRepresentation() {
        return this.name();
    }

    public static AlphaSizes fromRepresentation(String value){
        return valueOf(value);
    }
}
