package com.cardio_generator.outputs;

/**
 * Defines the interface for strategies used to output patient data.
 */
public interface OutputStrategy {

    /**
     * Outputs patient data.
     *
     * @param patientId The ID of the patient associated with the data.
     * @param timestamp The timestamp of the data.
     * @param label     The label describing the data.
     * @param data      The actual data to be output.
     */
    void output(int patientId, long timestamp, String label, String data);
}
