/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.server.service;

import org.apache.paimon.web.engine.flink.sql.gateway.model.SessionEntity;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/** Manages user sessions for Flink SQL Gateway. */
@Service
public class UserSessionManager {

    private final ConcurrentHashMap<String, SessionEntity> sessions = new ConcurrentHashMap<>();

    public SessionEntity getSession(String id) {
        return sessions.get(id);
    }

    public void addSession(String id, SessionEntity session) {
        sessions.put(id, session);
    }

    public void removeSession(String id) {
        sessions.remove(id);
    }

    public List<SessionEntity> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }
}
