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

import { useCDCList } from './cdc/use-cdc-list'
import { useMYSQL } from './cdc/use-mysql'
import { usePaimon } from './cdc/use-paimon'

import { useDatabase } from './metadata/use-database'
import { useOptions } from './metadata/use-options'

export default {
  CDCLIST: useCDCList,
  MYSQL: useMYSQL,
  PAIMON: usePaimon,
  DATABASE: useDatabase,
  OPTIONS: useOptions
}
