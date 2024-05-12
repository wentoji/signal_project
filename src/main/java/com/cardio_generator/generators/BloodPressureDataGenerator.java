package com.cardio_generator.generators;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;

/**
 * Generates simulated blood pressure data for patients.
 * This class simulates the generation of blood pressure data, including systolic and diastolic pressures, for a specified number of patients.
 */
public class BloodPressureDataGenerator implements PatientDataGenerator {

    private static final Random random = new Random();
    private int[] lastSystolicValues;
    private int[] lastDiastolicValues;
    private static final Logger logger = Logger.getLogger(BloodPressureDataGenerator.class.getName());
    private DataStorage dataStorage;

    /**
     * Constructs a BloodPressureDataGenerator object with the specified number of patients.
     *
     * @param patientCount The number of patients for which blood pressure data will be generated.
     * @param dataStorage  The data storage to store the generated data.
     */
    public BloodPressureDataGenerator(int patientCount, DataStorage dataStorage) {
        this.dataStorage = dataStorage;

        lastSystolicValues = new int[patientCount + 1];
        lastDiastolicValues = new int[patientCount + 1];

        // Initialize with baseline values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSystolicValues[i] = 110 + random.nextInt(20); // Random baseline between 110 and 130
            lastDiastolicValues[i] = 70 + random.nextInt(15); // Random baseline between 70 and 85
        }
    }

    /**
     * Generates blood pressure data for the specified patient and stores it in the DataStorage.
     *
     * @param patientId      The ID of the patient for which blood pressure data is generated.
     * @param outputStrategy The strategy used to output the blood pressure data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            int systolicVariation = random.nextInt(5) - 2; // -2, -1, 0, 1, or 2
            int diastolicVariation = random.nextInt(5) - 2;
            int newSystolicValue = lastSystolicValues[patientId] + systolicVariation;
            int newDiastolicValue = lastDiastolicValues[patientId] + diastolicVariation;
            // Ensure the blood pressure stays within a realistic and safe range
            newSystolicValue = Math.min(Math.max(newSystolicValue, 90), 180);
            newDiastolicValue = Math.min(Math.max(newDiastolicValue, 60), 120);
            lastSystolicValues[patientId] = newSystolicValue;
            lastDiastolicValues[patientId] = newDiastolicValue;

            // Store the generated values in the DataStorage
            dataStorage.addPatientData(patientId, newSystolicValue, "SystolicPressure", System.currentTimeMillis());
            dataStorage.addPatientData(patientId, newDiastolicValue, "DiastolicPressure", System.currentTimeMillis());

            outputStrategy.output(patientId, System.currentTimeMillis(), "SystolicPressure",
                    Double.toString(newSystolicValue));
            outputStrategy.output(patientId, System.currentTimeMillis(), "DiastolicPressure",
                    Double.toString(newDiastolicValue));
        } catch (Exception e) {
            // Log any errors that occur during blood pressure data generation
            logger.log(Level.SEVERE, "An error occurred while generating blood pressure data for patient " + patientId, e);
        }
    }
}
