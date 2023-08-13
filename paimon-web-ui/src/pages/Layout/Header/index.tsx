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

import {Avatar, Button, Layout, Nav} from '@douyinfe/semi-ui';
import { IconMoon, IconGithubLogo, IconSun } from '@douyinfe/semi-icons';
import useThemeSwitcher from '@src/utils/mode'
import paimonLogo from '@src/assets/logo/favicon_blue.svg'
import paimonWhiteLogo from '@src/assets/logo/favicon_white.svg'
import {useNavigate} from "react-router";
import {useMemo, useState} from "react";
import menuList from "@config/menu.tsx";
import ChangeI18nBtn from "@components/ChangeI18nBtn";
import {useTranslation} from "react-i18next";
import {Link} from "react-router-dom";

const { Header } = Layout

const HeaderRoot = ()=> {
    const {dark, switchMode } = useThemeSwitcher();
    const navigate = useNavigate()

    const [openKeys, setOpenKeys] = useState<string[]>([])
    const [selectedKeys, setSelectedKeys] = useState<string[]>([])

    const navList = useMemo(() => {
        return menuList.map((e) => {
            return {
                ...e,
                items: e?.items
                    ? e.items.map((m) => {
                        return {
                            ...m
                        }
                    })
                    : []
            }
        })
    }, [menuList])
    console.log('navlist...', navList)

    const onSelect = (data: any) => {
        setSelectedKeys([...data.selectedKeys])
        navigate(data.selectedItems[0].path as string)
    }

    const onOpenChange = (data: any) => {
        setOpenKeys([...data.openKeys])
    }

    const { t } = useTranslation()

    return(
        <Header style={{ backgroundColor: 'var(--semi-color-bg-1)'}}>
            <div>
                 <Nav
                    header={{
                        logo: <img src={dark ? paimonWhiteLogo : paimonLogo} alt="Apache Paimon"
                                   style={{ width: '96px', height: '36px', fontSize: 36, marginLeft: '-45px', marginRight: '-20px'}}/>,
                        text: 'Apache Paimon'
                    }}
                    mode={"horizontal"}
                    defaultSelectedKeys={['Home']}
                    openKeys={openKeys}
                    selectedKeys={selectedKeys}
                    onSelect={onSelect}
                    onOpenChange={onOpenChange}
                    // items={navList}
                    footer={
                        <div>
                            <Button
                                theme="borderless"
                                icon={!dark ? <IconMoon size={"extra-large"}/> : <IconSun size={"extra-large"}/>}
                                style={{
                                    color: 'var(--semi-color-text-2)',
                                    marginRight: '12px',
                                }}
                                onClick={switchMode}
                            />
                            <Button
                                theme="borderless"
                                icon={<IconGithubLogo size="extra-large"/>}
                                style={{
                                    color: 'var(--semi-color-text-2)',
                                    marginRight: '12px',
                                }}
                            />
                            <ChangeI18nBtn />
                            <Avatar
                                color="orange"
                                size="small"
                                style={{ marginRight: '0px'}}>
                                YJ
                            </Avatar>
                        </div>
                    }
                 >
                     {
                         navList.map( nav => {
                         return (
                             <Link
                                 key={nav.name}
                                 style={{ textDecoration: "none" }}
                                 to={nav.path}
                             >
                                <Nav.Item itemKey={nav.name} text={t('header.' + nav.name)} />
                             </Link>
                         )
                        })
                     }
                 </Nav>
            </div>
        </Header>
    )
}

export default HeaderRoot
