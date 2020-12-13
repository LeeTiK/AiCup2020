package model;

import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class RepairProperties {
    private model.EntityType[] validTargets;
    public model.EntityType[] getValidTargets() { return validTargets; }
    public void setValidTargets(model.EntityType[] validTargets) { this.validTargets = validTargets; }
    private int power;
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }
    public RepairProperties() {}
    public RepairProperties(model.EntityType[] validTargets, int power) {
        this.validTargets = validTargets;
        this.power = power;
    }
    public static RepairProperties readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        RepairProperties result = new RepairProperties();
        result.validTargets = new model.EntityType[inputByteBuffer.getInt()];
        for (int i = 0; i < result.validTargets.length; i++) {
            switch (inputByteBuffer.getInt()) {
            case 0:
                result.validTargets[i] = model.EntityType.WALL;
                break;
            case 1:
                result.validTargets[i] = model.EntityType.HOUSE;
                break;
            case 2:
                result.validTargets[i] = model.EntityType.BUILDER_BASE;
                break;
            case 3:
                result.validTargets[i] = model.EntityType.BUILDER_UNIT;
                break;
            case 4:
                result.validTargets[i] = model.EntityType.MELEE_BASE;
                break;
            case 5:
                result.validTargets[i] = model.EntityType.MELEE_UNIT;
                break;
            case 6:
                result.validTargets[i] = model.EntityType.RANGED_BASE;
                break;
            case 7:
                result.validTargets[i] = model.EntityType.RANGED_UNIT;
                break;
            case 8:
                result.validTargets[i] = model.EntityType.RESOURCE;
                break;
            case 9:
                result.validTargets[i] = model.EntityType.TURRET;
                break;
            default:
                throw new java.io.IOException("Unexpected tag value");
            }
        }
        result.power = inputByteBuffer.getInt();
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeInt(stream, validTargets.length);
        for (model.EntityType validTargetsElement : validTargets) {
            StreamUtilBAD.writeInt(stream, validTargetsElement.tag);
        }
        StreamUtilBAD.writeInt(stream, power);
    }
}
