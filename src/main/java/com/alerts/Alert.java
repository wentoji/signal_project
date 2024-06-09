package com.alerts;

public class Alert implements AlertComponent {
    private String patientId;
    private String condition;
    private long timestamp;

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void sendAlert() {
        System.out.println("Alert Triggered:");
        System.out.println("Patient ID: " + patientId);
        System.out.println("Condition: " + condition);
        System.out.println("Timestamp: " + timestamp);
    }
}
