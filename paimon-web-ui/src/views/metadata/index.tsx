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

import styles from './index.module.scss'
import MenuTree from './components/menu-tree'
import Breadcrumb from './components/breadcrumb'
import Tabs from './components/metadata-tabs'
import { useCatalogStore } from '@/store/catalog'

export default defineComponent({
  name: 'MetadataPage',
  setup() {
    const catalogStore = useCatalogStore()
    const catalogStoreRef = storeToRefs(catalogStore)

    return {
      currentTable: catalogStoreRef.currentTable,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-split direction="horizontal" max={0.45} min={0.12} resize-trigger-size={0} default-size={0.165}>
          {{
            '1': () => (
              <MenuTree />
            ),
            '2': () => (
              <div class={styles.content}>
                {this.currentTable
                  ? (
                    <>
                      <Breadcrumb />
                      <Tabs />
                    </>
                    )
                  : (
                    <div class={styles.empty}>
                      <n-empty description="Select the table. Please"></n-empty>
                    </div>
                    )}
              </div>
            ),
            'resize-trigger': () => (
              <div class={styles.split} />
            ),
          }}
        </n-split>
      </div>
    )
  },
})
