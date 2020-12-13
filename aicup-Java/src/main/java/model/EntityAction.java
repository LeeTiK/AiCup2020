package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class EntityAction {
    private model.MoveAction moveAction;
    public model.MoveAction getMoveAction() { return moveAction; }
    public void setMoveAction(model.MoveAction moveAction) { this.moveAction = moveAction; }
    private model.BuildAction buildAction;
    public model.BuildAction getBuildAction() { return buildAction; }
    public void setBuildAction(model.BuildAction buildAction) { this.buildAction = buildAction; }
    private model.AttackAction attackAction;
    public model.AttackAction getAttackAction() { return attackAction; }
    public void setAttackAction(model.AttackAction attackAction) { this.attackAction = attackAction; }
    private model.RepairAction repairAction;
    public model.RepairAction getRepairAction() { return repairAction; }
    public void setRepairAction(model.RepairAction repairAction) { this.repairAction = repairAction; }
    public EntityAction() {}
    public EntityAction(model.MoveAction moveAction, model.BuildAction buildAction, model.AttackAction attackAction, model.RepairAction repairAction) {
        this.moveAction = moveAction;
        this.buildAction = buildAction;
        this.attackAction = attackAction;
        this.repairAction = repairAction;
    }
    public static EntityAction readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        EntityAction result = new EntityAction();
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.moveAction = model.MoveAction.readFrom(inputByteBuffer);
        } else {
            result.moveAction = null;
        }
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.buildAction = model.BuildAction.readFrom(inputByteBuffer);
        } else {
            result.buildAction = null;
        }
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.attackAction = model.AttackAction.readFrom(inputByteBuffer);
        } else {
            result.attackAction = null;
        }
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.repairAction = model.RepairAction.readFrom(inputByteBuffer);
        } else {
            result.repairAction = null;
        }
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        if (moveAction == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            moveAction.writeTo(stream);
        }
        if (buildAction == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            buildAction.writeTo(stream);
        }
        if (attackAction == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            attackAction.writeTo(stream);
        }
        if (repairAction == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            repairAction.writeTo(stream);
        }
    }

    public void clear() {
        this.moveAction = null;
        this.buildAction = null;
        this.attackAction = null;
        this.repairAction = null;
    }
}
