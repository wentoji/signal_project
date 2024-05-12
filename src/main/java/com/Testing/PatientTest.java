package com.Testing;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class PatientTest {

    @Test
    void testGetRecordsWithinTimeRange() {
        // Create a patient
        Patient patient = new Patient(1);

        // Add some records to the patient
        patient.addRecord(100.0, "HeartRate", 1609459200000L); // Jan 1, 2023
        patient.addRecord(105.0, "HeartRate", 1609545600000L); // Jan 2, 2023
        patient.addRecord(110.0, "HeartRate", 1609632000000L); // Jan 3, 2023

        // Get records within a time range (Jan 1, 2023 - Jan 2, 2023)
        List<PatientRecord> records = patient.getRecords(1609459200000L, 1609545600000L);

        // Check if the correct number of records is retrieved
        assertEquals(2, records.size());

        // Check if the records retrieved are correct
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals(1609459200000L, records.get(0).getTimestamp());
        assertEquals(105.0, records.get(1).getMeasurementValue());
        assertEquals(1609545600000L, records.get(1).getTimestamp());
    }

    @Test
    void testGetRecordsOutsideTimeRange() {
        // Create a patient
        Patient patient = new Patient(1);

        // Add some records to the patient
        patient.addRecord(100.0, "HeartRate", 1609459200000L); // Jan 1, 2023
        patient.addRecord(105.0, "HeartRate", 1609545600000L); // Jan 2, 2023
        patient.addRecord(110.0, "HeartRate", 1609632000000L); // Jan 3, 2023

        // Get records within a time range (Jan 4, 2023 - Jan 5, 2023)
        List<PatientRecord> records = patient.getRecords(1609718400000L, 1609804800000L);

        // Check if no records are retrieved
        assertEquals(0, records.size());
    }
}
