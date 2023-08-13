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

import {Select} from '@douyinfe/semi-ui';
import React from "react";
import {CataLog} from "@pages/Playground/components/TabMenuSidebar/components/SiderTab";
import {IconRefresh} from "@douyinfe/semi-icons";
import { useTranslation } from 'react-i18next';

/**
 * 下拉菜单组件props
 */
type CatalogDropdownProps = {
    catalogList: CataLog[];
    catalogChange: (catalogId: number) => void;
    reLoadCatalog: boolean;
    reloadCatalogListCallBack: () => void;
}

const CatalogDropdown: React.FC<CatalogDropdownProps> = (props) => {

    const { t } = useTranslation()

    const {catalogList, catalogChange, reLoadCatalog,reloadCatalogListCallBack} = props;


    /**
     * 触发下拉菜单选中的事件 传递给父组件
     * @param
     */
    const handleCatalogChange = (value: any) => {
        catalogChange(value);
    }

    /**
     * 渲染下拉菜单
     * @returns {any}
     */
    const renderCatalogList = () => {
        return catalogList.map((catalog: CataLog) => {
            return {
                label: catalog.name,
                value: catalog.id,
                key: catalog.id,
            }
        })
    }


    return<>
        <Select
            suffix={<IconRefresh onClick={reloadCatalogListCallBack}  title={"Refresh"} spin={reLoadCatalog}/>}
            loading={reLoadCatalog} onChange={handleCatalogChange}
            placeholder={t('playground.selectCatalog')}
            optionList={renderCatalogList()}
            showClear
            style={{display: "flex", flexGrow: 1, backgroundColor: "var(--semi-color-bg-0)"}}
        />
    </>
}

export default CatalogDropdown;
