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

import { IconPlus } from "@douyinfe/semi-icons";
import CatalogTree from "@pages/Metadata/components/LeftContent/components/CatalogTree";
import { useState } from "react";
import CatalogModalForm from "@pages/Metadata/components/LeftContent/components/CatalogModalForm";
import {useCatalogStore} from "@src/store/catalogStore.ts";
import { useTranslation } from 'react-i18next';
import {useDatabaseStore} from "@src/store/databaseStore.ts";
import {useTableStore} from "@src/store/tableStore.ts";
import {Tooltip} from "@douyinfe/semi-ui";
import styles from "./left-content.module.less";


const MetadataSidebar = () => {

    const [showModal, setShowModal] = useState(false);
    const createFilesystemCatalog = useCatalogStore(state => state.createFileSystemCatalog);
    const createHiveCatalog = useCatalogStore(state => state.createHiveCatalog);
    const fetchCatalogData = useCatalogStore(state => state.fetchCatalogData);
    const fetchAllDatabases = useDatabaseStore(state => state.fetchDatabases);
    const fetchTables = useTableStore(state => state.fetchTables);

    const handleOpenModal = () => {
        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
    };

    const handleOk = (formApi: any) => {
        return new Promise<void>((resolve, reject) => {
            formApi
                .validate()
                .then((values: any) => {
                    // Assuming values contains the form data
                    const formData = values;

                    const catalogProp: Prop.CatalogProp = {
                        catalogName: formData.catalogName,
                        catalogType: formData.catalogType,
                        warehouse: formData.warehouse,
                        hiveUri: formData.hiveUri,
                        hiveConfDir: formData.hiveConfDir,
                        isDelete: false
                    };

                    if (formData.catalogType === 'filesystem') {
                        createFilesystemCatalog(catalogProp)
                            .then(() => {
                                fetchCatalogData();
                                fetchAllDatabases();
                                fetchTables();
                                resolve();
                            })
                    } else {
                        createHiveCatalog(catalogProp)
                            .then(() => {
                                fetchCatalogData();
                                fetchAllDatabases();
                                fetchTables();
                                resolve();
                            })
                    }
                    resolve();
                })
                .catch((errors: any) => {
                    console.log(errors);
                    reject(errors);
                });
        });
    };

    const { t } = useTranslation()
    return(
        <div className={styles.container}>
            <div className={styles['add-catalog-container']}>
                <span>{t('metadata.catalog')}</span>
                <Tooltip content={t('metadata.register-catalog')}>
                    <IconPlus className={styles.iconPlus}  onClick={handleOpenModal}/>
                </Tooltip>
            </div>
            <CatalogTree/>
            {showModal && (
                <CatalogModalForm visible={showModal} onClose={handleCloseModal} onOk={handleOk}/>
            )}
        </div>
    )
}

export default MetadataSidebar;
