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

import {Input, Tree} from '@douyinfe/semi-ui';
import { IconSearch } from "@douyinfe/semi-icons";
import styles from "./catalog-tree.module.less"

const CatalogTree = () => {
    const treeData = [
        {
            label: 'paimon_catalog_01',
            value: 'catalog01',
            key: '0',
            children: [
                {
                    label: 'paimon_db_01',
                    value: 'db01',
                    key: '0-0',
                    children: [
                        {
                            label: 'paimon_table_01',
                            value: 'paimon_table_01',
                            key: '0-0-0',
                        },
                        {
                            label: 'paimon_table_02',
                            value: 'paimon_table_02',
                            key: '0-1-0',
                        },
                        {
                            label: 'paimon_table_03',
                            value: 'paimon_table_03',
                            key: '0-2-0',
                        },
                        {
                            label: 'paimon_table_04',
                            value: 'paimon_table_04',
                            key: '0-3-0',
                        },
                        {
                            label: 'paimon_table_05',
                            value: 'paimon_table_05',
                            key: '0-4-0',
                        },
                    ],
                },
                {
                    label: 'paimon_db_02',
                    value: 'paimon_db_02',
                    key: '0-1',
                    children: [
                        {
                            label: 'Osaka',
                            value: 'Osaka',
                            key: '0-1-0'
                        }
                    ]
                },
                {
                    label: 'paimon_db_03',
                    value: 'paimon_db_03',
                    key: '0-2',
                    children: [
                        {
                            label: 'Beijing',
                            value: 'Beijing',
                            key: '0-2-0',
                        },
                    ],
                },
                {
                    label: 'paimon_db_04',
                    value: 'paimon_db_04',
                    key: '0-3',
                    children: [
                        {
                            label: 'Beijing',
                            value: 'Beijing',
                            key: '0-3-0',
                        },
                    ],
                },
                {
                    label: 'paimon_db_05',
                    value: 'paimon_db_05',
                    key: '0-4',
                    children: [
                        {
                            label: 'Beijing',
                            value: 'Beijing',
                            key: '0-4-0',
                        },
                    ],
                },
            ],
        },
        {
            label: 'paimon_catalog_02',
            value: 'paimon_catalog_02',
            key: '1',
            children: [
                {
                    label: 'United States',
                    value: 'United States',
                    key: '1-0'
                },
                {
                    label: 'Canada',
                    value: 'Canada',
                    key: '1-1'
                }
            ]
        }
    ];

    const renderLabel = (x: any) => {
        const className = x.className;
        const onExpand = x.onExpand;
        const onClick = x.onClick;
        const data = x.data;
        const expandIcon = x.expandIcon;
        const { label } = data;
        const isLeaf = !(data.children && data.children.length);
        return (
            <li
                className={className}
                role="treeitem"
                onClick={isLeaf ? onClick : onExpand}
            >
                {isLeaf ? null : expandIcon}
                <span>{label}</span>
            </li>
        );
    };


    return(
        <Tree
            filterTreeNode
            treeData={treeData}
            searchPlaceholder={"Filter"}
            searchRender={({ prefix, ...restProps }) => (
                <Input suffix={<IconSearch className={styles['catalog-tree-input-icon']}/>} {...restProps} className={styles['catalog-tree-input']}></Input>
            )}
            renderFullLabel={renderLabel}
        />
    )
}

export default CatalogTree;
