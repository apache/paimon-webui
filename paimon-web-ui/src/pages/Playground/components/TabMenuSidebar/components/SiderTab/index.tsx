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

import {TabPane, Tabs} from '@douyinfe/semi-ui';
import {IconCode, IconListView, IconHistory} from '@douyinfe/semi-icons';
import CatalogDropdown from "@components/Dropdown/CatalogDropdown.tsx";
import TreeNode from '@pages/Playground/components/TabMenuSidebar/components/TreeComponent';
import {useCallback, useEffect, useState} from "react";
import styles from "./siderbar.module.less";


const TMP_CATALOG_LIST = [
    {
        id: 1,
        name: 'catalog1',
        description: 'catalog1',
        createTime: '2021-08-01',
        updateTime: '2021-08-01',
    },
    {
        id: 2,
        name: 'catalog2',
        description: 'catalog2',
        createTime: '2021-08-01',
        updateTime: '2021-08-01',
    },
]

export type CataLog = {
    id: number;
    name: string;
    description: string;
    createTime: string;
    updateTime: string;
}

const SiderTab = () => {

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const [catalogList, setCatalogList] = useState<CataLog[]>(TMP_CATALOG_LIST || []);
    const [avtiveCatalogId, setActiveCatalogId] = useState<number>(0);
    const [reLoadCatalog, setReLoadCatalog] = useState<boolean>(false);
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const [treeLoading, setTreeLoading] = useState<boolean>(false);


    /**
     * 获取 catalog 列表
     * @type {() => Promise<void>}
     */
    const queryCatalog = useCallback(async () => {
        // todo: 获取后端 catalog 列表 ,
        console.log('CatalogDropdorn');
    }, [])


    /**
     * 获取 catalog 下的表
     * @type {() => Promise<void>}
     */
    const queryTableByCataLog = useCallback(async () => {
        // todo: 根据选择的 catalog id 获取后端 catalog 下的表
        console.log('queryDetailCataLog');
    }, [avtiveCatalogId]) // 当 avtiveCatalog 变化时，重新获取 catalog 下的表


    useEffect(() => {
        queryCatalog();
    }, []); // 当 reLoadCatalog 变化时，重新获取 catalog 列表

    /**
     * Catalog下拉菜单选中的事件
     * @param {number} catalogId
     */
    const handleCatalogChange = async (catalogId: number) => {
        // 设置当前选中的 catalog id
        setActiveCatalogId(catalogId);
        setTreeLoading(true);
        // 重新获取 catalog 下的表
        await queryTableByCataLog();
        setTreeLoading(false);
    }

    /**
     * 刷新 Catalog 列表
     */
    const handleReloadCatalog = () => {
        console.log('handleReloadCatalog')
        setReLoadCatalog(true);
        queryCatalog();
        setTimeout(() => {
            setReLoadCatalog(false);
        }, 3000)
    }

    return (
        <Tabs tabPosition={"left"} type={"button"} tabPaneMotion={false} className={styles.container}>
            <TabPane
                className={styles['tab-panel-container']}
                tab={
                    <span>
                        <IconListView size={"extra-large"} title={"Catalog"}/>
                    </span>
                } itemKey={'1'}>
                <CatalogDropdown
                    reloadCatalogListCallBack={() => handleReloadCatalog()}
                    reLoadCatalog={reLoadCatalog}
                    catalogChange={handleCatalogChange}
                    catalogList={catalogList}
                />
                <TreeNode/>
            </TabPane>
            <TabPane
                tab={
                    <span>
                        <IconCode size={"extra-large"} title={"Saved Queries"}/>
                    </span>
                } itemKey={'2'}>
                Saved Queries
            </TabPane>
            <TabPane
                tab={
                    <span>
                        <IconHistory size={"extra-large"} title={"History"}/>
                    </span>
                } itemKey={'3'}>
                    History
            </TabPane>
        </Tabs>
    )
}

export default SiderTab;
