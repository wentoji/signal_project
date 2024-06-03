package com.data_management;

/**
 * Interface for reading data from a WebSocket.
 */
public interface DataReader {

    /**
     * Connects to the WebSocket server.
     *
     * @param websocketUrl the URL of the WebSocket server
     */
    void connect(String websocketUrl);

    /**
     * Handles incoming messages from the WebSocket server.
     *
     * @param message the message received from the WebSocket server
     */
    void onMessage(String message);
}
