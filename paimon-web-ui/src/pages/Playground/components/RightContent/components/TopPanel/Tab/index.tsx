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

import {useState} from 'react';
import {TabPane, Tabs} from '@douyinfe/semi-ui';
import {IconCode} from "@douyinfe/semi-icons";
import Editor from "@components/Editor";

const Index = () => {
    const genContent = (value:string) => {
      return <Editor value={value}/>
    }
    const [tabList, setTabList] = useState([
        { tab: 'user', itemKey: '1', content: genContent(""), closable: true },
        { tab: 'student', itemKey: '2', content: genContent("12"), closable: true }
    ]);


    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    const close = (key:string) => {
        const newTabList = tabList.filter((t) => t.itemKey !== key);
        setTabList(newTabList);
    };


    return (
        <Tabs type="card" defaultActiveKey="1" onTabClose={close}>
            {tabList.map((t) => (
                <TabPane closable={t.closable} tab={<span><IconCode style={{color: "rgba(var(--semi-blue-5), 1)"}}/>{t.tab}</span>} itemKey={t.itemKey} key={t.itemKey}>
                    {t.content}
                </TabPane>
            ))}
        </Tabs>
    )
}

export default Index;
