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

package org.apache.paimon.web.cryptography;

import org.apache.paimon.web.cyptography.SymmetricCryptoService;
import org.apache.paimon.web.cyptography.SymmetricCryptoServiceFactory;

import java.util.Properties;

/** Factory of {@link Sm4SymmetricCryptoService}. */
public class Sm4SymmetricCryptoServiceFactory implements SymmetricCryptoServiceFactory {

    @Override
    public String name() {
        return SmCryptography.SM4.getType();
    }

    @Override
    public SymmetricCryptoService getSymmetricCryptoService(Properties properties) {
        String key = properties.getProperty("secret-key");
        if (key == null) {
            throw new IllegalArgumentException("SecretKey is required.");
        }
        return new Sm4SymmetricCryptoService(key);
    }
}
