package com.Strategies;

import com.data_management.PatientRecord;

// HeartRateStrategy.java
public class HeartRateStrategy implements AlertStrategy {
    @Override
    public boolean checkAlertCondition(PatientRecord record) {
        double measurementValue = record.getMeasurementValue();
        return measurementValue < 50 || measurementValue > 100;
    }
}
