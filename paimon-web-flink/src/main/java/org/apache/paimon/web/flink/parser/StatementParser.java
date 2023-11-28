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

package org.apache.paimon.web.flink.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Class for parsing SQL statements. */
public class StatementParser {

    private static final String STATEMENT_SPLIT = ";\n";

    public static String[] parse(String statement) {
        if (statement == null || statement.trim().isEmpty()) {
            return new String[0];
        }

        String[] splits = statement.replace(";\r\n", ";\n").split(STATEMENT_SPLIT);
        String lastStmt = splits[splits.length - 1].trim();
        if (lastStmt.endsWith(";")) {
            splits[splits.length - 1] = lastStmt.substring(0, lastStmt.length() - 1).trim();
        }

        for (int i = 0; i < splits.length; i++) {
            splits[i] = splits[i].replaceAll("(?m)^[ \t]*--.*$(\r\n|\n)?", "").trim();
        }

        return Arrays.stream(splits).filter(s -> !s.isEmpty()).toArray(String[]::new);
    }
}
