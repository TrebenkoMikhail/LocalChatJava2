package task7.server;

import task7.network.Connection;
import task7.network.ConnectionListner;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements ConnectionListner {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<Connection> connections = new ArrayList<>();
    private ChatServer () {
        System.out.println("Server running ...");
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("Connection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
    }

    @Override
    public synchronized void onRecieveString(Connection connection, String value) {
        sendToAllConections(value);
    }

    @Override
    public synchronized void onDisconnect(Connection connection) {
        connections.remove(connection);
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        System.out.println("Connection exception: " + e);
    }

    private void sendToAllConections(String value) {
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).sendString(value);
        }
    }
}
