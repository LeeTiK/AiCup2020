package model;

import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class RepairAction {
    private int target;
    public int getTarget() { return target; }
    public void setTarget(int target) { this.target = target; }
    public RepairAction() {}
    public RepairAction(int target) {
        this.target = target;
    }
    public static RepairAction readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        RepairAction result = new RepairAction();
        result.target = inputByteBuffer.getInt();
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeInt(stream, target);
    }
}
