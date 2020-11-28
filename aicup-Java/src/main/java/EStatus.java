public enum EStatus {

    NEW_Entity(0x00),
    DELETE_Entity(0x01),
    UPDATE_Entity(0x02),

    ERROR(0xFF)
    ;

    byte number;

    EStatus(int i) {
        number = (byte) i;
    }

    public byte getNumber() {
        return number;
    }
}
