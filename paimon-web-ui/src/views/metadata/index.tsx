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

import { useCatalogStore } from '@/store/catalog'

import styles from './index.module.scss'
import MenuTree from './components/menu-tree'
import Breadcrumb from './components/breadcrumb'
import Tabs from './components/metadata-tabs'

export default defineComponent({
  name: 'MetadataPage',
  setup() {
    const catalogStore = useCatalogStore()
    const catalogStoreRef = storeToRefs(catalogStore)

    const menuTreeWidth = ref('20%')
    const isResizing = ref(false)

    const startMenuTreeResize = (event: MouseEvent) => {
      isResizing.value = true
      event.preventDefault()
    }

    const doMenuTreeResize = (event: MouseEvent) => {
      if (isResizing.value) {
        const parentWidth = document.documentElement.clientWidth
        let newWidth = event.clientX

        let widthInPercent = (newWidth / parentWidth) * 100

        widthInPercent = Math.max(15, Math.min(widthInPercent, 40))

        menuTreeWidth.value = `${widthInPercent}%`
      }
    }

    const stopMenuTreeResize = () => {
      isResizing.value = false
    }

    const contentAreaStyle = computed(() => {
      const menuWidthPercent = parseFloat(menuTreeWidth.value)
      return {
        width: `calc(100% - ${menuWidthPercent}%)`
      }
    })

    onMounted(() => {
      document.addEventListener('mousemove', doMenuTreeResize)
      document.addEventListener('mouseup', stopMenuTreeResize)
    })

    onBeforeUnmount(() => {
      document.removeEventListener('mousemove', doMenuTreeResize)
      document.removeEventListener('mouseup', stopMenuTreeResize)
    })

    return {
      currentTable: catalogStoreRef.currentTable,
      menuTreeWidth,
      startMenuTreeResize,
      contentAreaStyle
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <MenuTree style={{ width: this.menuTreeWidth }} />
        <div class={styles.splitter} onMousedown={this.startMenuTreeResize}></div>
        <div class={styles.content} style={this.contentAreaStyle}>
          {this.currentTable ? (
            <>
              <Breadcrumb />
              <Tabs />
            </>
          ) : (
            <div class={styles.empty}>
              <n-empty description="Select the table. Please"></n-empty>
            </div>
          )}
        </div>
      </div>
    )
  }
})
