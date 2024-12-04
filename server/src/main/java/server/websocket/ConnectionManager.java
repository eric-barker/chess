package server.websocket;

import com.google.gson.Gson;
import logging.LoggerManager;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConnectionManager {

    private static final Logger LOGGER = LoggerManager.getLogger(ConnectionManager.class.getName());

    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public enum BroadcastType {
        EVERYONE,
        JUST_ME,
        EVERYONE_BUT_ME
    }

    public void add(Integer gameID, String visitorName, Session session) {
        var connection = new Connection(gameID, visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public <T> void broadcast(Integer gameID, String myName, T message, BroadcastType broadcastType) {
        LOGGER.info("Entering broadcast");
        try {
            // Serialize the message using Gson
            String serializedMessage = new Gson().toJson(message);
            LOGGER.info("Serialized message: " + serializedMessage);

            // Gather all connections with the same gameID
            var gameConnections = connections.values().stream()
                    .filter(c -> c.gameID.equals(gameID))
                    .toList(); // Collecting into a list for sequential processing
            LOGGER.info("Found " + gameConnections.size() + " connections for gameID: " + gameID);

            // Exclude the current user
            var targetConnections = gameConnections.stream()
                    .filter(c -> !c.visitorName.equals(myName))
                    .toList();
            LOGGER.info("Excluded current user: " + myName + ", remaining connections: " + targetConnections.size());

            // Just the current user
            var myConnection = gameConnections.stream()
                    .filter(c -> c.visitorName.equals(myName)).toList();
            LOGGER.info("My Connection: " + myConnection + " for " + myName);

            switch (broadcastType) {
                case EVERYONE:
                    for (Connection c : gameConnections) {
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
                    break;
                case JUST_ME:
                    for (Connection c : myConnection) {
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
                    break;
                case EVERYONE_BUT_ME:
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
                    break;
                default:
                    LOGGER.warning("Error: unknown broadcast type");
            }


            // Step 4: Clean up closed connections
            connections.entrySet().removeIf(entry -> !entry.getValue().session.isOpen());
            LOGGER.info("Cleaned up closed connections");
        } catch (Exception e) {
            LOGGER.severe("Error during broadcast: " + e.getMessage());
        }
    }


}