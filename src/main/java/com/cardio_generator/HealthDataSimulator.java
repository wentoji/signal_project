package com.cardio_generator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.Servers.SimpleWebSocketServer;
import com.alerts.AlertProcessor;
import com.cardio_generator.generators.*;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.ActiveAlerts;
import com.data_management.DataStorage;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * HealthDataSimulator simulates health data for multiple patients and outputs it using various strategies.
 */
public class HealthDataSimulator {

    private static final Logger LOGGER = Logger.getLogger(HealthDataSimulator.class.getName());

    private static int patientCount = 50; // Default number of patients
    private static ScheduledExecutorService scheduler;
    private static final Random random = new Random();

    private DataStorage storage;
    private ActiveAlerts activeAlerts;
    private SimpleWebSocketServer webSocketServer;

    private static OutputStrategy strategy;

    /**
     * Main method to start the health data simulation.
     *
     * @param args Command-line arguments.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        HealthDataSimulator simulator = new HealthDataSimulator();
        strategy = new ConsoleOutputStrategy();
        simulator.startSimulation();
    }

    public void startSimulation() throws IOException, URISyntaxException, InterruptedException {
        storage = new DataStorage();
        webSocketServer = new SimpleWebSocketServer(8080,storage); // Specify the port for the WebSocket server

        scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = initializePatientIds(patientCount);

        activeAlerts = new ActiveAlerts((ArrayList<Integer>) patientIds);

        Collections.shuffle(patientIds); // Randomize the order of patient IDs

        scheduleTasksForPatients(patientIds, 5); // Adjust the number of runs as needed
    }

    /**
     * Initializes the list of patient IDs.
     *
     * @param patientCount The number of patients.
     * @return The list of patient IDs.
     */
    private static List<Integer> initializePatientIds(int patientCount) {
        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            patientIds.add(i);
        }
        return patientIds;
    }

    /**
     * Schedules tasks for patients.
     *
     * @param patientIds The list of patient IDs.
     * @param maxRuns    The maximum number of runs.
     */
    private void scheduleTasksForPatients(List<Integer> patientIds, int maxRuns) {
        // Create instances of data generators and the alert generator
        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount, storage);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount, storage);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount, storage);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount, storage);
        AlertProcessor alertProcessor = new AlertProcessor(storage, activeAlerts);

        AtomicInteger runs = new AtomicInteger(); // Counter to track the number of runs

        for (int patientId : patientIds) {
            scheduleTask(() -> {
                // Generate data for each patient
                String ecgData = ecgDataGenerator.generate(patientId, strategy);
                String bloodSaturationData = bloodSaturationDataGenerator.generate(patientId, strategy);
                String bloodPressureData = bloodPressureDataGenerator.generate(patientId, strategy);
                String bloodLevelsData = bloodLevelsDataGenerator.generate(patientId, strategy);
                alertProcessor.evaluateData();

                // Send the data to the WebSocket server
                webSocketServer.sendData(patientId, ecgData, bloodSaturationData, bloodPressureData, bloodLevelsData);

                // Increment the runs counter
                runs.getAndIncrement();

                if (runs.get() >= maxRuns) {
                    // Shutdown the scheduler after reaching the maximum runs
                    scheduler.shutdown();
                }
            }, 1, TimeUnit.SECONDS);
        }
    }




    /**
     * Schedules a task with the given period and time unit.
     *
     * @param task     The task to be scheduled.
     * @param period   The period between consecutive executions.
     * @param timeUnit The time unit of the period.
     */
    private static void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, timeUnit);
    }
}
