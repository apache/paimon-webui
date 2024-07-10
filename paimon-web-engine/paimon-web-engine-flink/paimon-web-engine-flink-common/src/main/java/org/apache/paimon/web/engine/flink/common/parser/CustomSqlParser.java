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

package org.apache.paimon.web.engine.flink.common.parser;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.flink.sql.parser.impl.FlinkSqlParserImpl;
import org.apache.flink.sql.parser.validate.FlinkSqlConformance;

/** CustomSqlParser to parse Sql list. */
public class CustomSqlParser {

    private static final SqlParser.Config config;
    private final SqlParser parser;
    private final int limit;

    private static final int DEFAULT_LIMIT = 500;

    static {
        config =
                SqlParser.config()
                        .withParserFactory(FlinkSqlParserImpl.FACTORY)
                        .withConformance(FlinkSqlConformance.DEFAULT)
                        .withLex(Lex.JAVA)
                        .withIdentifierMaxLength(256);
    }

    public CustomSqlParser(String sql) {
        this(sql, DEFAULT_LIMIT);
    }

    public CustomSqlParser(String sql, int limit) {
        this.parser = SqlParser.create(sql, config);
        this.limit = limit;
    }

    public SqlNodeList parseStmtList() throws SqlParseException {
        SqlNodeList nodeList = parser.parseStmtList();
        for (SqlNode node : nodeList) {
            if (node instanceof SqlSelect) {
                SqlSelect select = (SqlSelect) node;
                if (!hasAggregateOrGroupBy(select) && select.getFetch() == null) {
                    SqlLiteral sqlLiteral =
                            SqlLiteral.createExactNumeric(String.valueOf(limit), SqlParserPos.ZERO);
                    select.setFetch(sqlLiteral);
                }
            }
        }
        return nodeList;
    }

    private boolean hasAggregateOrGroupBy(SqlSelect select) {
        if (select.getGroup() != null && !select.getGroup().isEmpty()) {
            return true;
        }
        return containsComplexOperations(select.getSelectList());
    }

    private boolean containsComplexOperations(SqlNodeList nodes) {
        if (nodes != null) {
            for (SqlNode node : nodes) {
                if (!(node instanceof SqlIdentifier)) {
                    return true;
                }
            }
        }
        return false;
    }
}
