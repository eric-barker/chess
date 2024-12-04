package server.websocket;

import com.google.gson.Gson;
import logging.LoggerManager;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ConnectServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ConnectionManager {

    private static final Logger LOGGER = LoggerManager.getLogger(ConnectionManager.class.getName());

    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String visitorName, Session session) {
        var connection = new Connection(gameID, visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public <T> void broadcast(Integer gameID, String excludeVisitorName, T message) {
        LOGGER.info("Entering broadcast");
        try {
            // Serialize the message using Gson
            String serializedMessage = new Gson().toJson(message);
            LOGGER.info("Serialized message: " + serializedMessage);

            // Step 1: Gather all connections with the same gameID
            var gameConnections = connections.values().stream()
                    .filter(c -> c.gameID.equals(gameID))
                    .toList(); // Collecting into a list for sequential processing
            LOGGER.info("Found " + gameConnections.size() + " connections for gameID: " + gameID);

            // Step 2: Exclude the current user
            var targetConnections = gameConnections.stream()
                    .filter(c -> !c.visitorName.equals(excludeVisitorName))
                    .toList();
            LOGGER.info("Excluded current user: " + excludeVisitorName + ", remaining connections: " + targetConnections.size());

            // Step 3: Send the message to relevant connections
            for (Connection c : targetConnections) {
                if (c.session.isOpen()) {
                    try {
                        c.send(serializedMessage);
                        LOGGER.info("Message sent to " + c.visitorName);
                    } catch (IOException e) {
                        LOGGER.severe("Failed to send message to " + c.visitorName + ": " + e.getMessage());
                    }
                } else {
                    LOGGER.warning("Session for " + c.visitorName + " is closed and will be cleaned up");
                }
            }

            // Step 4: Clean up closed connections
            connections.entrySet().removeIf(entry -> !entry.getValue().session.isOpen());
            LOGGER.info("Cleaned up closed connections");
        } catch (Exception e) {
            LOGGER.severe("Error during broadcast: " + e.getMessage());
        }
    }


}