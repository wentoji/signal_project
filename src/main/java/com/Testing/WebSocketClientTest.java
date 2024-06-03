package com.Testing;

import com.cardio_generator.outputs.CustomWebSocketClient;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Test class for the WebSocket client.
 */
public class WebSocketClientTest {

    /**
     * The main method to test WebSocket client functionality.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            // Create DataStorage instance
            DataStorage storage = new DataStorage();

            // Create and connect WebSocketClient
            CustomWebSocketClient client = new CustomWebSocketClient(new URI("ws://localhost:8080"), storage);
            client.connectBlocking();

            // Simulate multiple incoming messages
            String[] testMessages = {
                    "Patient ID: 1, Timestamp: 1700000000000, Label: HeartRate, Data: 75.0",
                    "Patient ID: 1, Timestamp: 1700000005000, Label: BloodPressure, Data: 120.0",
                    "Patient ID: 2, Timestamp: 1700000010000, Label: HeartRate, Data: 65.0"
            };

            for (String testMessage : testMessages) {
                client.onMessage(testMessage);
            }

            // Verify data is stored correctly
            List<PatientRecord> recordsPatient1 = storage.getRecords(1, 1700000000000L, 1800000000000L);
            for (PatientRecord record : recordsPatient1) {
                System.out.println("Record for Patient ID: " + record.getPatientId() +
                        ", Type: " + record.getRecordType() +
                        ", Data: " + record.getMeasurementValue() +
                        ", Timestamp: " + record.getTimestamp());
            }

            List<PatientRecord> recordsPatient2 = storage.getRecords(2, 1700000000000L, 1800000000000L);
            for (PatientRecord record : recordsPatient2) {
                System.out.println("Record for Patient ID: " + record.getPatientId() +
                        ", Type: " + record.getRecordType() +
                        ", Data: " + record.getMeasurementValue() +
                        ", Timestamp: " + record.getTimestamp());
            }

            // Close client
            client.close();
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
