package com.cardio_generator.outputs;

/**
 * A concrete implementation of the OutputStrategy interface that outputs data to the console.
 */
public class ConsoleOutputStrategy implements OutputStrategy {

    /**
     * Outputs the provided data to the console.
     *
     * @param patientId The ID of the patient associated with the data.
     * @param timestamp The timestamp of the data.
     * @param label     The label describing the data.
     * @param data      The actual data to be output.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        System.out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
    }
}
