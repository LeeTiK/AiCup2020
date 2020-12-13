package model;

import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class Color {
    private float r;
    public float getR() { return r; }
    public void setR(float r) { this.r = r; }
    private float g;
    public float getG() { return g; }
    public void setG(float g) { this.g = g; }
    private float b;
    public float getB() { return b; }
    public void setB(float b) { this.b = b; }
    private float a;
    public float getA() { return a; }
    public void setA(float a) { this.a = a; }
    public Color() {}
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public static Color readFrom(ByteBuffer stream) throws java.io.IOException {
        Color result = new Color();
        result.r = stream.getFloat();
        result.g = stream.getFloat();
        result.b = stream.getFloat();
        result.a = stream.getFloat();
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeFloat(stream, r);
        StreamUtilBAD.writeFloat(stream, g);
        StreamUtilBAD.writeFloat(stream, b);
        StreamUtilBAD.writeFloat(stream, a);
    }
}
