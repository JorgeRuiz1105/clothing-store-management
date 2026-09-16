package com.jorgeruiz.clothingstore.model.size;

public enum ShoeSizes implements Size {
    S15(15),
    S16(16),
    S17(17),
    S18(18),
    S19(19),
    S20(20),
    S21(21),
    S22(22),
    S23(23),
    S24(24),
    S25(25),
    S26(26),
    S27(27),
    S28(28),
    S29(29),
    S30(30),
    S31(31),
    S32(32),
    S33(33),
    S34(34),
    S35(35),
    S36(36),
    S37(37),
    S38(38),
    S39(39),
    S40(40),
    S41(41),
    S42(42),
    S43(43),
    S44(44),
    S45(45),
    S46(46),
    S47(47),
    S48(48);

    private final int value;

    ShoeSizes(int value){
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
