package com.cardio_generator.generators;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;

/**
 * Generates simulated ECG (Electrocardiogram) data for patients.
 * This class simulates the generation of ECG data for a specified number of patients.
 */
public class ECGDataGenerator implements PatientDataGenerator {

    private static final Random random = new Random();
    private double[] lastEcgValues;
    private static final double PI = Math.PI;
    private static final Logger logger = Logger.getLogger(ECGDataGenerator.class.getName());
    private DataStorage dataStorage;

    /**
     * Constructs an ECGDataGenerator object with the specified number of patients.
     *
     * @param patientCount The number of patients for which ECG data will be generated.
     * @param dataStorage  The data storage to store the generated data.
     */
    public ECGDataGenerator(int patientCount, DataStorage dataStorage) {
        this.dataStorage = dataStorage;

        lastEcgValues = new double[patientCount + 1];
        // Initialize the last ECG value for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastEcgValues[i] = 0; // Initial ECG value can be set to 0
        }
    }

    /**
     * Generates ECG data for the specified patient and outputs it using the provided OutputStrategy.
     *
     * @param patientId      The ID of the patient for which ECG data is generated.
     * @param outputStrategy The strategy used to output the ECG data.
     * @throws IllegalArgumentException if the patientId is invalid.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) throws IllegalArgumentException {
        try {
            double ecgValue = simulateEcgWaveform(patientId, lastEcgValues[patientId]);
            outputStrategy.output(patientId, System.currentTimeMillis(), "ECG", Double.toString(ecgValue));
            lastEcgValues[patientId] = ecgValue;

            // Store the generated ECG value in the DataStorage
            dataStorage.addPatientData(patientId, ecgValue, "ECG", System.currentTimeMillis());
        } catch (ArrayIndexOutOfBoundsException e) {
            // Log and rethrow if the patientId is invalid
            logger.log(Level.SEVERE, "Invalid patientId: " + patientId, e);
            throw new IllegalArgumentException("Invalid patientId: " + patientId, e);
        } catch (Exception e) {
            // Log any other errors that occur during ECG data generation
            logger.log(Level.SEVERE, "An error occurred while generating ECG data for patient " + patientId, e);
        }
    }

    /**
     * Simulates the ECG waveform for the specified patient.
     *
     * @param patientId     The ID of the patient for which the ECG waveform is simulated.
     * @param lastEcgValue  The last ECG value for the specified patient.
     * @return              The simulated ECG value for the specified patient.
     */
    private double simulateEcgWaveform(int patientId, double lastEcgValue) {
        // Simplified ECG waveform generation based on sinusoids
        double hr = 60.0 + random.nextDouble() * 20.0; // Simulate heart rate variability between 60 and 80 bpm
        double t = System.currentTimeMillis() / 1000.0; // Use system time to simulate continuous time
        double ecgFrequency = hr / 60.0; // Convert heart rate to Hz

        // Simulate different components of the ECG signal
        double pWave = 0.1 * Math.sin(2 * PI * ecgFrequency * t);
        double qrsComplex = 0.5 * Math.sin(2 * PI * 3 * ecgFrequency * t); // QRS is higher frequency
        double tWave = 0.2 * Math.sin(2 * PI * 2 * ecgFrequency * t + PI / 4); // T wave is offset

        return pWave + qrsComplex + tWave + random.nextDouble() * 0.05; // Add small noise
    }
}
