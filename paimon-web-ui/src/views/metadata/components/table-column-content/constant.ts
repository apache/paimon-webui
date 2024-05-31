/* Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License. */

export const dataTypeOptions = [
  { value: 'INT', label: 'INT' },
  { value: 'TINYINT', label: 'TINYINT' },
  { value: 'SMALLINT', label: 'SMALLINT' },
  { value: 'BIGINT', label: 'BIGINT' },
  { value: 'CHAR', label: 'CHAR' },
  { value: 'VARCHAR', label: 'VARCHAR' },
  { value: 'STRING', label: 'STRING' },
  { value: 'BINARY', label: 'BINARY' },
  { value: 'VARBINARY', label: 'VARBINARY' },
  { value: 'DOUBLE', label: 'DOUBLE' },
  { value: 'BOOLEAN', label: 'BOOLEAN' },
  { value: 'DATE', label: 'DATE' },
  { value: 'TIME', label: 'TIME' },
  { value: 'TIME(precision)', label: 'TIME(precision)' },
  { value: 'TIMESTAMP', label: 'TIMESTAMP' },
  { value: 'TIMESTAMP(precision)', label: 'TIMESTAMP(precision)' },
  { value: 'TIMESTAMP_MILLIS', label: 'TIMESTAMP_MILLIS' },
  { value: 'BYTES', label: 'BYTES' },
  { value: 'FLOAT', label: 'FLOAT' },
  { value: 'DECIMAL', label: 'DECIMAL' },
  { value: 'TIMESTAMP_WITH_LOCAL_TIME_ZONE', label: 'TIMESTAMP_WITH_LOCAL_TIME_ZONE' },
  { value: 'TIMESTAMP_WITH_LOCAL_TIME_ZONE(precision)', label: 'TIMESTAMP_WITH_LOCAL_TIME_ZONE(precision)' },
]

export const hasLength = ['CHAR', 'VARCHAR', 'BINARY', 'VARBINARY', 'TIME(precision)', 'TIMESTAMP(precision)', 'TIMESTAMP_WITH_LOCAL_TIME_ZONE(precision)', 'DECIMAL']

export const hasEndLength = ['DECIMAL']
