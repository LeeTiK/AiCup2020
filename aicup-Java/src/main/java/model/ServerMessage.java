package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.io.DataInputStream;
import java.nio.ByteBuffer;

public abstract class ServerMessage {
    public abstract void writeTo(java.io.OutputStream stream) throws java.io.IOException;
    public static ServerMessage readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        switch (inputByteBuffer.getInt()) {
            case GetAction.TAG:
                return GetAction.readFrom(inputByteBuffer);
            case Finish.TAG:
                return Finish.readFrom(inputByteBuffer);
            case DebugUpdate.TAG:
                return DebugUpdate.readFrom(inputByteBuffer);
            default:
                throw new java.io.IOException("Unexpected tag value");
        }
    }

    public static class GetAction extends ServerMessage {
        public static final int TAG = 0;
        private model.PlayerView playerView;
        public model.PlayerView getPlayerView() { return playerView; }
        public void setPlayerView(model.PlayerView playerView) { this.playerView = playerView; }
        private boolean debugAvailable;
        public boolean isDebugAvailable() { return debugAvailable; }
        public void setDebugAvailable(boolean debugAvailable) { this.debugAvailable = debugAvailable; }
        public GetAction() {}
        public GetAction(model.PlayerView playerView, boolean debugAvailable) {
            this.playerView = playerView;
            this.debugAvailable = debugAvailable;
        }
        public static GetAction readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
            GetAction result = new GetAction();
            result.playerView = model.PlayerView.readFrom(inputByteBuffer);
            result.debugAvailable = FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer);
            return result;
        }
        @Override
        public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
            StreamUtilBAD.writeInt(stream, TAG);
            playerView.writeTo(stream);
            StreamUtilBAD.writeBoolean(stream, debugAvailable);
        }
    }

    public static class Finish extends ServerMessage {
        public static final int TAG = 1;
        public Finish() {}
        public static Finish readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
            Finish result = new Finish();
            return result;
        }
        @Override
        public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
            StreamUtilBAD.writeInt(stream, TAG);
        }
    }

    public static class DebugUpdate extends ServerMessage {
        public static final int TAG = 2;
        private model.PlayerView playerView;
        public model.PlayerView getPlayerView() { return playerView; }
        public void setPlayerView(model.PlayerView playerView) { this.playerView = playerView; }
        public DebugUpdate() {}
        public DebugUpdate(model.PlayerView playerView) {
            this.playerView = playerView;
        }
        public static DebugUpdate readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
            DebugUpdate result = new DebugUpdate();
            result.playerView = model.PlayerView.readFrom(inputByteBuffer);
            return result;
        }
        @Override
        public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
            StreamUtilBAD.writeInt(stream, TAG);
            playerView.writeTo(stream);
        }
    }
}
