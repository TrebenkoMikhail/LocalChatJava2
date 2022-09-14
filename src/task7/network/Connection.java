package task7.network;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.Charset;

public class Connection  {

    private final Socket socket;
    private final Thread rxThread;
    private final ConnectionListner eventListner;
    private final BufferedReader in;
    private final BufferedWriter out;

    public Connection (ConnectionListner eventListner, String ipAdress, int port) throws IOException {
        this(eventListner, new Socket(ipAdress, port));

    }

    public Connection (ConnectionListner eventListner, Socket socket) throws IOException {
        this.eventListner = eventListner;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListner.onConnectionReady(Connection.this);
                    while (!rxThread.isInterrupted()) {
                        String msg = in.readLine();
                        eventListner.onRecieveString(Connection.this, msg);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    eventListner.onDisconnect(Connection.this);
                }
            }
        });
        rxThread.start();
    }
    public synchronized void sendString (String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListner.onException(Connection.this, e);
            disconnect();
        }
    }
    public synchronized void disconnect () {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListner.onException(Connection.this, e);
        }
    }

    @Override
    public String toString() {
        return "Connection{" +
                "socket=" + socket +
                ", rxThread=" + rxThread +
                ", eventListner=" + eventListner +
                ", in=" + in +
                ", out=" + out +
                '}';
    }



}
