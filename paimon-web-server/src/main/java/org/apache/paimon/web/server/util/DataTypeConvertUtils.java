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

import org.apache.paimon.types.DataType;
import org.apache.paimon.types.DataTypes;

/** data type convert util. */
public class DataTypeConvertUtils {

    public static DataType convert(String type) {
        switch (type) {
            case "INT":
                return DataTypes.INT();
            case "TINYINT":
                return DataTypes.TINYINT();
            case "SMALLINT":
                return DataTypes.SMALLINT();
            case "BIGINT":
                return DataTypes.BIGINT();
            case "STRING":
                return DataTypes.STRING();
            case "DOUBLE":
                return DataTypes.DOUBLE();
            case "BOOLEAN":
                return DataTypes.BOOLEAN();
            case "DATE":
                return DataTypes.DATE();
            case "TIME":
                return DataTypes.TIME();
            case "TIMESTAMP":
                return DataTypes.TIMESTAMP();
            case "BYTES":
                return DataTypes.BYTES();
            case "FLOAT":
                return DataTypes.FLOAT();
            case "DECIMAL":
                return DataTypes.DECIMAL(38, 0);
            default:
                throw new RuntimeException("Invalid type: " + type);
        }
    }

    public static String fromPaimonType(DataType dataType) {
        if (dataType.equals(DataTypes.INT())) {
            return "INT";
        } else if (dataType.equals(DataTypes.TINYINT())) {
            return "TINYINT";
        } else if (dataType.equals(DataTypes.SMALLINT())) {
            return "SMALLINT";
        } else if (dataType.equals(DataTypes.BIGINT())) {
            return "BIGINT";
        } else if (dataType.equals(DataTypes.STRING())) {
            return "STRING";
        } else if (dataType.equals(DataTypes.DOUBLE())) {
            return "DOUBLE";
        } else if (dataType.equals(DataTypes.BOOLEAN())) {
            return "BOOLEAN";
        } else if (dataType.equals(DataTypes.DATE())) {
            return "DATE";
        } else if (dataType.equals(DataTypes.TIME())) {
            return "TIME";
        } else if (dataType.equals(DataTypes.TIMESTAMP())) {
            return "TIMESTAMP";
        } else if (dataType.equals(DataTypes.BYTES())) {
            return "BYTES";
        } else if (dataType.equals(DataTypes.FLOAT())) {
            return "FLOAT";
        } else if (dataType.equals(DataTypes.DECIMAL(38, 0))) {
            return "DECIMAL";
        } else {
            return "UNKNOWN";
        }
    }
}
