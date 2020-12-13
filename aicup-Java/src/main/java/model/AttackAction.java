package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class AttackAction {
    private Integer target;
    public Integer getTarget() { return target; }
    public void setTarget(Integer target) { this.target = target; }
    private model.AutoAttack autoAttack;
    public model.AutoAttack getAutoAttack() { return autoAttack; }
    public void setAutoAttack(model.AutoAttack autoAttack) { this.autoAttack = autoAttack; }
    public AttackAction() {}
    public AttackAction(Integer target, model.AutoAttack autoAttack) {
        this.target = target;
        this.autoAttack = autoAttack;
    }
    public static AttackAction readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        AttackAction result = new AttackAction();
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.target = inputByteBuffer.getInt();
        } else {
            result.target = null;
        }
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.autoAttack = model.AutoAttack.readFrom(inputByteBuffer);
        } else {
            result.autoAttack = null;
        }
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        if (target == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            StreamUtilBAD.writeInt(stream, target);
        }
        if (autoAttack == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            autoAttack.writeTo(stream);
        }
    }
}
