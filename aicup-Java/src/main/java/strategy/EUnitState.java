package strategy;

public enum  EUnitState {
    NEW_UNIT(0x00),
    ATTACK(0x01),
    DEFENSE(0x02),
    RESURCE(0x03),
    REPAIR(0x04),
    BUILD(0x05),

    EMPTY(0xFF);
    ;

    byte number;

    EUnitState(int i) {
        number = (byte) i;
    }

    public byte getNumber() {
        return number;
    }
}
