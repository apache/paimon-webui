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

package org.apache.paimon.web.server.configrue.cryptography;

import org.apache.paimon.web.cyptography.DigesterService;
import org.apache.paimon.web.cyptography.DigesterServiceFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;

/** DigesterConfiguration. */
@Getter
@Setter
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "paimon.web.cryptography.digester")
public class DigesterConfiguration {

    private String algorithm;

    private Properties properties;

    @Bean
    public DigesterService digesterService() {
        if (algorithm == null) {
            return new NoneDigesterService();
        }
        ServiceLoader<DigesterServiceFactory> factories =
                ServiceLoader.load(DigesterServiceFactory.class);
        for (DigesterServiceFactory factory : factories) {
            if (Objects.equals(factory.name(), algorithm)) {
                log.info("Digester service found: {}", algorithm);
                return factory.getDigesterService(
                        properties == null ? new Properties() : properties);
            }
        }
        return new NoneDigesterServiceFactory().getDigesterService(properties);
    }

    /**
     * When the encryption algorithm in the configuration file is incorrect, encryption is not
     * performed.
     */
    private static class NoneDigesterService implements DigesterService {
        @Override
        public String digestHex(String data) {
            return data;
        }

        @Override
        public String digestHex(String data, String salt) {
            return data;
        }
    }

    /** Factory of {@link NoneDigesterService } . */
    private static class NoneDigesterServiceFactory implements DigesterServiceFactory {

        @Override
        public String name() {
            return "none";
        }

        @Override
        public DigesterService getDigesterService(Properties properties) {
            return new NoneDigesterService();
        }
    }
}
