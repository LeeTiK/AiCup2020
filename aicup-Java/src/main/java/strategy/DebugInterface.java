package strategy;

import java.io.*;

public class DebugInterface {
    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;

    public DebugInterface(BufferedInputStream inputStream, BufferedOutputStream outputStream) {
        this.inputStream = inputStream;
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

    public model.DebugState getState() {
        try {
            System.out.println("size1: "  + inputStream.available());
            new model.ClientMessage.RequestDebugState().writeTo(outputStream);
            outputStream.flush();
            System.out.println("size2: "  + inputStream.available());
            return model.DebugState.readFrom(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}