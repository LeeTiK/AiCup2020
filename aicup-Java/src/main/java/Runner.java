import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import model.DebugCommand;
import strategy.DebugInterface;
import util.StreamUtilBAD;

public class Runner {
    private final BufferedInputStream inputStream;
    private final DataOutputStream outputStream;

    ByteBuffer input;
    ByteBuffer output;

    byte[] bytesRead = new byte[1000000];

    Runner(String host, int port, String token) throws IOException {
        Socket socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        inputStream = new BufferedInputStream(socket.getInputStream());
      //  outputStream = new BufferedOutputStream(socket.getOutputStream());
        input = ByteBuffer.allocateDirect(1000000).order(ByteOrder.LITTLE_ENDIAN);
        output = ByteBuffer.allocateDirect(1000000).order(ByteOrder.LITTLE_ENDIAN);

        //inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        StreamUtilBAD.writeString(outputStream, token);
        outputStream.flush();
    }

    void run() throws IOException {
        MyStrategy myStrategy = new MyStrategy();
        DebugInterface debugInterface = new DebugInterface(input, outputStream);
        while (true) {

            input.clear();
            while (inputStream.available()!=0)
            {
                int size = inputStream.available();
                System.out.println("sizeRead: " + size);
                inputStream.read(bytesRead,0,size);
                input.put(bytesRead,0,size);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            input.flip();

            if (input.remaining()>3) {
                System.out.println("decode!!!!");
                model.ServerMessage message = model.ServerMessage.readFrom(input);
                System.out.println(message);
                if (message instanceof model.ServerMessage.GetAction) {
                    model.ServerMessage.GetAction getActionMessage = (model.ServerMessage.GetAction) message;
                    new model.ClientMessage.ActionMessage(myStrategy.getAction(getActionMessage.getPlayerView(), getActionMessage.isDebugAvailable() ? debugInterface : null)).writeTo(outputStream);
                    outputStream.flush();
                } else if (message instanceof model.ServerMessage.Finish) {
                    break;
                } else if (message instanceof model.ServerMessage.DebugUpdate) {
                    model.ServerMessage.DebugUpdate debugUpdateMessage = (model.ServerMessage.DebugUpdate) message;
                 //   myStrategy.debugUpdate(debugUpdateMessage.getPlayerView(), debugInterface);
                   // debugInterface.send(new DebugCommand.Clear());
                  //  debugInterface.getStateWrite();
                   // debugInterface.getStateRead(input);
                    new model.ClientMessage.DebugUpdateDone().writeTo(outputStream);
                    outputStream.flush();
                } else {
                    throw new IOException("Unexpected server message");
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String host = args.length < 1 ? "127.0.0.1" : args[0];
        int port = args.length < 2 ? 31001 : Integer.parseInt(args[1]);
        String token = args.length < 3 ? "0000000000000000" : args[2];
        new Runner(host, port, token).run();
    }
}