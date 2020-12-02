public enum  EUnitState {
    CREATE(0x00),
    ATTACK(0x01),
    DEFENSE(0x02),
    RESURCE(0x03),

    ERROR(0xFF);
    ;

    byte number;

    EUnitState(int i) {
        number = (byte) i;
    }

    public byte getNumber() {
        return number;
    }
}
