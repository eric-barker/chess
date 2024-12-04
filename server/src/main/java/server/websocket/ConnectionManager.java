package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String visitorName, Session session) {
        var connection = new Connection(gameID, visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public <T> void broadcast(Integer gameID, String excludeVisitorName, T message) {
        try {
            // Serialize the message using Gson
            String serializedMessage = new Gson().toJson(message);

            // Stream connections and send the message to relevant sessions
            connections.values().stream()
                    .filter(c -> c.session.isOpen() &&
                            c.gameID.equals(gameID) &&
                            !c.visitorName.equals(excludeVisitorName))
                    .forEach(c -> {
                        try {
                            c.send(serializedMessage);
                        } catch (IOException e) {
                            System.err.println("Failed to send message to " + c.visitorName + ": " + e.getMessage());
                        }
                    });

            // Clean up closed connections
            connections.entrySet().removeIf(entry -> !entry.getValue().session.isOpen());
        } catch (Exception e) {
            System.err.println("Error during broadcast: " + e.getMessage());
        }
    }

}