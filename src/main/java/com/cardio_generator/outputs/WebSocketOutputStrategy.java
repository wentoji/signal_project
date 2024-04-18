package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A concrete implementation of the OutputStrategy interface that broadcasts data over WebSocket connections.
 *
 WebSockets are a communication protocol that provides full-duplex communication channels over a single TCP connection.
 They enable interactive communication between a client and a server in real-time.
 Unlike traditional HTTP connections, which are stateless and request-based,
 WebSocket connections allow for continuous data exchange between the client and server.

 WebSocket connections are persistent, bidirectional communication channels established between a client and a server.
 Once initiated, these connections remain open,
 allowing both parties to send messages to each other at any time without the need for repeated HTTP requests.

 WebSocket connections are commonly used in web applications for real-time features such as chat applications,
 live updates, multiplayer gaming, collaborative editing, and more.
 They provide low-latency communication and reduce overhead compared to other techniques
 like long polling or server-sent events.
 */
public class WebSocketOutputStrategy implements OutputStrategy {

    private static final Logger LOGGER = Logger.getLogger(WebSocketOutputStrategy.class.getName());

    private WebSocketServer server;

    /**
     * Constructs a WebSocketOutputStrategy object that listens for WebSocket connections on the specified port.
     *
     * @param port The port number on which the WebSocket server will listen for connections.
     */
    public WebSocketOutputStrategy(int port) {
        try {
            server = new SimpleWebSocketServer(new InetSocketAddress(port));
            LOGGER.info("WebSocket server created on port: " + port + ", listening for connections...");
            server.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting WebSocket server", e);
        }
    }

    /**
     * Outputs the provided data to all connected WebSocket clients.
     *
     * @param patientId The ID of the patient associated with the data.
     * @param timestamp The timestamp of the data.
     * @param label     The label describing the data.
     * @param data      The actual data to be output.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
        // Broadcast the message to all connected clients
        for (WebSocket conn : server.getConnections()) {
            conn.send(message);
        }
    }

    /**
     * A simple WebSocketServer implementation for handling WebSocket connections.
     */
    private static class SimpleWebSocketServer extends WebSocketServer {

        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            LOGGER.info("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            LOGGER.info("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            LOGGER.log(Level.SEVERE, "WebSocket server error", ex);
        }

        @Override
        public void onStart() {
            LOGGER.info("Server started successfully");
        }
    }
}
