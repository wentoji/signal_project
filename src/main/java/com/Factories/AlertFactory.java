package com.Factories;

import com.alerts.Alert;

// AlertFactory.java
public abstract class AlertFactory {
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}