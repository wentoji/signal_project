package com.Strategies;

import com.data_management.PatientRecord;

// BloodPressureStrategy.java
public class BloodPressureStrategy implements AlertStrategy {
    @Override
    public boolean checkAlertCondition(PatientRecord record) {
        double measurementValue = record.getMeasurementValue();
        return measurementValue > 180 || measurementValue < 90;
    }
}
