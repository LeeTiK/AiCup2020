import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.io.InputStream;
import java.io.BufferedOutputStream;

import model.ServerMessage;
import strategy.DebugInterface;
import util.StreamUtil;

public class Runner {
    private final BufferedInputStream inputStream;
    private final BufferedOutputStream outputStream;

    Runner(String host, int port, String token) throws IOException {
        Socket socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        inputStream = new BufferedInputStream(socket.getInputStream());
        outputStream = new BufferedOutputStream(socket.getOutputStream());
        StreamUtil.writeString(outputStream, token);
        outputStream.flush();
    }

    void run() throws IOException {
        MyStrategy myStrategy = new MyStrategy();
        DebugInterface debugInterface = new DebugInterface(inputStream, outputStream);
        while (true) {
          /*  int size = 0;
            while (size==0)
            {
                size += inputStream.available();
            }

          //  System.out.println("size: "  + inputStream.available());*/

            model.ServerMessage message = model.ServerMessage.readFrom(inputStream);
           // if (message.getClass()!= ServerMessage.DebugUpdate.class)System.out.println("decoder: "  + message);

            if (message.getClass() == model.ServerMessage.GetAction.class) {
                model.ServerMessage.GetAction getActionMessage = (model.ServerMessage.GetAction) message;
                new model.ClientMessage.ActionMessage(myStrategy.getAction(getActionMessage.getPlayerView(), getActionMessage.isDebugAvailable() ? debugInterface : null)).writeTo(outputStream);
                outputStream.flush();
            } else if (message.getClass() == model.ServerMessage.Finish.class) {
                break;
            } else if (message.getClass() == model.ServerMessage.DebugUpdate.class) {
                model.ServerMessage.DebugUpdate debugUpdateMessage = (model.ServerMessage.DebugUpdate) message;
                myStrategy.debugUpdate(debugUpdateMessage.getPlayerView(), debugInterface);
                new model.ClientMessage.DebugUpdateDone().writeTo(outputStream);
                outputStream.flush();
            } else {
                throw new IOException("Unexpected server message");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String host = args.length < 1 ? "127.0.0.1" : args[0];
        int port = args.length < 2 ? 31001 : Integer.parseInt(args[1]);
        String token = args.length < 3 ? "0000000000000000" : args[2];

       // Integer.

        new Runner(host, port, token).run();
    }
}