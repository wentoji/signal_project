package com.Servers;

import com.data_management.DataStorage;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class SimpleWebSocketServer extends WebSocketServer {

    private final DataStorage dataStorage;

    public SimpleWebSocketServer(int port, DataStorage dataStorage) {
        super(new InetSocketAddress(port));
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from " + conn.getRemoteSocketAddress() + ": " + message);
        // Forward the message to the DataStorage
         parseAndStoreData(message);
    }
    private void parseAndStoreData(String message) {
        // Assuming the message format is the same as your file format
        String[] parts = message.split(", ");
        int patientId = Integer.parseInt(parts[0].split(": ")[1].trim());
        long timestamp = Long.parseLong(parts[1].split(": ")[1].trim());
        String label = parts[2].split(": ")[1].trim();
        double data = Double.parseDouble(parts[3].split(": ")[1].trim());

        dataStorage.addPatientData(patientId, data, label, timestamp);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully!");
    }
    public void sendData(int patientId, String ecgData, String bloodSaturationData, String bloodPressureData, String bloodLevelsData) {
        // Construct the message to send
        String message = String.format("Patient ID: %d, ECG: %s, Saturation: %s, Pressure: %s, Levels: %s, Alerts: %s",
                patientId, ecgData, bloodSaturationData, bloodPressureData, bloodLevelsData);

        // Broadcast the message to all connected clients
        for (WebSocket conn : getConnections()) {
            conn.send(message);
        }
    }


    public static void main(String[] args) {
        int port = 8080; // Port number for the WebSocket server
        DataStorage dataStorage = new DataStorage(); // Create an instance of DataStorage
        SimpleWebSocketServer server = new SimpleWebSocketServer(port, dataStorage);
        server.start();
        System.out.println("WebSocket server started on port: " + port);
    }
}
