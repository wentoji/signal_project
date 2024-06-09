package com.Factories;

import com.alerts.Alert;

// BloodOxygenAlertFactory.java
public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, condition, timestamp);
    }
}
