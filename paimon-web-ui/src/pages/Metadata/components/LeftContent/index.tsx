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
import {useState} from "react";
import CatalogModalForm from "@pages/Metadata/components/LeftContent/components/CatalogModalForm";
import Api from "@api/api.ts";
import styles from "./left-content.module.less";

const MetadataSidebar = () => {

    const [showModal, setShowModal] = useState(false);

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

                    if (formData.catalog === 'filesystem') {
                        const catalogProp: Prop.FileSystemCatalogProp = {
                            catalogName: formData.catalog,
                            catalogType: 'filesystem',
                            path: formData.path
                        };
                        let catalog = Api.createFileSystemCatalog(catalogProp);
                        console.log(catalog);
                    } else {

                    }
                    resolve(); // Resolve the promise when done
                })
                .catch((errors: any) => {
                    console.log(errors);
                    reject(errors); // Reject the promise on error
                });
        });
    };

    return(
        <div className={styles.container}>
            <div className={styles['add-catalog-container']}>
                <span>Catalog</span>
                <IconPlus className={styles.iconPlus}  onClick={handleOpenModal}/>
            </div>
            <CatalogTree/>
            {showModal && (
                <CatalogModalForm visible={showModal} onClose={handleCloseModal} onOk={handleOk}/>
            )}
        </div>
    )
}

export default MetadataSidebar;
