package com.cardio_generator.generators;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;

/**
 * Generates simulated blood saturation data for patients.
 * This class simulates the generation of blood saturation data for a specified number of patients.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {

    private static final Random random = new Random();
    private int[] lastSaturationValues;
    private static final Logger logger = Logger.getLogger(BloodSaturationDataGenerator.class.getName());
    private DataStorage dataStorage;

    /**
     * Constructs a BloodSaturationDataGenerator object with the specified number of patients.
     *
     * @param patientCount The number of patients for which blood saturation data will be generated.
     * @param dataStorage  The data storage to store the generated data.
     */
    public BloodSaturationDataGenerator(int patientCount, DataStorage dataStorage) {
        this.dataStorage = dataStorage;

        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates blood saturation data for the specified patient and stores it in the DataStorage.
     *
     * @param patientId      The ID of the patient for which blood saturation data is generated.
     * @param outputStrategy The strategy used to output the blood saturation data.
     * @throws IllegalArgumentException if the patientId is invalid.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) throws IllegalArgumentException {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;

            // Store the generated value in the DataStorage
            dataStorage.addPatientData(patientId, newSaturationValue, "Saturation", System.currentTimeMillis());

            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Log and rethrow if the patientId is invalid
            logger.log(Level.SEVERE, "Invalid patientId: " + patientId, e);
            throw new IllegalArgumentException("Invalid patientId: " + patientId, e);
        } catch (Exception e) {
            // Log any other errors that occur during blood saturation data generation
            logger.log(Level.SEVERE, "An error occurred while generating blood saturation data for patient " + patientId, e);
        }
    }
}
