package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class DebugState {
    private model.Vec2Int windowSize;
    public model.Vec2Int getWindowSize() { return windowSize; }
    public void setWindowSize(model.Vec2Int windowSize) { this.windowSize = windowSize; }
    private model.Vec2Float mousePosWindow;
    public model.Vec2Float getMousePosWindow() { return mousePosWindow; }
    public void setMousePosWindow(model.Vec2Float mousePosWindow) { this.mousePosWindow = mousePosWindow; }
    private model.Vec2Float mousePosWorld;
    public model.Vec2Float getMousePosWorld() { return mousePosWorld; }
    public void setMousePosWorld(model.Vec2Float mousePosWorld) { this.mousePosWorld = mousePosWorld; }
    private String[] pressedKeys;
    public String[] getPressedKeys() { return pressedKeys; }
    public void setPressedKeys(String[] pressedKeys) { this.pressedKeys = pressedKeys; }
    private model.Camera camera;
    public model.Camera getCamera() { return camera; }
    public void setCamera(model.Camera camera) { this.camera = camera; }
    private int playerIndex;
    public int getPlayerIndex() { return playerIndex; }
    public void setPlayerIndex(int playerIndex) { this.playerIndex = playerIndex; }
    public DebugState() {}
    public DebugState(model.Vec2Int windowSize, model.Vec2Float mousePosWindow, model.Vec2Float mousePosWorld, String[] pressedKeys, model.Camera camera, int playerIndex) {
        this.windowSize = windowSize;
        this.mousePosWindow = mousePosWindow;
        this.mousePosWorld = mousePosWorld;
        this.pressedKeys = pressedKeys;
        this.camera = camera;
        this.playerIndex = playerIndex;
    }
    public static DebugState readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        DebugState result = new DebugState();
        //if (inputByteBuffer.remaining()==0) return result;
        result.windowSize = model.Vec2Int.readFrom(inputByteBuffer);
        result.mousePosWindow = model.Vec2Float.readFrom(inputByteBuffer);
        result.mousePosWorld = model.Vec2Float.readFrom(inputByteBuffer);
        result.pressedKeys = new String[inputByteBuffer.getInt()];
        for (int i = 0; i < result.pressedKeys.length; i++) {
            result.pressedKeys[i] = FinalProtocol.decoderStringByteBufferUTF8(inputByteBuffer);
        }
        result.camera = model.Camera.readFrom(inputByteBuffer);
        result.playerIndex = inputByteBuffer.getInt();
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        windowSize.writeTo(stream);
        mousePosWindow.writeTo(stream);
        mousePosWorld.writeTo(stream);
        StreamUtilBAD.writeInt(stream, pressedKeys.length);
        for (String pressedKeysElement : pressedKeys) {
            StreamUtilBAD.writeString(stream, pressedKeysElement);
        }
        camera.writeTo(stream);
        StreamUtilBAD.writeInt(stream, playerIndex);
    }
}
