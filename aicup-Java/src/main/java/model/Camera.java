package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class Camera {
    private model.Vec2Float center;
    public model.Vec2Float getCenter() { return center; }
    public void setCenter(model.Vec2Float center) { this.center = center; }
    private float rotation;
    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }
    private float attack;
    public float getAttack() { return attack; }
    public void setAttack(float attack) { this.attack = attack; }
    private float distance;
    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }
    private boolean perspective;
    public boolean isPerspective() { return perspective; }
    public void setPerspective(boolean perspective) { this.perspective = perspective; }
    public Camera() {}
    public Camera(model.Vec2Float center, float rotation, float attack, float distance, boolean perspective) {
        this.center = center;
        this.rotation = rotation;
        this.attack = attack;
        this.distance = distance;
        this.perspective = perspective;
    }
    public static Camera readFrom(ByteBuffer byteBuffer) throws java.io.IOException {
        Camera result = new Camera();
        result.center = model.Vec2Float.readFrom(byteBuffer);
        result.rotation = byteBuffer.getFloat();
        result.attack =  byteBuffer.getFloat();
        result.distance =  byteBuffer.getFloat();
        result.perspective = FinalProtocol.decoderBooleanByteBuffer(byteBuffer);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        center.writeTo(stream);
        StreamUtilBAD.writeFloat(stream, rotation);
        StreamUtilBAD.writeFloat(stream, attack);
        StreamUtilBAD.writeFloat(stream, distance);
        StreamUtilBAD.writeBoolean(stream, perspective);
    }
}
