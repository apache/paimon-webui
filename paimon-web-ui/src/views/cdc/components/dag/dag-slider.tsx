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
import { getPortsByType } from './node-config'

export default defineComponent({
  name: 'DagSlider',
  props: {
    graph: {
      type: Object,
      default: () => { },
    },
    dnd: {
      type: Object,
      default: () => { },
    },
  },
  setup(props) {
    const { t } = useLocaleHooks()
    const sliderVariables = reactive({
      sourceList: [
        {
          name: 'MySQL',
          value: 'MYSQL',
          type: 'INPUT',
        },
        {
          name: 'Kafka',
          value: 'KAFKA',
          type: 'INPUT',
        },
        {
          name: 'MongoDB',
          value: 'MONGODB',
          type: 'INPUT',
        },
        {
          name: 'PostgreSQL',
          value: 'POSTGRESQL',
          type: 'INPUT',
        },
      ],
      sinkList: [
        {
          name: 'Paimon',
          value: 'PAIMON',
          type: 'OUTPUT',
        },
      ],
    })

    const handleNodeMove = (e: any, item: any) => {
      const node = props.graph.createNode({
        id: item.name,
        shape: 'custom-node',
        data: item,
        ports: getPortsByType(item.type, item.name),
      })
      props.dnd.start(node, e)
    }

    return {
      t,
      ...toRefs(sliderVariables),
      handleNodeMove,
    }
  },
  render() {
    return (
      <div class={styles['dag-slider']} id="dag-slider">
        <n-card class={styles.card} content-style="overflow:scroll;">
          <n-space vertical>
            <div class={styles.source}>
              <n-space vertical size={15}>
                <div class={styles.title}>Source</div>
                {
                  this.sourceList.map((item) => {
                    return (
                      <n-button class={styles['list-item']} onMousedown={(e: any) => this.handleNodeMove(e, item)}>
                        {item.name}
                      </n-button>
                    )
                  })
                }
              </n-space>
            </div>
            <div class={styles.sink}>
              <n-space vertical size={15}>
                <div class={styles.title}>Sink</div>
                {
                  this.sinkList.map((item) => {
                    return (
                      <n-button class={styles['list-item']} onMousedown={(e: any) => this.handleNodeMove(e, item)}>
                        {item.name}
                      </n-button>
                    )
                  })
                }
              </n-space>
            </div>
          </n-space>
        </n-card>
      </div>
    )
  },
})
