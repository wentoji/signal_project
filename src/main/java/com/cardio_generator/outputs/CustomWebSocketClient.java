package com.cardio_generator.outputs;

import com.data_management.DataReader;
import com.data_management.DataStorage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Custom WebSocket client implementation that also acts as a DataReader.
 */
public class CustomWebSocketClient extends WebSocketClient implements DataReader {

    private final DataStorage dataStorage;

    /**
     * Constructs a new CustomWebSocketClient.
     *
     * @param serverUri   the URI of the WebSocket server
     * @param dataStorage the data storage to store received data
     */
    public CustomWebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Opened connection");
    }

    @Override
    public void onMessage(String message) {
        try {
            parseAndStoreData(message);
        } catch (Exception e) {
            System.err.println("Failed to process message: " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed connection with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("An error occurred:");
        ex.printStackTrace();
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
    public void connect(String websocketUrl) {
        try {
            this.uri = new URI(websocketUrl);  // Update the URI if needed
            this.connectBlocking();  // Connect to the WebSocket server
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        DataStorage storage = new DataStorage();
        CustomWebSocketClient client = new CustomWebSocketClient(new URI("ws://localhost:8080"), storage);
        client.connectBlocking();

        // Example of sending a message to the server
        client.send("Patient ID: 1, Timestamp: 1700000000000, Label: HeartRate, Data: 75.0");

        // Close client after test
        client.close();
    }
}
