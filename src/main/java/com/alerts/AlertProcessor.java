package com.alerts;

import com.data_management.ActiveAlerts;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertProcessor {

    private int IRREGULAR_BEAT_THRESHOLD = 5;
    private static final Logger logger = Logger.getLogger(AlertProcessor.class.getName());
    private DataStorage dataStorage;
    private ActiveAlerts activeAlerts;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertProcessor(DataStorage dataStorage, ActiveAlerts activeAlerts) {
        this.dataStorage = dataStorage;
        this.activeAlerts = activeAlerts;

    }



    /**
     * Evaluates the patient data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert} method. This method should define the specific conditions under which an
     * alert will be triggered.
     */
    public void evaluateData() {
        try {

            List<Patient> patients = dataStorage.getAllPatients();
            long currentTime = System.currentTimeMillis();

            for (Patient patient : patients) {
                if (patient != null) {
                    int patientId = patient.getId();

                    // Get records for the patient within the last minute
                    long startTime = currentTime - 60 * 1000; // 1 minute ago
                    long endTime = currentTime; // Current time
                    List<PatientRecord> records = dataStorage.getRecords(patientId, startTime, endTime);


                    // Check if any records are available for the patient
                    if (records != null && !records.isEmpty()) {
                        for (PatientRecord record : records) {
                            if (record != null) {
                                // Check different alert conditions
                                if (checkAlertCondition(record)) {
                                    Alert alert = new Alert(String.valueOf(patientId), record.getRecordType(), record.getTimestamp());
                                    triggerAlert(alert);
                                }
                                if (checkAlertConditionINCorDECTREND(record)) {
                                    Alert alert = new Alert(String.valueOf(patientId), "Increasing or Decreasing trend in blood pressure found", record.getTimestamp());
                                    triggerAlert(alert);
                                }
                                if (checkAlertConditionSaturationDROP(record)) {
                                    Alert alert = new Alert(String.valueOf(patientId), "Rapid Saturation Drop detected", record.getTimestamp());
                                    triggerAlert(alert);
                                }
                                if (checkAlertConditionHypotensiveHypoxemiaAlert(record)) {
                                    Alert alert = new Alert(String.valueOf(patientId), "Hypotensive Hypoxemia detected", record.getTimestamp());
                                    triggerAlert(alert);
                                }
                                if (checkAlertConditionIRREGULARHEARTBEAT(record)) {
                                    Alert alert = new Alert(String.valueOf(patientId), "Irregular heart beat detected", record.getTimestamp());
                                    triggerAlert(alert);
                                }
                            } else {
                                logger.warning("Null record found for patient ID: " + patientId);
                            }
                        }
                    } else {
                        logger.warning("No records found for patient ID: " + patientId);
                    }
                } else {
                    logger.warning("Patient object is null.");
                }
            }
            for (com.data_management.Alert alert: activeAlerts.getReadOnlyAlertStatus().values()) {
                // System.out.println(alert.toString());
                if (alert.getActive() && alert.getUpdated()) {
                    // System.out.println("[][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][]");
                    System.out.println("Manual Alert triggered: ");
                    System.out.println("Timestamp: " + alert.getTimestamp());
                    // sets the newly updated flag to false to prevent multiple trips
                    activeAlerts.update(alert.getPatientId(), false, true, alert.getTimestamp());
                } else {
                    // System.out.println(alert.toString());
                }
            }
        } catch (Exception e) {
            // Log any errors that occur during alert evaluation
            logger.log(Level.SEVERE, "An error occurred while evaluating patient data for alerts", e);
        }
    }



    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        System.out.println("Alert Triggered:");
        System.out.println("Patient ID: " + alert.getPatientId());
        System.out.println("Condition: " + alert.getCondition());
        System.out.println("Timestamp: " + alert.getTimestamp());
    }

    /**
     * Checks if an alert needs to be triggered based on the patient record.
     *
     * @param record the patient record to evaluate
     * @return true if an alert needs to be triggered, false otherwise
     */
    private boolean checkAlertCondition(PatientRecord record) {
        String recordType = record.getRecordType();
        double measurementValue = record.getMeasurementValue();

        // Evaluate blood pressure alerts
        if (recordType.equals("SystolicPressure")) {
            return checkBloodPressureSystolicAlertCondition(measurementValue);
        }


        if (recordType.equals("DiastolicPressure")) {
            return checkBloodPressureDiastolicAlertCondition(measurementValue);
        }
        // Evaluate blood saturation alerts
        if (recordType.equals("Saturation ")) {
            return checkBloodSaturationAlertCondition(measurementValue);
        }

        // Evaluate ECG alerts
        if (recordType.equals("ECG")) {
            return checkECGAlertCondition(measurementValue);
        }


        // No alert condition met by default
        return false;

        }






    private boolean checkAlertConditionINCorDECTREND(PatientRecord record) {
        if(increasingOrDecreasingBloodPressure(record.getPatientId(), System.currentTimeMillis())) {
            return true;
        }

        return false;
    }


    private boolean checkAlertConditionSaturationDROP(PatientRecord record) {
        if(checkRapidDropAlert(record.getPatientId(), System.currentTimeMillis())) {
            return true;
        }

        return false;
    }
    private boolean checkAlertConditionHypotensiveHypoxemiaAlert(PatientRecord record) {
        if(checkHypotensiveHypoxemia(record.getPatientId(), System.currentTimeMillis())) {
            return true;
        }

        return false;
    }
    private boolean checkAlertConditionIRREGULARHEARTBEAT(PatientRecord record) {
        if(checkIrregularBeatPattern(record.getPatientId(), System.currentTimeMillis())) {
            return true;
        }

        return false;
    }

    private boolean checkBloodPressureSystolicAlertCondition(double measurementValue) {
        // Check for critical thresholds
        if (measurementValue > 180 || measurementValue < 90) {
            return true; // Trigger critical threshold alert
        }


        // No alert triggered
        return false;
    }

    private boolean checkBloodPressureDiastolicAlertCondition(double measurementValue) {
        // Check for critical thresholds
        if (measurementValue > 120 || measurementValue < 60) {
            return true; // Trigger critical threshold alert
        }

        // No alert triggered
        return false;
    }

    private boolean checkBloodSaturationAlertCondition(double measurementValue) {
        // Check for low saturation alert
        if (measurementValue < 92) {
            return true; // Trigger low saturation alert
        }

        // No alert triggered
        return false;
    }

    private boolean checkECGAlertCondition(double measurementValue) {
        // Check for abnormal heart rate alert
        if (measurementValue < 50 || measurementValue > 100) {
            return true; // Trigger abnormal heart rate alert
        }

        // No alert triggered
        return false;
    }

    /**
     * Checks if there is an increasing trend in blood pressure readings for the
     * specified patient over three consecutive timestamps, with a one-minute
     * interval between each timestamp.
     *
     * @param patientId   the ID of the patient
     * @param currentTime the current time
     * @return true if there is an increasing trend in blood pressure, false otherwise
     */
    private boolean increasingOrDecreasingBloodPressure(int patientId, long currentTime) {
        // Start checking from the last minute
        long endTime = currentTime;
        long startTime = endTime - 60 * 1000; // One minute ago

        // Retrieve blood pressure records for each timestamp
        List<PatientRecord> records1 = dataStorage.getRecords(patientId, startTime, endTime);
        startTime -= 60 * 1000;
        List<PatientRecord> records2 = dataStorage.getRecords(patientId, startTime, endTime);
        startTime -= 60 * 1000;
        List<PatientRecord> records3 = dataStorage.getRecords(patientId, startTime, endTime);

        // Check if there are records for each timestamp
        if (!records1.isEmpty() && !records2.isEmpty() && !records3.isEmpty()) {
            // Get systolic and diastolic blood pressure readings for each timestamp
            double bp1 = getLatestSystolicBloodPressure(records1);
            double bp2 = getLatestSystolicBloodPressure(records2);
            double bp3 = getLatestSystolicBloodPressure(records3);
            double bp4 = getLatestDiastolicBloodPressure(records1);
            double bp5 = getLatestDiastolicBloodPressure(records2);
            double bp6 = getLatestDiastolicBloodPressure(records3);

            // Check for increasing or decreasing trend
            if ((bp1 - bp2 > 10 && bp2 - bp3 > 10) || (bp2 - bp1 < 10 && bp3 - bp2 < 10) ||
                    (bp4 - bp5 > 10 && bp5 - bp6 > 10) || (bp5 - bp4 < 10 && bp6 - bp5 < 10)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if a rapid drop alert needs to be triggered for a specific patient.
     *
     * @param patientId the unique identifier of the patient
     * @param currentTime the current time in milliseconds
     * @return true if a rapid drop alert needs to be triggered, false otherwise
     */
    private boolean checkRapidDropAlert(int patientId, long currentTime) {
        // Get records for the patient within the last 10 minutes
        long startTime = currentTime - 10 * 60 * 1000; // 10 minutes ago
        long endTime = currentTime; // Current time
        List<PatientRecord> records = dataStorage.getRecords(patientId, startTime, endTime);

        // Check if there are records within the specified time range
        if (records.size() >= 2) {
            // Get the blood saturation readings at the start and end of the interval
            double startSaturation = getLatestBloodSaturation(records.subList(0, 1));
            double endSaturation = getLatestBloodSaturation(records.subList(records.size() - 1, records.size()));

            // Calculate the percentage drop
            double percentageDrop = ((startSaturation - endSaturation) / startSaturation) * 100;

            // Check if there is a drop of 5% or more within 10 minutes
            if (percentageDrop >= 5) {
                return true; // Trigger rapid drop alert
            }
        }
        return false; // No alert triggered
    }
    private boolean checkHypotensiveHypoxemia(int patientId, long currentTime) {
        long startTime = currentTime - 60 * 1000; // 1 minute ago
        long endTime = currentTime; // Current time
        List<PatientRecord> records = dataStorage.getRecords(patientId, startTime, endTime);

        double sys = getLatestSystolicBloodPressure(records);
        double sat = getLatestBloodSaturation(records);

        if(sys< 90 && sat < 92){
            return true;
        }
        return false; // No alert triggered
    }
    private boolean checkIrregularBeatPattern(int patientId, long currentTime) {
        // Calculate the time span of 5 minutes
        long startTime = currentTime - 5 * 60 * 1000; // 5 minutes ago

        // Retrieve ECG records for the patient within the last 5 minutes
        List<PatientRecord> records = dataStorage.getRecords(patientId, startTime, currentTime);

        // Extract heartbeat values from records
        List<Double> heartbeats = getLatestHeartBeats(records);

        // Check for irregular beat patterns
        if (heartbeats.size() >= 2) {
            for (int i = 1; i < heartbeats.size(); i++) {
                double heartbeat1 = heartbeats.get(i - 1);
                double heartbeat2 = heartbeats.get(i);
                if (Math.abs(heartbeat1 - heartbeat2) > IRREGULAR_BEAT_THRESHOLD) {
                    return true; // Trigger irregular beat alert
                }
            }
        }
        return false;
    }

    /**
     * Retrieves the latest systolic blood pressure reading from a list of patient
     * records.
     *
     * @param records the list of patient records
     * @return the latest systolic blood pressure reading
     */
    private double getLatestSystolicBloodPressure(List<PatientRecord> records) {
        for (int i = records.size() - 1; i >= 0; i--) {
            PatientRecord record = records.get(i);
            if (record.getRecordType().equals("SystolicPressure")) {
                return record.getMeasurementValue();
            }
        }
        return 0.0; // Default value if no systolic blood pressure record is found
    }
    private double getLatestDiastolicBloodPressure(List<PatientRecord> records) {
        for (int i = records.size() - 1; i >= 0; i--) {
            PatientRecord record = records.get(i);
            if (record.getRecordType().equals("DiastolicPressure")) {
                return record.getMeasurementValue();
            }
        }
        return 0.0; // Default value if no systolic blood pressure record is found
    }
    /**
     * Retrieves the latest blood saturation reading from a list of patient records.
     *
     * @param records the list of patient records
     * @return the latest blood saturation reading
     */
    private double getLatestBloodSaturation(List<PatientRecord> records) {
        for (int i = records.size() - 1; i >= 0; i--) {
            PatientRecord record = records.get(i);
            if (record.getRecordType().equals("Saturation")) {
                return record.getMeasurementValue();
            }
        }
        return 0.0; // Default value if no blood saturation record is found
    }
    /**
     * Extracts heartbeat values from ECG records.
     *
     * @param records the list of ECG records for the patient
     * @return a list of heartbeat values
     */
    private List<Double> getLatestHeartBeats(List<PatientRecord> records) {
        List<Double> heartbeats = new ArrayList<>();
        for (PatientRecord record : records) {
            heartbeats.add(record.getMeasurementValue());
        }
        return heartbeats;
    }
}