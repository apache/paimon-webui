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

package org.apache.paimon.web.engine.flink.sql.gateway.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Utility class for building Flink SQL STATEMENT SET strings for batch execution.
 *
 * <p>This class provides a static method to construct a STATEMENT SET from a list of individual
 * INSERT statements. If there is only one INSERT statement, it returns the statement as is. For
 * multiple statements, it wraps them in a Flink SQL EXECUTE STATEMENT SET block.
 */
public class FlinkSqlStatementSetBuilder {

    private static final String EXECUTE_STATEMENT_SET = "EXECUTE STATEMENT SET\n";
    private static final String BEGIN_STATEMENT = "BEGIN\n";
    private static final String END_STATEMENT = "END;\n";
    private static final String STATEMENT_DELIMITER = ";\n";

    public static String buildStatementSet(List<String> insertStatements) {
        StringBuilder statementSetBuilder = new StringBuilder();

        if (CollectionUtils.isNotEmpty(insertStatements)) {
            if (insertStatements.size() > 1) {
                statementSetBuilder.append(EXECUTE_STATEMENT_SET);
                statementSetBuilder.append(BEGIN_STATEMENT);

                for (String insertStatement : insertStatements) {
                    statementSetBuilder.append(insertStatement);
                    statementSetBuilder.append(STATEMENT_DELIMITER);
                }

                statementSetBuilder.append(END_STATEMENT);
            } else {
                statementSetBuilder.append(insertStatements.get(0));
                statementSetBuilder.append(STATEMENT_DELIMITER);
            }
        }

        return statementSetBuilder.toString();
    }
}
