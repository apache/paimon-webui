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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.Security;

/** SM4 Cryptographic Algorithm. */
public class Sm4SymmetricCryptoService implements SymmetricCryptoService {

    private final SecretKey secretKey;

    public Sm4SymmetricCryptoService(String secretKey) {
        Security.addProvider(new BouncyCastleProvider());
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "SM4");
    }

    @Override
    public String encrypt(String data) {
        byte[] keyBytes = data.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        try {
            byte[] bytes = cipher.doFinal(keyBytes);
            return Hex.toHexString(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher getCipher(int mode) {
        try {
            Cipher cipher =
                    Cipher.getInstance("SM4/ECB/PKCS5Padding", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(mode, secretKey);
            return cipher;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not initialize SM4/ECB/PKCS5Padding.", e);
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            byte[] decode = Hex.decode(data);
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] bytes = cipher.doFinal(decode);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
