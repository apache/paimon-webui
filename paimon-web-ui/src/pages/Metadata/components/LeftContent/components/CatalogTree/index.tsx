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
import { IconSearch, IconFile, IconPlus } from "@douyinfe/semi-icons";
import {Tooltip} from "@douyinfe/semi-ui";
import {useEffect, useState} from "react";
import styles from "./catalog-tree.module.less"
import { useTranslation } from 'react-i18next';
import { useCatalogStore } from "@src/store/catalogStore.ts";

const CatalogTree = () => {
    const { t } = useTranslation()
    const [hoveredNode, setHoveredNode] = useState(null);
    const [hoveredIcon, setHoveredIcon] = useState(false); // 新增一个状态来追踪图标的悬停状态

    const handleMouseEnter = (key: any) => {
        setHoveredNode(key);
    };

    const handleMouseLeave = () => {
        setHoveredNode(null);
    };


    type TreeDataItem = {
        label: string;
        value: string;
        key: string;
        children?: TreeDataItem[];
    };

    const [treeData, setTreeData] = useState<TreeDataItem[]>([]);
    const fetchCatalogData = useCatalogStore(state => state.fetchCatalogData);
    const catalogItemList = useCatalogStore(state => state.catalogItemList);

    useEffect(() => {
        // Fetch the catalog data when the component mounts
        fetchCatalogData();
    }, [fetchCatalogData]);

    useEffect(() => {
        // Update treeData when catalogItemList changes
        const transformedData = catalogItemList.map(item => ({
            label: item.catalogName,
            value: item.catalogName,
            key: item.id.toString(),
        }));
        setTreeData(transformedData);
    }, [catalogItemList]);

    const renderLabel = (x: any) => {
        const className = x.className;
        const onExpand = x.onExpand;
        const onClick = x.onClick;
        const data = x.data;
        const expandIcon = x.expandIcon;
        const { label, key} = data;
        const isLeaf = !(data.children && data.children.length);

        return (
            <li
                className={className}
                role="treeitem"
                onClick={isLeaf ? onClick : onExpand}
                onMouseEnter={() => handleMouseEnter(key)}
                onMouseLeave={handleMouseLeave}
                tabIndex={0}
                style={{
                    position: 'relative',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    width: '100%'
                }}
            >
                {isLeaf ? <IconFile style={{marginRight: "8px", color: "var(--semi-color-text-2)"}}/> : expandIcon}
                <div style={{ flex: 1 }}>{label}</div>
                { key === hoveredNode && (
                    <Tooltip content={t('metadata.add-database')}>
                        <IconPlus  style={{
                            position: 'absolute',
                            right: '10px',
                            borderRadius: '5px',
                            padding: '2px',
                            transition: 'border 0.3s, color 0.3s', // Add color transition effects
                            border: hoveredIcon ? '1px solid #d3d3d3' : 'none', // Set border color to light gray
                            color: hoveredIcon ? '#d3d3d3' : '#000', // Set icon color to light gray or black
                            fontSize: hoveredIcon ? '16px' : '12px',
                        }}
                                   onMouseEnter={() => setHoveredIcon(true)} // Update state on mouseover
                                   onMouseLeave={() => setHoveredIcon(false)} // Update state when mouse leaves
                        />
                    </Tooltip>
                )}
            </li>
        );
    };

    return(
        <Tree
            filterTreeNode
            treeData={treeData}
            searchPlaceholder={t('common.filter')}
            searchRender={({ prefix, ...restProps }) => (
                <Input suffix={<IconSearch className={styles['catalog-tree-input-icon']}/>} {...restProps} className={styles['catalog-tree-input']}></Input>
            )}
            renderFullLabel={renderLabel}
        />
    )
}

export default CatalogTree;
