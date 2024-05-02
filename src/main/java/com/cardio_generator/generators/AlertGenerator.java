package com.cardio_generator.generators;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Generates alert data for patients based on random triggers.
 * This class simulates the generation of alert data for patients and outputs the alerts using the provided OutputStrategy.
 */
public class AlertGenerator implements PatientDataGenerator {

    private static final Logger logger = Logger.getLogger(AlertGenerator.class.getName());
    public static final Random RANDOM_GENERATOR = new Random();
    private final boolean[] alertStates; // false = resolved, true = pressed
    private DataStorage storage;

    /**
     * Constructs an AlertGenerator object with the specified number of patients.
     *
     * @param patientCount The number of patients for which alerts will be generated.
     */
    public AlertGenerator(int patientCount, DataStorage storage) {
        alertStates = new boolean[patientCount + 1];
        this.storage = storage;
    }

    /**
     * Generates alert data for the specified patient and outputs it using the provided OutputStrategy.
     *
     * @param patientId      The ID of the patient for which the alert is generated.
     * @param outputStrategy The strategy used to output the alert data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Check if the patient has an active alert
            if (alertStates[patientId]) {
                // If the patient has an active alert, there's a 90% chance to resolve it
                if (RANDOM_GENERATOR.nextDouble() < 0.9) {
                    alertStates[patientId] = false; // Resolve the alert
                    // Output the resolved alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // If the patient doesn't have an active alert, calculate the probability of triggering an alert
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true; // Trigger the alert
                    // Output the triggered alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            // Log any errors that occur during alert generation
            logger.log(Level.SEVERE, "An error occurred while generating alert data for patient " + patientId, e);
        }
    }

    /**
     * Evaluates patient data and triggers alerts if necessary.
     *
     * @param outputStrategy  The strategy used to output the alert data.
     */
    public void evaluateData(OutputStrategy outputStrategy) {
        try {
            for (Patient patient : storage.getAllPatients()) {
                int patientId = patient.getId();
                // Get records for the patient within a specific time range
                long startTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // 24 hours ago
                long endTime = System.currentTimeMillis(); // Current time
                List<PatientRecord> records = storage.getRecords(patientId, startTime, endTime);

                // Check if an alert needs to be triggered based on patient data
                for (PatientRecord record : records) {
                    boolean alertNeeded = checkAlertCondition(record);
                    if (alertNeeded) {
                        // Trigger the alert
                        alertStates[patientId] = true;
                        // Output the triggered alert
                        outputStrategy.output(patientId, record.getTimestamp(), "Alert", "triggered");
                        break; // Break loop after triggering the first alert for this patient
                    }
                }
            }
        } catch (Exception e) {
            // Log any errors that occur during alert evaluation
            logger.log(Level.SEVERE, "An error occurred while evaluating patient data for alerts", e);
        }
    }

    /**
     * Checks if an alert needs to be triggered based on the patient's data.
     *
     * @param record The patient's record to be evaluated.
     * @return true if an alert needs to be triggered, false otherwise.
     */
    private boolean checkAlertCondition(PatientRecord record) {
        // Example: If patient's heart rate exceeds a certain threshold, trigger an alert
        if (record.getMeasurementValue() > 100) {
            return true;
        }
        return false;
    }
}
