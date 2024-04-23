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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests of {@link StatementParser}. */
public class StatementParserTest {

    private String statement =
            "DROP TABLE IF EXISTS t_order;\n"
                    + "CREATE TABLE IF NOT EXISTS t_order(\n"
                    + "    --order id\n"
                    + "    `order_id` BIGINT,\n"
                    + "    --product\n"
                    + "    `product` BIGINT,\n"
                    + "    --amount\n"
                    + "    `amount` BIGINT,\n"
                    + "    --payment time\n"
                    + "    `order_time` as CAST(CURRENT_TIMESTAMP AS TIMESTAMP(3)),\n"
                    + "    --WATERMARK\n"
                    + "    WATERMARK FOR order_time AS order_time-INTERVAL '2' SECOND\n"
                    + ") WITH(\n"
                    + "    'connector' = 'datagen',\n"
                    + "    'rows-per-second' = '1',\n"
                    + "    'fields.order_id.min' = '1',\n"
                    + "    'fields.order_id.max' = '2',\n"
                    + "    'fields.amount.min' = '1',\n"
                    + "    'fields.amount.max' = '10',\n"
                    + "    'fields.product.min' = '1',\n"
                    + "    'fields.product.max' = '2'\n"
                    + ");\n"
                    + "-- SELECT * FROM t_order LIMIT 10;\n"
                    + "DROP TABLE IF EXISTS sink_table;\n"
                    + "CREATE TABLE IF NOT EXISTS sink_table(\n"
                    + "    --product\n"
                    + "    `product` BIGINT,\n"
                    + "    --amount\n"
                    + "    `amount` BIGINT,\n"
                    + "    --payment time\n"
                    + "    `order_time` TIMESTAMP(3),\n"
                    + "    `one_minute_sum` BIGINT\n"
                    + ") WITH('connector' = 'print');\n"
                    + "\n"
                    + "INSERT INTO\n"
                    + "    sink_table\n"
                    + "SELECT\n"
                    + "    product,\n"
                    + "    amount,\n"
                    + "    order_time,\n"
                    + "    0 as one_minute_sum\n"
                    + "FROM\n"
                    + "    t_order;";

    @Test
    public void testParse() {
        String[] statements = StatementParser.parse(statement);
        assertThat(statements.length).isEqualTo(5);
    }
}
