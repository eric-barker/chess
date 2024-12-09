package server.websocket;

import logging.LoggerManager;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Connection that = (Connection) o;
        return Objects.equals(gameID, that.gameID) && Objects.equals(visitorName, that.visitorName) && Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, visitorName, session);
    }
}