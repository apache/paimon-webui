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

import org.apache.paimon.types.BigIntType;
import org.apache.paimon.types.BinaryType;
import org.apache.paimon.types.BooleanType;
import org.apache.paimon.types.CharType;
import org.apache.paimon.types.DataType;
import org.apache.paimon.types.DateType;
import org.apache.paimon.types.DecimalType;
import org.apache.paimon.types.DoubleType;
import org.apache.paimon.types.FloatType;
import org.apache.paimon.types.IntType;
import org.apache.paimon.types.LocalZonedTimestampType;
import org.apache.paimon.types.SmallIntType;
import org.apache.paimon.types.TimeType;
import org.apache.paimon.types.TimestampType;
import org.apache.paimon.types.TinyIntType;
import org.apache.paimon.types.VarBinaryType;
import org.apache.paimon.types.VarCharType;

/** data type convert util. */
public class DataTypeConvertUtils {

    public static DataType convert(PaimonDataType type) {
        switch (type.getType()) {
            case "INT":
                return new IntType(type.isNullable());
            case "TINYINT":
                return new TinyIntType(type.isNullable());
            case "SMALLINT":
                return new SmallIntType(type.isNullable());
            case "BIGINT":
                return new BigIntType(type.isNullable());
            case "CHAR":
                return new CharType(
                        type.isNullable(), type.getPrecision() > 0 ? type.getPrecision() : 1);
            case "VARCHAR":
                return new VarCharType(
                        type.isNullable(), type.getPrecision() > 0 ? type.getPrecision() : 1);
            case "STRING":
                return new VarCharType(type.isNullable(), Integer.MAX_VALUE);
            case "BINARY":
                return new BinaryType(
                        type.isNullable(), type.getPrecision() > 0 ? type.getPrecision() : 1);
            case "VARBINARY":
                return new VarBinaryType(
                        type.isNullable(), type.getPrecision() > 0 ? type.getPrecision() : 1);
            case "DOUBLE":
                return new DoubleType(type.isNullable());
            case "BOOLEAN":
                return new BooleanType(type.isNullable());
            case "DATE":
                return new DateType(type.isNullable());
            case "TIME":
                return new TimeType(type.isNullable(), 0);
            case "TIME(precision)":
                return new TimeType(type.isNullable(), type.getPrecision());
            case "TIMESTAMP":
                return new TimestampType(type.isNullable(), 0);
            case "TIMESTAMP(precision)":
                return new TimestampType(type.isNullable(), type.getPrecision());
            case "TIMESTAMP_MILLIS":
                return new TimestampType(type.isNullable(), 3);
            case "BYTES":
                return new VarBinaryType(type.isNullable(), 0);
            case "FLOAT":
                return new FloatType(type.isNullable());
            case "DECIMAL":
                return new DecimalType(type.isNullable(), type.getPrecision(), type.getScale());
            case "TIMESTAMP_WITH_LOCAL_TIME_ZONE":
                return new LocalZonedTimestampType(type.isNullable(), 0);
            case "TIMESTAMP_WITH_LOCAL_TIME_ZONE(precision)":
                return new LocalZonedTimestampType(type.isNullable(), type.getPrecision());
            default:
                throw new RuntimeException("Invalid type: " + type);
        }
    }

    public static PaimonDataType fromPaimonType(DataType dataType) {
        if (dataType instanceof IntType) {
            return new PaimonDataType("INT", dataType.isNullable(), 0, 0);
        } else if (dataType instanceof TinyIntType) {
            return new PaimonDataType("TINYINT", dataType.isNullable(), 0, 0);
        } else if (dataType instanceof SmallIntType) {
            return new PaimonDataType("SMALLINT", dataType.isNullable(), 0, 0);
        } else if (dataType instanceof BigIntType) {
            return new PaimonDataType("BIGINT", dataType.isNullable(), 0, 0);
        } else if (dataType instanceof VarCharType) {
            VarCharType varCharType = (VarCharType) dataType;
            if (varCharType.getLength() == Integer.MAX_VALUE) {
                return new PaimonDataType("STRING", varCharType.isNullable(), 0, 0);
            } else {
                return new PaimonDataType(
                        "VARCHAR", varCharType.isNullable(), varCharType.getLength(), 0);
            }
        } else if (dataType instanceof CharType) {
            CharType charType = (CharType) dataType;
            return new PaimonDataType("CHAR", charType.isNullable(), charType.getLength(), 0);
        } else if (dataType instanceof BinaryType) {
            BinaryType binaryType = (BinaryType) dataType;
            return new PaimonDataType("BINARY", binaryType.isNullable(), binaryType.getLength(), 0);
        } else if (dataType instanceof VarBinaryType) {
            VarBinaryType varBinaryType = (VarBinaryType) dataType;
            if (varBinaryType.getLength() == Integer.MAX_VALUE) {
                return new PaimonDataType(
                        "BYTES", varBinaryType.isNullable(), varBinaryType.getLength(), 0);
            } else {
                return new PaimonDataType(
                        "VARBINARY", varBinaryType.isNullable(), varBinaryType.getLength(), 0);
            }
        } else if (dataType instanceof DoubleType) {
            return new PaimonDataType("DOUBLE", dataType.isNullable(), 0, 0);
        } else if (dataType instanceof BooleanType) {
            return new PaimonDataType("BOOLEAN", dataType.isNullable(), 0, 0);
        } else if (dataType instanceof DateType) {
            return new PaimonDataType("DATE", dataType.isNullable(), 0, 0);
        } else if (dataType instanceof TimeType) {
            TimeType timeType = (TimeType) dataType;
            if (timeType.getPrecision() == 0) {
                return new PaimonDataType("TIME", timeType.isNullable(), 0, 0);
            } else {
                return new PaimonDataType(
                        "TIME(precision)", timeType.isNullable(), timeType.getPrecision(), 0);
            }
        } else if (dataType instanceof TimestampType) {
            TimestampType timestampType = (TimestampType) dataType;
            if (timestampType.getPrecision() == 0) {
                return new PaimonDataType("TIMESTAMP", timestampType.isNullable(), 0, 0);
            } else if (timestampType.getPrecision() == 3) {
                return new PaimonDataType("TIMESTAMP_MILLIS", timestampType.isNullable(), 3, 0);
            } else {
                return new PaimonDataType(
                        "TIMESTAMP(precision)",
                        timestampType.isNullable(),
                        timestampType.getPrecision(),
                        0);
            }
        } else if (dataType instanceof FloatType) {
            return new PaimonDataType("FLOAT", dataType.isNullable(), 0, 0);
        } else if (dataType instanceof DecimalType) {
            DecimalType decimalType = (DecimalType) dataType;
            return new PaimonDataType(
                    "DECIMAL",
                    decimalType.isNullable(),
                    decimalType.getPrecision(),
                    decimalType.getScale());
        } else if (dataType instanceof LocalZonedTimestampType) {
            LocalZonedTimestampType localZonedTimestampType = (LocalZonedTimestampType) dataType;
            if (localZonedTimestampType.getPrecision() == 0) {
                return new PaimonDataType(
                        "TIMESTAMP_WITH_LOCAL_TIME_ZONE",
                        localZonedTimestampType.isNullable(),
                        0,
                        0);
            } else {
                return new PaimonDataType(
                        "TIMESTAMP_WITH_LOCAL_TIME_ZONE(precision)",
                        localZonedTimestampType.isNullable(),
                        localZonedTimestampType.getPrecision(),
                        0);
            }
        } else {
            return new PaimonDataType("UNKNOWN", dataType.isNullable(), 0, 0);
        }
    }
}
