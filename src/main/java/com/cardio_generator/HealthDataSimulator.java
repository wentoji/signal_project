package com.cardio_generator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alerts.AlertProcessor;
import com.cardio_generator.generators.*;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
import com.data_management.ActiveAlerts;
import com.data_management.DataStorage;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * HealthDataSimulator simulates health data for multiple patients and outputs it using various strategies.
 */
public class HealthDataSimulator {

    private static final Logger LOGGER = Logger.getLogger(HealthDataSimulator.class.getName());

    private static int patientCount = 50; // Default number of patients
    private static ScheduledExecutorService scheduler;
    private static OutputStrategy outputStrategy = new ConsoleOutputStrategy(); // Default output strategy
    private static final Random random = new Random();

    private DataStorage storage;
    private ActiveAlerts activeAlerts;

    /**
     * Main method to start the health data simulation.
     *
     * @param args Command-line arguments.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        parseArguments(args);

        HealthDataSimulator simulator = new HealthDataSimulator();
        simulator.startSimulation();
    }
    public void startSimulation() {
        storage = new DataStorage();

        scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = initializePatientIds(patientCount);

        activeAlerts = new ActiveAlerts((ArrayList<Integer>) patientIds);

        Collections.shuffle(patientIds); // Randomize the order of patient IDs

        scheduleTasksForPatients(patientIds, 5); // Adjust the number of runs as needed
    }

    /**
     * Parses command-line arguments.
     *
     * @param args The command-line arguments.
     * @throws IOException If an I/O error occurs.
     */
    private static void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            LOGGER.warning("Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];
                        if (outputArg.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();
                        } else if (outputArg.startsWith("file:")) {
                            String baseDirectory = outputArg.substring(5);
                            Path outputPath = Paths.get(baseDirectory);
                            if (!Files.exists(outputPath)) {
                                Files.createDirectories(outputPath);
                            }
                            outputStrategy = new FileOutputStrategy(baseDirectory);
                        } else if (outputArg.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(10));
                                outputStrategy = new WebSocketOutputStrategy(port);
                                LOGGER.info("WebSocket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                LOGGER.log(Level.SEVERE, "Invalid port for WebSocket output. Please specify a valid port number.", e);
                            }
                        } else if (outputArg.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(4));
                                outputStrategy = new TcpOutputStrategy(port);
                                LOGGER.info("TCP socket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                LOGGER.log(Level.SEVERE, "Invalid port for TCP output. Please specify a valid port number.", e);
                            }
                        } else {
                            LOGGER.warning("Unknown output type. Using default (console).");
                        }
                    }
                    break;
                default:
                    LOGGER.warning("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Prints the help message.
     */
    private static void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println("  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method. Options are:");
        System.out.println("                             'console' for console output,");
        System.out.println("                             'file:<directory>' for file output,");
        System.out.println("                             'websocket:<port>' for WebSocket output,");
        System.out.println("                             'tcp:<port>' for TCP socket output.");
        System.out.println("Example:");
        System.out.println("  java HealthDataSimulator --patient-count 100 --output websocket:8080");
        System.out.println("  This command simulates data for 100 patients and sends the output to WebSocket clients connected to port 8080.");
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
     */
    /**
     * Schedules tasks for patients.
     *
     * @param patientIds The list of patient IDs.
     * @param maxRuns    The maximum number of runs.
     */
    private void scheduleTasksForPatients(List<Integer> patientIds, int maxRuns) {
        // Create instances of data generators and the alert generator

        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount,storage);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount,storage);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount,storage);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount,storage);
        AlertGenerator alertGenerator = new AlertGenerator(storage, activeAlerts);
        AlertProcessor alertProcessor = new AlertProcessor(storage, activeAlerts);

        AtomicInteger runs = new AtomicInteger(); // Counter to track the number of runs

        for (int patientId : patientIds) {
            scheduleTask(() -> {
                // Generate data for each patient and store it in the data storage
                ecgDataGenerator.generate(patientId, outputStrategy);
                bloodSaturationDataGenerator.generate(patientId, outputStrategy);
                bloodPressureDataGenerator.generate(patientId, outputStrategy);
                bloodLevelsDataGenerator.generate(patientId, outputStrategy);
                alertGenerator.generate(patientId, outputStrategy);
                runs.getAndIncrement(); // Increment the runs counter

                // Evaluate alerts after generating data for each patient
                alertProcessor.evaluateData();

                if (runs.get() >= maxRuns) {
                    scheduler.shutdown(); // Shutdown the scheduler after reaching the maximum runs
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
