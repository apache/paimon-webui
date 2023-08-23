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

import {Button, Tooltip} from "@douyinfe/semi-ui";
import {IconGithubLogo} from "@douyinfe/semi-icons";
import i18n from 'i18next';
import {useMemo} from "react";
import { useTranslation } from 'react-i18next';

const GithubLogoButton = () => {
    const { t } = useTranslation();
    const handleClick = () => {
        window.open('https://github.com/apache/incubator-paimon-webui', '_blank');
    }

    const tooltipContent = useMemo(() => {
        return t('component.btn-github-icon');
    }, [i18n.language]);

    return (
        <Tooltip content={tooltipContent} position='bottom'>
            <Button
                theme="borderless"
                icon={<IconGithubLogo size="extra-large"/>}
                style={{
                    color: 'var(--semi-color-text-2)',
                    marginRight: '12px',
                }}
                onClick={handleClick}
            />
        </Tooltip>
    )
}

export default GithubLogoButton;