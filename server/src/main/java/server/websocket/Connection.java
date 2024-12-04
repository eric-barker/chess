package server.websocket;

import logging.LoggerManager;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.logging.Logger;

public class Connection {

    private static final Logger LOGGER = LoggerManager.getLogger(Connection.class.getName());
    public Integer gameID;
    public String visitorName;
    public Session session;

    public Connection(Integer gameID, String visitorName, Session session) {
        this.visitorName = visitorName;
        this.session = session;
        this.gameID = gameID;
    }

    public void send(String msg) throws IOException {

        session.getRemote().sendString(msg);
        LOGGER.info("msg: " + msg);
    }
}