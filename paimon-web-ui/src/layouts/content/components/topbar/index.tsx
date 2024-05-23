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

import type { MentionOption } from 'naive-ui'
import MenuBar from '../menubar'
import ToolBar from '../toolbar'
import styles from './index.module.scss'
import logoImage from '@/assets/logo.svg'

export default defineComponent({
  props: {
    headerMenuOptions: {
      type: Array<MentionOption>,
      default: [],
    },
  },
  name: 'TopBar',
  setup() {
  },
  render() {
    return (
      <div class={styles.container}>
        <div class={styles['logo-bar']}>
          <n-space align="center" justify="center">
            <div class={styles.logo}>
              <img src={logoImage} alt="logo-image" />
            </div>
            <div>Apache Paimon</div>
          </n-space>
        </div>
        <div class={styles['menu-bar']}>
          <MenuBar menuOptions={this.headerMenuOptions} />
        </div>
        <div class={styles.toolbar}>
          <ToolBar />
        </div>
      </div>
    )
  },
})
