package com.data_management;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActiveAlerts {
    private HashMap<Integer, Alert> alertStatus;

    // Constructor that initializes the HashMap with patient IDs and sets their default value to False
    public ActiveAlerts(ArrayList<Integer> patientIds) {
        alertStatus = new HashMap<>();
        for (Integer id : patientIds) {
            alertStatus.put(id, new Alert(id, false, false, 0L)); // Initialize with False as default value
        }
    }

    // Method to update the value at a specific patientID in the internal HashMap
    public void update(int patientId, boolean updated, boolean newValue, long timestamp) {
        if (alertStatus.containsKey(patientId)) {
            alertStatus.put(patientId, new Alert(patientId, updated, newValue, timestamp)); // Update the value
        } else {
            System.out.println("Error: Patient ID " + patientId + " not found in the error status map.");
        }
    }

    public Alert getAlert(int patientID) {
        return alertStatus.get(patientID);
    }

    public int size() {
        return alertStatus.size();
    }

    public Map<Integer, Alert> getReadOnlyAlertStatus() {
        return Collections.unmodifiableMap(alertStatus);
    }
}