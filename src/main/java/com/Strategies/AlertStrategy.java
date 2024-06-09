package com.Strategies;

import com.data_management.PatientRecord;

// AlertStrategy.java
public interface AlertStrategy {
    boolean checkAlertCondition(PatientRecord record);
}