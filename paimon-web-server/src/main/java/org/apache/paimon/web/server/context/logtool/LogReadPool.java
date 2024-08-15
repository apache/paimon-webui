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

package org.apache.paimon.web.server.context.logtool;

import org.apache.paimon.web.server.util.LRUCache;

/** log read pool for console read. */
public class LogReadPool extends AbstractPool<StringBuilder> {
    private static final LRUCache<String, StringBuilder> consoleEntityCache = new LRUCache<>(64);

    private static final LogReadPool instance = new LogReadPool();

    public static LogReadPool getInstance() {
        return instance;
    }

    @Override
    public LRUCache<String, StringBuilder> getLRUCache() {
        return consoleEntityCache;
    }

    public static void write(String str, String userId) {
        consoleEntityCache.computeIfAbsent(userId, k -> new StringBuilder()).append(str);
    }

    public static void clear(String userId) {
        consoleEntityCache.put(userId, new StringBuilder());
    }
}
