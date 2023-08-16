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

import { useState } from 'react';
import {TabPane, Tabs} from '@douyinfe/semi-ui';
import { useTranslation } from 'react-i18next';

const TableTab = () => {
    const { t } = useTranslation()

    const [tabList, setTabList] = useState([
        { tab: 'TableInfo', name: 'tableInfo', itemKey: '1', content: "info", closable: true },
        { tab: 'Details', name: 'details', itemKey: '2', content: "info", closable: true },
        { tab: 'Files', name: 'files', itemKey: '3', content: "info", closable: true }
    ]);

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const close = (key) => {
        const newTabList = tabList.filter((t) => t.itemKey !== key);
        setTabList(newTabList);
    };


    return (
        <Tabs type="line" defaultActiveKey="1" onTabClose={close}>
            {tabList.map((tab) => (
                <TabPane closable={tab.closable}
                         tab={<span>{t(`metadata.${tab.name}`)}</span>}
                         itemKey={tab.itemKey}
                         key={tab.itemKey}>
                    {tab.content}
                </TabPane>
            ))}
        </Tabs>
    )
}

export default TableTab;
