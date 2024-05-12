package com.Testing;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private AlertGenerator alertGenerator;
    private DataStorage dataStorage;

    @BeforeEach
    void setUp() {
        // Create a mock DataStorage for testing
        dataStorage = new DataStorage();

        // Initialize the AlertGenerator with the mock DataStorage
        alertGenerator = new AlertGenerator(dataStorage);
    }
    @BeforeEach
    void setUpStreams() {
        // Redirect System.out to outContent
        System.setOut(new PrintStream(outContent));
    }
    @AfterEach
    void cleanUpStreams() {
        // Reset System.out
        System.setOut(System.out);
    }

    @Test
    void testEvaluateData_AlertTriggered() {
        // Create a patient record that exceeds the threshold
        int patientId = 1;
        double measurementValue = 110;
        String recordType = "HeartRate";
        long timestamp = System.currentTimeMillis() - 10000; // 10 seconds ago
        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

        // Evaluate data
        alertGenerator.evaluateData();

        // Check if an alert is triggered
        // Assuming the condition in AlertGenerator is to trigger an alert when heart rate exceeds 100
        // Retrieve the patient's records and check if an alert is generated
        List<PatientRecord> records = Arrays.asList(new PatientRecord(patientId, measurementValue, recordType, timestamp));
        assertTrue(checkAlertGenerated(records)); // Implement checkAlertGenerated method to verify if alert is triggered
    }

    private boolean checkAlertGenerated(List<PatientRecord> records) {
        // Call evaluateData to potentially trigger the alert
        alertGenerator.evaluateData();

        // Get the console output as a string
        String consoleOutput = outContent.toString();

        // Check if the console output contains the expected alert message
        return consoleOutput.contains("Alert Triggered:") &&
                consoleOutput.contains("Patient ID: " + records.get(0).getPatientId()) &&
                consoleOutput.contains("Condition: Heart Rate Alert") &&
                consoleOutput.contains("Timestamp: " + records.get(0).getTimestamp());
    }
}
