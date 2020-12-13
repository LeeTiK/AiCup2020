package model;

import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class Vec2Float {
    private float x;
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    private float y;
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public Vec2Float() {}
    public Vec2Float(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public static Vec2Float readFrom(ByteBuffer byteBuffer) throws java.io.IOException {
        Vec2Float result = new Vec2Float();
        result.x = byteBuffer.getFloat();
        result.y = byteBuffer.getFloat();
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeFloat(stream, x);
        StreamUtilBAD.writeFloat(stream, y);
    }
}
