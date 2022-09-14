package task7.network;


public interface ConnectionListner {

    void onConnectionReady (Connection connection);
    void onRecieveString (Connection connection, String value);
    void onDisconnect (Connection connection);
    void onException (Connection connection,Exception e);
}
