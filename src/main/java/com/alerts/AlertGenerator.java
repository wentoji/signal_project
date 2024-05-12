package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private static final Logger logger = Logger.getLogger(AlertGenerator.class.getName());
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the patient data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert} method. This method should define the specific conditions under which an
     * alert will be triggered.
     */
    /**
     * Evaluates the patient data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert} method. This method should define the specific conditions under which an
     * alert will be triggered.
     */
    public void evaluateData() {
        try {
            List<Patient> patients = dataStorage.getAllPatients();
            for (Patient patient : patients) {
                int patientId = patient.getId();
                // Get records for the patient within a specific time range
                long startTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // 24 hours ago
                long endTime = System.currentTimeMillis(); // Current time
                List<PatientRecord> records = dataStorage.getRecords(patientId, startTime, endTime);

                // Check if an alert needs to be triggered based on patient data
                for (PatientRecord record : records) {
                    if (checkAlertCondition(record)) {
                        Alert alert = new Alert(String.valueOf(patientId), "Heart Rate Alert", record.getTimestamp());
                        triggerAlert(alert);
                    }
                }
            }
        } catch (Exception e) {
            // Log any errors that occur during alert evaluation
            logger.log(Level.SEVERE, "An error occurred while evaluating patient data for alerts", e);
        }
    }


    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        System.out.println("Alert Triggered:");
        System.out.println("Patient ID: " + alert.getPatientId());
        System.out.println("Condition: " + alert.getCondition());
        System.out.println("Timestamp: " + alert.getTimestamp());
    }

    /**
     * Checks if an alert needs to be triggered based on the patient record.
     *
     * @param record the patient record to evaluate
     * @return true if an alert needs to be triggered, false otherwise
     */
    private boolean checkAlertCondition(PatientRecord record) {
        // Example: If patient's heart rate exceeds a certain threshold, trigger an alert
        return record.getMeasurementValue() > 100;
    }
}
