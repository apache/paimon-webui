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

import { useCanvasInit } from './use-canvas-init'
import styles from './index.module.scss'
import DagSlider from './dag-slider'
import Drawer from './drawer'
import ContextMenuTool from './context-menu'

export default defineComponent({
  name: 'DagCanvasPage',
  setup(props, { expose }) {
    const { t } = useLocaleHooks()

    const { graph, dnd } = useCanvasInit()

    const nodeVariables = reactive({
      x: 0,
      y: 0,
      row: {} as any,
      cell: {} as any,
      showDrawer: false,
      showContextMenu: false,
    })

    onMounted(() => {
      if (graph.value) {
        graph.value.on('node:dblclick', ({ node }) => {
          nodeVariables.showDrawer = true
          nodeVariables.row = node.data
        })
        graph.value.on('node:contextmenu', ({ e, node }) => {
          nodeVariables.showContextMenu = true
          nodeVariables.row = node.data
          nodeVariables.x = e.clientX - 20
          nodeVariables.y = e.clientY - 178
        })
        graph.value.on('blank:click', () => {
          nodeVariables.showContextMenu = false
        })
      }
    })

    const handleNodeConfirm = (model: any) => {
      if (graph.value) {
        nodeVariables.cell = graph.value.getCellById(nodeVariables.row.name)
        if (nodeVariables.cell) {
          nodeVariables.cell.data = {
            ...nodeVariables.cell.data,
            ...model,
          }
        }
      }
      nodeVariables.showDrawer = false
    }

    const handleDelete = () => {
      nodeVariables.showContextMenu = false
      graph.value?.removeNode(nodeVariables.row.name)
    }

    expose({
      graph,
      dnd,
    })

    return {
      t,
      graph,
      dnd,
      handleNodeConfirm,
      handleDelete,
      ...toRefs(nodeVariables),
    }
  },
  render() {
    return (
      <div class={styles.dag}>
        <div
          class={styles['dag-container']}
          id="dag-container"
        />
        <DagSlider
          graph={this.graph}
          dnd={this.dnd}
        />
        {
          this.showDrawer
          && (
            <Drawer
              showDrawer={this.showDrawer}
              formType={this.row.value || 'MYSQL'}
              onConfirm={this.handleNodeConfirm}
              onCancel={() => this.showDrawer = false}
              row={this.row}
            />
          )
        }
        {
          this.showContextMenu
          && (
            <ContextMenuTool
              onDelete={this.handleDelete}
              x={this.x}
              y={this.y}
            />
          )
        }
      </div>
    )
  },
})
