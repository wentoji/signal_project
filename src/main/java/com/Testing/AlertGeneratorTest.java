package com.Testing;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AlertGeneratorTest {

    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        dataStorage = new DataStorage();
        alertGenerator = new AlertGenerator(dataStorage);

        // Redirect System.out to the outContent stream
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testEvaluateData_NoAlert() {

        // Call the evaluateData method
        alertGenerator.evaluateData();

        // Assert that no alerts are triggered
        assertEquals("", outContent.toString().trim());
    }

    @Test
    public void testEvaluateData_BloodPressureAlert() {
        dataStorage.addPatientData(1,200,"SystolicPressure",System.currentTimeMillis());
        dataStorage.addPatientData(2,89,"SystolicPressure",System.currentTimeMillis());
        dataStorage.addPatientData(2,91,"Saturation",System.currentTimeMillis());

        // Call the evaluateData method
        alertGenerator.evaluateData();

        // Assert that the blood pressure alert is triggered
        assertTrue(outContent.toString().contains("Alert Triggered:"));
        assertTrue(outContent.toString().contains("Patient ID: 1"));
        assertTrue(outContent.toString().contains("Condition: SystolicPressure"));
        assertTrue(outContent.toString().contains("Alert Triggered:"));
        assertTrue(outContent.toString().contains("Patient ID: 2"));
        assertTrue(outContent.toString().contains("Hypotensive Hypoxemia detected"));
    }

    // Add more test cases for other alert conditions

    @After
    public void restoreSystemOut() {
        // Restore original System.out
        System.setOut(originalOut);
    }
}
