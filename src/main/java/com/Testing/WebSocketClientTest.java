package com.Testing;

import com.cardio_generator.outputs.CustomWebSocketClient;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the WebSocket client.
 */
public class WebSocketClientTest {

    private CustomWebSocketClient client;
    private DataStorage storage;

    @BeforeEach
    public void setup() throws URISyntaxException, InterruptedException {
        storage = new DataStorage();
        client = new CustomWebSocketClient(new URI("ws://localhost:8080"), storage);
        client.connectBlocking();
    }

    @AfterEach
    public void teardown() {
        client.close();
    }

    @Test
    public void testWebSocketConnection() {
        assertTrue(client.isOpen(), "WebSocket client should be open.");
    }

    @Test
    public void testMessageReceivedAndStored() {
        String testMessage = "Patient ID: 1, Timestamp: 1700000000000, Label: HeartRate, Data: 75.0";

        // Simulate message received
        client.onMessage(testMessage);

        // Verify data is stored correctly
        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);
        assertEquals(1, records.size(), "One record should be stored for patient 1.");
        PatientRecord record = records.get(0);
        assertEquals(1, record.getPatientId(), "Patient ID should match.");
        assertEquals("HeartRate", record.getRecordType(), "Record type should match.");
        assertEquals(75.0, record.getMeasurementValue(), 0.01, "Measurement value should match.");
        assertEquals(1700000000000L, record.getTimestamp(), "Timestamp should match.");
    }
}
