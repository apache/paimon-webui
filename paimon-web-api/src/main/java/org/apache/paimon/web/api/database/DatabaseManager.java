/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.paimon.web.api.database;

import org.apache.paimon.catalog.Catalog;

import java.util.List;

/** paimon database manager. */
public class DatabaseManager {

    public static void createDatabase(Catalog catalog, String name)
            throws Catalog.DatabaseAlreadyExistException {
        catalog.createDatabase(name, false);
    }

    public static boolean databaseExists(Catalog catalog, String name) {
        return catalog.databaseExists(name);
    }

    public static List<String> listDatabase(Catalog catalog) {
        return catalog.listDatabases();
    }

    public static void dropDatabase(Catalog catalog, String name)
            throws Catalog.DatabaseNotEmptyException, Catalog.DatabaseNotExistException {
        catalog.dropDatabase(name, false, true);
    }
}
