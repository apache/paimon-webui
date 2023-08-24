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

import TableTab from "@pages/Metadata/components/RightContent/components/MainContent/Table";
import { Breadcrumb } from '@douyinfe/semi-ui';
import {useTableStore} from "@src/store/tableStore.ts";
import styles from "./right-content.module.less"
import emptyImg from "@assets/img/empty.png";

const MetadataRightContent = () => {
    const tableNodeClicked = useTableStore((state) => state.tableNodeClicked);

    if (!tableNodeClicked) {
        return (
            <div className={styles['empty-container']}>
                <div className={styles['el-empty__image']}>
                    <img src={emptyImg}/>
                </div>
                <div className={styles['el-empty__description']}>
                    <p>请在左侧选择 Table</p>
                </div>
            </div>
        )
    }

    return(
        <div className={styles.container}>
            <Breadcrumb separator={'>'} compact={false}>
                <Breadcrumb.Item>{tableNodeClicked.split("#")[0]}</Breadcrumb.Item>
                <Breadcrumb.Item>{tableNodeClicked.split("#")[1]}</Breadcrumb.Item>
                <Breadcrumb.Item>{tableNodeClicked.split("#")[2]}</Breadcrumb.Item>
            </Breadcrumb>
            <TableTab/>
        </div>
    )
}

export default MetadataRightContent;
