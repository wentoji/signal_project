package com.Strategies;

import com.data_management.PatientRecord;

// OxygenSaturationStrategy.java
public class OxygenSaturationStrategy implements AlertStrategy {
    @Override
    public boolean checkAlertCondition(PatientRecord record) {
        double measurementValue = record.getMeasurementValue();
        return measurementValue < 92;
    }
}
