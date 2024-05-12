package com.data_management;

public class Alert {
    private int patientId;
    private boolean updated;
    private long timestamp;
    private boolean active;

    public Alert(int patientId, boolean updated, boolean active, long timestamp) {
        this.patientId = patientId;
        this.updated = updated;
        this.active = active;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return patientId + ": status: " + active + " updated: " + updated;
    }

    public int getPatientId() {
        return patientId;
    }

    public boolean getUpdated() {
        return updated;
    }

    public boolean getActive() {
        return active;
    }


    public long getTimestamp() {
        return timestamp;
    }
}
