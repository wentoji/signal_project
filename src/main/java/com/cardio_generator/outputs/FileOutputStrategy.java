package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an output strategy that writes data to individual files based on data labels.
 * Each data label is associated with a separate file in the specified base directory.
 * This strategy ensures that data with different labels is stored in separate files for organization.
 */
public class FileOutputStrategy implements OutputStrategy {

    private final String baseDirectory; // Base directory path where files will be stored
    private static final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>();

    /**
     * Constructs a FileOutputStrategy object with the specified base directory.
     *
     * @param baseDirectory The base directory path where files will be stored.
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs the data to a file corresponding to the provided label.
     * If a file corresponding to the label does not exist, a new file is created.
     *
     * @param patientId  The ID of the patient associated with the data.
     * @param timestamp  The timestamp of the data.
     * @param label      The label associated with the data.
     * @param data       The data to be written to the file.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory if it does not exist
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }

        // Set the file path variable based on the label
        String filePath = FILE_MAP.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            // Write patient ID, timestamp, label, and data to the file
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}
