package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A concrete implementation of the OutputStrategy interface that outputs data over Transmission Control Protocol (TCP)/IP.
 * TCP/IP: Transmission Control Protocol (TCP) and Internet Protocol (IP) are foundational protocols of the internet.
 * TCP provides reliable, ordered, and error-checked delivery of data between applications, while IP handles the routing of packets across networks.
 * Combined, TCP/IP forms the basis for most internet communication, including web browsing, email, and file transfer.
 * The TcpOutputStrategy class utilizes TCP/IP to establish connections and transmit data between the server and clients.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private static final Logger LOGGER = Logger.getLogger(TcpOutputStrategy.class.getName());

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a TcpOutputStrategy object that listens for client connections on the specified port.
     *
     * @param port The port number on which the TCP server will listen for client connections.
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    LOGGER.info("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error accepting client connection", e);
                }
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting TCP server", e);
        }
    }

    /**
     * Outputs the provided data over the established TCP connection.
     *
     * @param patientId The ID of the patient associated with the data.
     * @param timestamp The timestamp of the data.
     * @param label     The label describing the data.
     * @param data      The actual data to be output.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        } else {
            LOGGER.warning("TCP connection not established, data not sent");
        }
    }
}
