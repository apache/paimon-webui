/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * reflect util
 */
public class ReflectUtil {

    /** Get the value of a static field */
    public static <T> T getStaticFieldValue(Class<?> clazz, String fieldName) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (!field.getName().equals(fieldName)) {
                continue;
            }
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    return (T) field.get(clazz);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("not found field:" + fieldName);
    }
}
