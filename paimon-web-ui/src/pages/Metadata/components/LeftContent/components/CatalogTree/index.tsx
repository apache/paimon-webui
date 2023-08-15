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
import { IconSearch, IconFile } from "@douyinfe/semi-icons";
import Api from "@api/api.ts";
import {useEffect, useState} from "react";
import styles from "./catalog-tree.module.less"

const CatalogTree = () => {

    type TreeDataItem = {
        label: string;
        value: string;
        key: string;
        children?: TreeDataItem[];
    };

    const [treeData, setTreeData] = useState<TreeDataItem[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const result = await Api.getAllCatalogs();
                if (result && result.data) {
                    const transformedData = result.data.map(item => ({
                        label: item.catalogName,
                        value: item.catalogName,
                        key: item.id.toString(),
                    }));
                    setTreeData(transformedData);
                }
            } catch (error) {
                console.error('Failed to get catalogs:', error);
            }
        };

        fetchData();
    }, []);

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
                {isLeaf ? <IconFile style={{marginRight: "8px", color: "var(--semi-color-text-2)"}}/> : expandIcon}
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
