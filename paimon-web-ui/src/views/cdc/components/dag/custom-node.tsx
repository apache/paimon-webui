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

import { NButton } from 'naive-ui'
import styles from './index.module.scss'

export default defineComponent({
  name: 'CustomNode',
  setup() {
    const getNode = inject('getNode') as any
    const node = getNode()
    const data = node.data

    const onMainMouseEnter = () => {
      const ports = node.getPorts() || []
      ports.forEach((port: { id: any }) => {
        node.setPortProp(port.id, 'attrs/circle', {
          fill: '#fff',
          stroke: '#85A5FF',
        })
      })
    }

    const onMainMouseLeave = () => {
      const ports = node.getPorts() || []
      ports.forEach((port: { id: any }) => {
        node.setPortProp(port.id, 'attrs/circle', {
          fill: 'transparent',
          stroke: 'transparent',
        })
      })
    }

    return {
      data,
      onMainMouseEnter,
      onMainMouseLeave,
    }
  },
  render() {
    return (
      <div
        class={styles['custom-node']}
        onMouseenter={this.onMainMouseEnter}
        onMouseleave={this.onMainMouseLeave}
      >
        <NButton text>{this.data.name}</NButton>
      </div>
    )
  },
})
