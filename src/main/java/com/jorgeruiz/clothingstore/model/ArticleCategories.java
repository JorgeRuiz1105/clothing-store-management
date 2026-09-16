package com.jorgeruiz.clothingstore.model;

import com.jorgeruiz.clothingstore.model.size.*;

public enum ArticleCategories {
    ROPA,
    CALZADO,
    BEBES;

    public boolean isValidSize(Size size){
        if(size == null){
            return false;
        }
        switch (this){
            case ROPA:
                return (size instanceof AlphaSizes) || (size instanceof NumSizes);
            case CALZADO:
                return size instanceof ShoeSizes;
            case BEBES:
                return size instanceof BabySizes;
            default:
                return false;
        }
    }

}
