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

import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.parser.SqlParseException;
import org.junit.jupiter.api.Test;

import static org.apache.paimon.web.engine.flink.common.parser.StatementsConstant.statement1;
import static org.apache.paimon.web.engine.flink.common.parser.StatementsConstant.statement2;
import static org.apache.paimon.web.engine.flink.common.parser.StatementsConstant.statement3;
import static org.assertj.core.api.Assertions.assertThat;

/** Tests of {@link CustomSqlParser}. */
public class CustomSqlParserTest {

    @Test
    public void testParse() throws SqlParseException {
        CustomSqlParser customSqlParser = new CustomSqlParser(statement1);
        SqlNodeList sqlNodeList = customSqlParser.parseStmtList();
        assertThat(sqlNodeList.size()).isEqualTo(5);
    }

    @Test
    public void testSelectLimit() throws SqlParseException {
        CustomSqlParser customSqlParser = new CustomSqlParser(statement2);
        String actual = customSqlParser.parseStmtList().get(2).toString();
        assertThat(actual)
                .isEqualToIgnoringWhitespace("SELECT * FROM `t_order` FETCH NEXT 500 ROWS ONLY");
    }

    @Test
    public void testSelectWithoutLimit() throws SqlParseException {
        CustomSqlParser customSqlParser = new CustomSqlParser(statement3);
        String actual = customSqlParser.parseStmtList().get(2).toString();
        assertThat(actual).isEqualToIgnoringWhitespace("SELECT COUNT(*) FROM `t_order`");
    }
}
