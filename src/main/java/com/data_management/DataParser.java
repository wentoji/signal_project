package com.data_management;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataParser {
    // Regex pattern to match the lines in the file
    private static final Pattern dataPattern = Pattern.compile(
            "Patient ID: (\\d*), Timestamp: (\\d*), Label: (\\w*), Data: (\\d*\\.\\d*)"
    );

    public void readData(DataStorage storage, String inLabel) throws IOException {
        // File containing the data
        File file = new File("output/" + inLabel + ".txt");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher matcher = dataPattern.matcher(line);
                if (matcher.find()) {
                    // Extracting data using the regex groups
                    int patientId = Integer.parseInt(matcher.group(1));
                    long timestamp = Long.parseLong(matcher.group(2));
                    String label = matcher.group(3);
                    double data = Double.parseDouble(matcher.group(4));

                    // Call the method in DataStorage with extracted values
                    storage.addPatientData(patientId, data, label, timestamp);
                }
            }
        } catch (Exception e) {
            throw new IOException();
        }
    }
}