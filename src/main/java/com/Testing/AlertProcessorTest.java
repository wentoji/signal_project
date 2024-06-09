//import com.Factories.ECGAlertFactory;
//import com.Strategies.HeartRateStrategy;
//import com.alerts.*;
//import com.data_management.*;
//import com.data_management.Alert;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class AlertProcessorTest {
//    private DataStorage dataStorage;
//    private ActiveAlerts activeAlerts;
//    private AlertProcessor alertProcessor;
//
//    @BeforeEach
//    public void setUp() {
//        dataStorage = new DataStorage();
//        activeAlerts = new ActiveAlerts();
//        alertProcessor = new AlertProcessor(dataStorage, activeAlerts);
//    }
//
//    @Test
//    public void testBloodPressureAlert() {
//        alertProcessor.setAlertFactory(new BloodPressureAlertFactory());
//        alertProcessor.setAlertStrategy(new BloodPressureStrategy());
//
//        Patient patient = new Patient(1, "John Doe");
//        dataStorage.addPatient(patient);
//        PatientRecord record = new PatientRecord(1, "blood_pressure", 150, System.currentTimeMillis());
//        dataStorage.addPatientRecord(1, record);
//
//        alertProcessor.evaluateData();
//
//        Alert alert = activeAlerts.getAlert(1);
//        assertNotNull(alert);
//        assertEquals("blood_pressure", alert.getCondition());
//        assertEquals(String.valueOf(patient.getId()), alert.getPatientId());
//    }
//
//    @Test
//    public void testOxygenSaturationAlert() {
//        alertProcessor.setAlertFactory(new BloodOxygenAlertFactory());
//        alertProcessor.setAlertStrategy(new OxygenSaturationStrategy());
//
//        Patient patient = new Patient(1, "John Doe");
//        dataStorage.addPatient(patient);
//        PatientRecord record = new PatientRecord(1, "oxygen_saturation", 85, System.currentTimeMillis());
//        dataStorage.addPatientRecord(1, record);
//
//        alertProcessor.evaluateData();
//
//        Alert alert = activeAlerts.getAlert(1);
//        assertNotNull(alert);
//        assertEquals("oxygen_saturation", alert.getCondition());
//        assertEquals(String.valueOf(patient.getId()), alert.getPatientId());
//    }
//
//    @Test
//    public void testECGAlert() {
//        alertProcessor.setAlertFactory(new ECGAlertFactory());
//        alertProcessor.setAlertStrategy(new HeartRateStrategy());
//
//        Patient patient = new Patient(1, "John Doe");
//        dataStorage.addPatient(patient);
//        PatientRecord record = new PatientRecord(1, "ecg", 110, System.currentTimeMillis());
//        dataStorage.addPatientRecord(1, record);
//
//        alertProcessor.evaluateData();
//
//        Alert alert = activeAlerts.getAlert(1);
//        assertNotNull(alert);
//        assertEquals("ecg", alert.getCondition());
//        assertEquals(String.valueOf(patient.getId()), alert.getPatientId());
//    }
//}
