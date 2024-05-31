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

package org.apache.paimon.web.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** FlinkCdcType. */
@Getter
public enum FlinkCdcSyncType {
    SINGLE_TABLE_SYNC(0),
    ALL_DATABASES_SYNC(1);

    private final Integer value;

    private static final Map<Integer, FlinkCdcSyncType> enumsMap;

    static {
        enumsMap =
                Arrays.stream(FlinkCdcSyncType.values())
                        .collect(Collectors.toMap(FlinkCdcSyncType::getValue, Function.identity()));
    }

    FlinkCdcSyncType(Integer value) {
        this.value = value;
    }

    public static FlinkCdcSyncType valueOf(Integer value) {
        if (enumsMap.containsKey(value)) {
            return enumsMap.get(value);
        }
        throw new RuntimeException("Invalid cdc sync type.");
    }
}
