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

import { EditOutlined } from '@ant-design/icons';
import {Button} from "@douyinfe/semi-ui";
import styles from "./circle-edit-btn.module.less";

// @ts-ignore
const CircleEditBtn = ({ onClick }) => {
    return(
        <Button
            className={styles['edit-btn']}
            icon={
                <EditOutlined style={{ color: '#000', fontSize: '16px' }} />}
            onClick={onClick} />
    );
}

export default CircleEditBtn;