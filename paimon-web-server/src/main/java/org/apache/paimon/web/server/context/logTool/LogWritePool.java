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

package org.apache.paimon.web.server.context.logTool;

import org.apache.paimon.web.server.util.LRUCache;

/** log write pool is used for write logs to process thread local. */
public class LogWritePool extends AbstractPool<LogEntity> {

  private static final LRUCache<String, LogEntity> logEntityCache = new LRUCache<>(64);

  private static final LogWritePool instance = new LogWritePool();

  public static LogWritePool getInstance() {
    return instance;
  }

  @Override
  public LRUCache<String, LogEntity> getLRUCache() {
    return logEntityCache;
  }
}
