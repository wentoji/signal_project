package com.Testing;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AlertGeneratorTest {

    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

    @Before
    public void setUp() {
        dataStorage = new DataStorage(); // Create a new instance of DataStorage
        alertGenerator = new AlertGenerator(dataStorage); // Initialize AlertGenerator with DataStorage
    }

    @Test
    public void testEvaluateData_NoPatients() {
        // When there are no patients, evaluation should not trigger any alerts
        alertGenerator.evaluateData();
        // Add assertions here to verify that no alerts are triggered
    }

    @Test
    public void testEvaluateData_AlertTriggered() {
        // Create a patient and add a record that exceeds the threshold
        int patientId = 1;
        double measurementValue = 110;
        String recordType = "HeartRate";
        long timestamp = System.currentTimeMillis() - 10000; // 10 seconds ago
        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

        // Evaluate data
        alertGenerator.evaluateData();

        // Assuming the condition in AlertGenerator is to trigger an alert when heart rate exceeds 100,
        // we should expect an alert to be triggered for this patient
        // Add assertions here to verify that an alert is triggered
    }

    // Add more test cases for other scenarios if needed
}
