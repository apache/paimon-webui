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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test for {@link Sm3DigesterService}. */
public class Sm3DigesterServiceTest {

    @Test
    public void testDigest() throws Exception {
        String data = "admin";
        Sm3DigesterService sm3DigesterService = new Sm3DigesterService(null);
        String result1 = sm3DigesterService.digestHex(data);
        String result2 = sm3DigesterService.digestHex(data);
        assertEquals(result1, result2);
        String salt = "salt";
        String result3 = sm3DigesterService.digestHex(data, salt);
        String result4 = sm3DigesterService.digestHex(data, salt);
        assertEquals(result3, result4);
    }

    @Test
    public void testDigestConfoundKey() throws Exception {
        String data = "admin";
        Sm3DigesterService sm3DigesterService = new Sm3DigesterService("dsadasdw22d");
        String result1 = sm3DigesterService.digestHex(data);
        String result2 = sm3DigesterService.digestHex(data);
        assertEquals(result1, result2);
        String salt = "salt";
        String result3 = sm3DigesterService.digestHex(data, salt);
        String result4 = sm3DigesterService.digestHex(data, salt);
        assertEquals(result3, result4);
    }
}
