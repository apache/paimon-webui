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

package org.apache.paimon.web.server.util;

import java.util.LinkedHashMap;
import java.util.Map;

/** LRU Cache. */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int cacheSize;

    /**
     * Initialize LRUCache with cache size.
     *
     * @param cacheSize Cache size
     */
    public LRUCache(int cacheSize) {
        // true means based on access order
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        this.cacheSize = cacheSize;
    }

    /**
     * When map size over CACHE_SIZE, remove oldest entry.
     *
     * @param eldest The least recently accessed entry in the map
     * @return boolean value indicates whether the oldest entry should be removed
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > this.cacheSize;
    }
}
