package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class ColoredVertex {
    private model.Vec2Float worldPos;
    public model.Vec2Float getWorldPos() { return worldPos; }
    public void setWorldPos(model.Vec2Float worldPos) { this.worldPos = worldPos; }
    private model.Vec2Float screenOffset;
    public model.Vec2Float getScreenOffset() { return screenOffset; }
    public void setScreenOffset(model.Vec2Float screenOffset) { this.screenOffset = screenOffset; }
    private model.Color color;
    public model.Color getColor() { return color; }
    public void setColor(model.Color color) { this.color = color; }
    public ColoredVertex() {}
    public ColoredVertex(model.Vec2Float worldPos, model.Vec2Float screenOffset, model.Color color) {
        this.worldPos = worldPos;
        this.screenOffset = screenOffset;
        this.color = color;
    }
    public static ColoredVertex readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        ColoredVertex result = new ColoredVertex();
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.worldPos = model.Vec2Float.readFrom(inputByteBuffer);
        } else {
            result.worldPos = null;
        }
        result.screenOffset = model.Vec2Float.readFrom(inputByteBuffer);
        result.color = model.Color.readFrom(inputByteBuffer);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        if (worldPos == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            worldPos.writeTo(stream);
        }
        screenOffset.writeTo(stream);
        color.writeTo(stream);
    }
}
