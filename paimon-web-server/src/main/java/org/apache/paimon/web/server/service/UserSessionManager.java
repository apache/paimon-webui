package org.apache.paimon.web.server.service;

import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/** Manages user sessions for Flink SQL Gateway. */
@Service
public class UserSessionManager {

    private final ConcurrentHashMap<String, SessionEntity> sessions = new ConcurrentHashMap<>();

    public SessionEntity getSession(String username) {
        return sessions.get(username);
    }

    public void addSession(String username, SessionEntity session) {
        sessions.put(username, session);
    }
}
