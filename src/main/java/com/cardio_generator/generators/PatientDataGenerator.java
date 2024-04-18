package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the interface for generating patient data.
 */
public interface PatientDataGenerator {

    /**
     * Generates patient data for the specified patient ID and outputs it using the provided OutputStrategy.
     *
     * @param patientId      The ID of the patient for which data is generated.
     * @param outputStrategy The strategy used to output the generated data.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
