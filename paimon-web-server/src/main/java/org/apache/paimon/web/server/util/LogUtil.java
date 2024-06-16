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

package org.apache.paimon.web.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/** log util */
public class LogUtil {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public static String getError(Throwable e) {
        String error = null;
        try (StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            error = sw.toString();
            logger.error(error);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            return error;
        }
    }

    public static String getError(String msg, Throwable e) {
        String error = null;
        try (StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            LocalDateTime now = LocalDateTime.now();
            error = now + ": " + msg + " \nError message:\n " + sw.toString();
            logger.error(error);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            return error;
        }
    }
}
