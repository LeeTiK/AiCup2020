package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class MoveAction {
    private model.Vec2Int target;
    public model.Vec2Int getTarget() { return target; }
    public void setTarget(model.Vec2Int target) { this.target = target; }
    private boolean findClosestPosition;
    public boolean isFindClosestPosition() { return findClosestPosition; }
    public void setFindClosestPosition(boolean findClosestPosition) { this.findClosestPosition = findClosestPosition; }
    private boolean breakThrough;
    public boolean isBreakThrough() { return breakThrough; }
    public void setBreakThrough(boolean breakThrough) { this.breakThrough = breakThrough; }
    public MoveAction() {}
    public MoveAction(model.Vec2Int target, boolean findClosestPosition, boolean breakThrough) {
        if (target==null)
        {
            int k =0;
        }
        this.target = target;
        this.findClosestPosition = findClosestPosition;
        this.breakThrough = breakThrough;
    }
    public static MoveAction readFrom(ByteBuffer byteBuffer) throws java.io.IOException {
        MoveAction result = new MoveAction();
        result.target = model.Vec2Int.readFrom(byteBuffer);
        result.findClosestPosition = FinalProtocol.decoderBooleanByteBuffer(byteBuffer);
        result.breakThrough = FinalProtocol.decoderBooleanByteBuffer(byteBuffer);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        target.writeTo(stream);
        StreamUtilBAD.writeBoolean(stream, findClosestPosition);
        StreamUtilBAD.writeBoolean(stream, breakThrough);
    }
}
