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

package org.apache.paimon.web.server.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.Objects;

/** i18n util. */
public class MessageUtils {

    private static final MessageSource MESSAGE_SOURCE = SpringUtils.getBean(MessageSource.class);

    public MessageUtils() {}

    /**
     * According to messageKey and parameters, get the message and delegate to spring messageSource.
     *
     * @param code msg key
     * @return {@link String} internationalization information
     */
    public static String getMsg(Object code) {
        return MESSAGE_SOURCE.getMessage(
                code.toString(), null, code.toString(), LocaleContextHolder.getLocale());
    }

    /**
     * According to messageKey and parameters, get the message and delegate to spring messageSource.
     *
     * @param code msg key
     * @param messageArgs msg parameters
     * @return {@link String} internationalization information
     */
    public static String getMsg(Object code, Object... messageArgs) {
        Object[] objs =
                Arrays.stream(messageArgs)
                        .filter(Objects::nonNull)
                        .map(MessageUtils::getMsg)
                        .toArray();
        return MESSAGE_SOURCE.getMessage(
                code.toString(), objs, code.toString(), LocaleContextHolder.getLocale());
    }
}
