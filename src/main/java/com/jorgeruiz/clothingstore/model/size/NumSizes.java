package com.jorgeruiz.clothingstore.model.size;

public enum NumSizes implements Size {
    S2(2),
    S4(4),
    S6(6),
    S8(8),
    S10(10),
    S12(12),
    S14(14),
    S16(16),
    S18(18),
    S20(20),
    S22(22),
    S24(24),
    S26(26),
    S28(28),
    S30(30),
    S32(32),
    S34(34),
    S36(36),
    S38(38),
    S40(40);

    private final int value;

    NumSizes(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }


    @Override
    public String getRepresentation() {
        return String.valueOf(value);
    }
}
