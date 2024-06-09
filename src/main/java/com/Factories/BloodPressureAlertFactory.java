package com.Factories;

import com.alerts.Alert;

// BloodPressureAlertFactory.java
public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, condition, timestamp);
    }
}