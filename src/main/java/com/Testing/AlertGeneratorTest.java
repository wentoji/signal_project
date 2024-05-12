package com.Testing;

import com.alerts.AlertProcessor;
import com.data_management.ActiveAlerts;
import com.data_management.DataStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertGeneratorTest {

    private DataStorage dataStorage;
    private AlertProcessor alertProcessor;
    private ActiveAlerts activeAlerts;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        dataStorage = new DataStorage();
        ArrayList<Integer> patientIds = new ArrayList<Integer>();
        patientIds.add(1);
        patientIds.add(2);
        activeAlerts = new ActiveAlerts(patientIds);
        alertProcessor = new AlertProcessor(dataStorage, activeAlerts);

        // Redirect System.out to the outContent stream
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testEvaluateData_NoAlert() {

        // Call the evaluateData method
        alertProcessor.evaluateData();

        // Assert that no alerts are triggered
        assertEquals("", outContent.toString().trim());
    }

    @Test
    public void testEvaluateData_BloodPressureAlert() {
        dataStorage.addPatientData(1,200,"SystolicPressure",System.currentTimeMillis());
        dataStorage.addPatientData(2,89,"SystolicPressure",System.currentTimeMillis());
        dataStorage.addPatientData(2,91,"Saturation",System.currentTimeMillis());
        // dataStorage.addPatientData(2,91,"Alert",System.currentTimeMillis());

        // Call the evaluateData method
        alertProcessor.evaluateData();

        // Assert that the blood pressure alert is triggered
        assertTrue(outContent.toString().contains("Alert Triggered:"));
        assertTrue(outContent.toString().contains("Patient ID: 1"));
        assertTrue(outContent.toString().contains("Condition: SystolicPressure"));
        assertTrue(outContent.toString().contains("Alert Triggered:"));
        assertTrue(outContent.toString().contains("Patient ID: 2"));
        assertTrue(outContent.toString().contains("Hypotensive Hypoxemia detected"));
    }

    // Add more test cases for other alert conditions

    @AfterEach
    public void restoreSystemOut() {
        // Restore original System.out
        System.setOut(originalOut);
    }
}