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
import org.apache.paimon.web.api.catalog.CatalogCreator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/** The test class of database manager in {@link DatabaseManager}. */
public class DatabaseManagerTest {

    @TempDir java.nio.file.Path tempFile;
    Catalog catalog;

    @BeforeEach
    public void setUp() {
        String warehouse = tempFile.toUri().toString();
        catalog = CatalogCreator.createFilesystemCatalog(warehouse);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (catalog != null) {
            catalog.close();
        }
    }

    @Test
    public void testDatabaseExists() throws Catalog.DatabaseAlreadyExistException {
        DatabaseManager.createDatabase(catalog, "db_01");
        boolean exists = DatabaseManager.databaseExists(catalog, "db_01");
        assertThat(exists).isTrue();
    }

    @Test
    public void testCreateDatabase() throws Catalog.DatabaseAlreadyExistException {
        DatabaseManager.createDatabase(catalog, "db_01");
        boolean exists = catalog.databaseExists("db_01");
        assertThat(exists).isTrue();

        //  Create database throws DatabaseAlreadyExistException when database already exists
        assertThatExceptionOfType(Catalog.DatabaseAlreadyExistException.class)
                .isThrownBy(() -> catalog.createDatabase("db_01", false))
                .withMessage("Database db_01 already exists.");
    }

    @Test
    public void testDropDatabase() throws Exception {
        DatabaseManager.createDatabase(catalog, "db_01");
        DatabaseManager.dropDatabase(catalog, "db_01");
        boolean exists = catalog.databaseExists("db_01");
        assertThat(exists).isFalse();

        // Drop database throws DatabaseNotExistException when database does not exist
        assertThatExceptionOfType(Catalog.DatabaseNotExistException.class)
                .isThrownBy(() -> DatabaseManager.dropDatabase(catalog, "db_04"))
                .withMessage("Database db_04 does not exist.");
    }

    @Test
    public void testListDatabase() throws Catalog.DatabaseAlreadyExistException {
        DatabaseManager.createDatabase(catalog, "db_01");
        DatabaseManager.createDatabase(catalog, "db_02");
        DatabaseManager.createDatabase(catalog, "db_03");

        List<String> dbs = DatabaseManager.listDatabase(catalog);
        assertThat(dbs).contains("db_01", "db_02", "db_03");
    }
}
