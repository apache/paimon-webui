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

import org.apache.paimon.web.cyptography.DigesterService;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * Sm3 Cryptographic Algorithm.
 */
public class Sm3DigesterService implements DigesterService {

    private String confoundKey;

    public Sm3DigesterService(String confoundKey) {
        Security.addProvider(new BouncyCastleProvider());
        if (confoundKey != null) {
            this.confoundKey = confoundKey;
        }
    }

    @Override
    public String digestHex(String data) {
        String confounded = data + (confoundKey != null ? confoundKey : "");
        byte[] dataBytes = confounded.getBytes(StandardCharsets.UTF_8);
        SM3Digest sm3Digest = new SM3Digest();
        sm3Digest.update(dataBytes, 0, dataBytes.length);
        byte[] result = new byte[sm3Digest.getDigestSize()];
        sm3Digest.doFinal(result, 0);
        return Hex.toHexString(result);
    }

    @Override
    public String digestHex(String data, String salt) {
        return digestHex(data + salt);
    }
}
