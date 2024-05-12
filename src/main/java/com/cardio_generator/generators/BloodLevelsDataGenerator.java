package com.cardio_generator.generators;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;

/**
 * Generates simulated blood levels data for patients.
 * This class simulates the generation of blood levels data, including cholesterol, white blood cells, and red blood cells, for a specified number of patients.
 */
public class BloodLevelsDataGenerator implements PatientDataGenerator {

    private static final Random random = new Random();
    private final double[] baselineCholesterol;
    private final double[] baselineWhiteCells;
    private final double[] baselineRedCells;
    private static final Logger logger = Logger.getLogger(BloodLevelsDataGenerator.class.getName());
    private DataStorage dataStorage;

    /**
     * Constructs a BloodLevelsDataGenerator object with the specified number of patients.
     *
     * @param patientCount The number of patients for which blood levels data will be generated.
     * @param dataStorage  The data storage to store the generated data.
     */
    public BloodLevelsDataGenerator(int patientCount, DataStorage dataStorage) {
        this.dataStorage = dataStorage;

        // Initialize arrays to store baseline values for each patient
        baselineCholesterol = new double[patientCount + 1];
        baselineWhiteCells = new double[patientCount + 1];
        baselineRedCells = new double[patientCount + 1];

        // Generate baseline values for each patient
        for (int i = 1; i <= patientCount; i++) {
            baselineCholesterol[i] = 150 + random.nextDouble() * 50; // Initial random baseline
            baselineWhiteCells[i] = 4 + random.nextDouble() * 6; // Initial random baseline
            baselineRedCells[i] = 4.5 + random.nextDouble() * 1.5; // Initial random baseline
        }
    }

    /**
     * Generates blood levels data for the specified patient and stores it in the DataStorage.
     *
     * @param patientId      The ID of the patient for which blood levels data is generated.
     * @param outputStrategy The strategy used to output the blood levels data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Generate values around the baseline for realism
            double cholesterol = baselineCholesterol[patientId] + (random.nextDouble() - 0.5) * 10; // Small variation
            double whiteCells = baselineWhiteCells[patientId] + (random.nextDouble() - 0.5) * 1; // Small variation
            double redCells = baselineRedCells[patientId] + (random.nextDouble() - 0.5) * 0.2; // Small variation

            // Store the generated values in the DataStorage
            dataStorage.addPatientData(patientId, cholesterol, "Cholesterol", System.currentTimeMillis());
            dataStorage.addPatientData(patientId, whiteCells, "WhiteBloodCells", System.currentTimeMillis());
            dataStorage.addPatientData(patientId, redCells, "RedBloodCells", System.currentTimeMillis());

            // Output the generated values using the specified OutputStrategy
            outputStrategy.output(patientId, System.currentTimeMillis(), "Cholesterol", Double.toString(cholesterol));
            outputStrategy.output(patientId, System.currentTimeMillis(), "WhiteBloodCells", Double.toString(whiteCells));
            outputStrategy.output(patientId, System.currentTimeMillis(), "RedBloodCells", Double.toString(redCells));
        } catch (Exception e) {
            // Log any errors that occur during blood levels data generation
            logger.log(Level.SEVERE, "An error occurred while generating blood levels data for patient " + patientId, e);
        }
    }
}
