package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class BuildProperties {
    private model.EntityType[] options;
    public model.EntityType[] getOptions() { return options; }
    public void setOptions(model.EntityType[] options) { this.options = options; }
    private Integer initHealth;
    public Integer getInitHealth() { return initHealth; }
    public void setInitHealth(Integer initHealth) { this.initHealth = initHealth; }
    public BuildProperties() {}
    public BuildProperties(model.EntityType[] options, Integer initHealth) {
        this.options = options;
        this.initHealth = initHealth;
    }
    public static BuildProperties readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        BuildProperties result = new BuildProperties();
        result.options = new model.EntityType[inputByteBuffer.getInt()];
        for (int i = 0; i < result.options.length; i++) {
            switch (inputByteBuffer.getInt()) {
            case 0:
                result.options[i] = model.EntityType.WALL;
                break;
            case 1:
                result.options[i] = model.EntityType.HOUSE;
                break;
            case 2:
                result.options[i] = model.EntityType.BUILDER_BASE;
                break;
            case 3:
                result.options[i] = model.EntityType.BUILDER_UNIT;
                break;
            case 4:
                result.options[i] = model.EntityType.MELEE_BASE;
                break;
            case 5:
                result.options[i] = model.EntityType.MELEE_UNIT;
                break;
            case 6:
                result.options[i] = model.EntityType.RANGED_BASE;
                break;
            case 7:
                result.options[i] = model.EntityType.RANGED_UNIT;
                break;
            case 8:
                result.options[i] = model.EntityType.RESOURCE;
                break;
            case 9:
                result.options[i] = model.EntityType.TURRET;
                break;
            default:
                throw new java.io.IOException("Unexpected tag value");
            }
        }
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.initHealth = inputByteBuffer.getInt();
        } else {
            result.initHealth = null;
        }
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeInt(stream, options.length);
        for (model.EntityType optionsElement : options) {
            StreamUtilBAD.writeInt(stream, optionsElement.tag);
        }
        if (initHealth == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            StreamUtilBAD.writeInt(stream, initHealth);
        }
    }
}
