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

package org.apache.paimon.web.api.action.context.factory;

import org.apache.paimon.web.api.enums.FlinkCdcType;
import org.apache.paimon.web.api.exception.ActionException;

import java.util.Objects;
import java.util.ServiceLoader;

/** ActionContextFactoryServiceLoadUtil. */
public class ActionContextFactoryServiceLoadUtil {

    private ActionContextFactoryServiceLoadUtil() {}

    public static FlinkCdcActionContextFactory getFlinkCdcActionContextFactory(
            String sourceType, String targetType, FlinkCdcType flinkCdcType) {
        ServiceLoader<FlinkCdcActionContextFactory> serviceLoader =
                ServiceLoader.load(FlinkCdcActionContextFactory.class);
        for (FlinkCdcActionContextFactory factory : serviceLoader) {
            if (factory.cdcType() == flinkCdcType
                    && Objects.equals(factory.sourceType(), sourceType)
                    && Objects.equals(factory.targetType(), targetType)) {
                return factory;
            }
        }
        throw new ActionException(("Could not find suitable FlinkCdcActionContextFactory."));
    }
}
