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

import type { TreeOption } from 'naive-ui'
import styles from './index.module.scss'

export default defineComponent({
  name: 'ContextMenu',
  props: {
    x: {
      type: Number as PropType<number>,
      default: 0,
    },
    y: {
      type: Number as PropType<number>,
      default: 0,
    },
    visible: {
      type: Boolean as PropType<boolean>,
      default: false,
    },
    type: {
      type: Array as PropType<string[]>,
      default: () => [],
    },
  },
  emits: ['update:visible', 'select'],
  setup(props, { emit }) {
    const { t } = useLocaleHooks()

    const options = computed(() => ([
      {
        label: t('playground.new_folder'),
        key: 'new_folder',
        show: props.type.includes('folder'),
      },
      {
        label: t('playground.new_file'),
        key: 'new_file',
        show: props.type.includes('file'),
      },
      {
        label: t('playground.delete'),
        key: 'delete',
        show: props.type.includes('delete'),
      },
      {
        label: t('playground.rename'),
        key: 'rename',
        show: props.type.includes('rename'),
      },
      {
        label: t('playground.close_left'),
        key: 'close_left',
        show: props.type.includes('close_left'),
      },
      {
        label: t('playground.close_right'),
        key: 'close_right',
        show: props.type.includes('close_right'),
      },
      {
        label: t('playground.close_others'),
        key: 'close_others',
        show: props.type.includes('close_others'),
      },
      {
        label: t('playground.close_all'),
        key: 'close_all',
        show: props.type.includes('close_all'),
      },
    ]))

    const handleSelect = (keys: Array<string | number>, option: Array<TreeOption | null>) => {
      emit('select', keys, option)
    }

    const handleClickOutside = () => {
      emit('update:visible', false)
    }

    return {
      options,
      handleSelect,
      handleClickOutside,
    }
  },
  render() {
    return (
      <div class={styles['context-menu']}>
        <n-dropdown
          trigger="manual"
          placement="bottom-start"
          options={this.options}
          show={this.visible}
          x={this.x}
          y={this.y}
          on-select={this.handleSelect}
          on-clickoutside={this.handleClickOutside}
        />
      </div>
    )
  },
})
