package strategy;

import java.io.*;
import java.nio.ByteBuffer;

public class DebugInterface {
    private DataOutputStream outputStream;

    public DebugInterface(ByteBuffer inputStream, DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send(model.DebugCommand command) {
        try {
            new model.ClientMessage.DebugMessage(command).writeTo(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getStateWrite() {
        try {
            new model.ClientMessage.RequestDebugState().writeTo(outputStream);
            outputStream.flush();

          //  return model.DebugState.readFrom(mByteBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public model.DebugState getStateRead(ByteBuffer byteBuffer) {
        try {
          //  new model.ClientMessage.RequestDebugState().writeTo(outputStream);
         //   outputStream.flush();

            return model.DebugState.readFrom(byteBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}