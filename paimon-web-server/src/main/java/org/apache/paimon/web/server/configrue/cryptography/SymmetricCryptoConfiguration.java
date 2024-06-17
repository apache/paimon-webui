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

import org.apache.paimon.web.cyptography.SymmetricCryptoService;
import org.apache.paimon.web.cyptography.SymmetricCryptoServiceFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;

/** SymmetricCryptoConfiguration. */
@Getter
@Setter
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "paimon.web.cryptography.symmetric")
public class SymmetricCryptoConfiguration {

    private String algorithm;

    private Properties properties;

    @Bean
    public SymmetricCryptoService symmetricCryptoService() {
        if (algorithm == null) {
            algorithm = "none";
        }
        ServiceLoader<SymmetricCryptoServiceFactory> factories =
                ServiceLoader.load(SymmetricCryptoServiceFactory.class);
        for (SymmetricCryptoServiceFactory factory : factories) {
            if (Objects.equals(factory.name(), algorithm)) {
                log.info("Symmetric crypto service found: {}", algorithm);
                return factory.getSymmetricCryptoService(
                        properties == null ? new Properties() : properties);
            }
        }
        throw new RuntimeException("Could not find suitable SymmetricCryptoServiceFactory.");
    }
}
