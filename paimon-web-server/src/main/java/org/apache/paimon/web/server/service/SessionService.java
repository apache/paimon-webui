package org.apache.paimon.web.server.service;

import org.apache.paimon.web.server.data.dto.SessionDTO;

/** Session Service. */
public interface SessionService {

    /**
     * Creates a new session based on the provided session data transfer object (DTO).
     *
     * @param sessionDTO the session DTO containing the necessary data to create a new session
     * @return {@code true} if the session was successfully created, {@code false} otherwise
     * @throws Exception if there is an issue during the session creation process
     */
    void createSession(SessionDTO sessionDTO);

    /**
     * Closes an existing session identified by the session data transfer object (DTO).
     *
     * @param sessionDTO the session DTO containing the identifier of the session to be closed
     * @return {@code true} if the session was successfully closed, {@code false} otherwise
     * @throws Exception if there is an issue during the session closure process
     */
    void closeSession(SessionDTO sessionDTO);

    /**
     * Triggers a heartbeat event for an existing session identified by the session DTO. This is
     * typically used to keep the session active.
     *
     * @param sessionDTO the session DTO containing the identifier of the session for which the
     *     heartbeat is to be triggered
     * @return a positive integer if the heartbeat was successfully triggered, 0 otherwise
     * @throws Exception if there is an issue during the heartbeat triggering process
     */
    int triggerSessionHeartbeat(SessionDTO sessionDTO);
}
