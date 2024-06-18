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
import { useJobStore } from '@/store/job'

export default defineComponent({
  name: 'LogConsole',
  props: {
    maxHeight: {
      type: Number as PropType<number>,
      default: 150,
    },
  },
  setup(props) {
    const jobStore = useJobStore()
    const { mittBus } = getCurrentInstance()!.appContext.config.globalProperties
    const logContent = computed(() => jobStore.getJobLog)
    const maxLogHeight = ref(0)
    const logRef = ref<any>(null)
    const containerRef = ref<HTMLElement | null>(null)

    function updateLogHeight() {
      const viewportHeight = window.innerHeight
      const calculatedHeight = (viewportHeight - 181) * 0.4 - 54
      maxLogHeight.value = Math.max(calculatedHeight, props.maxHeight + 52)
    }

    mittBus.on('resizeLog', () => {
      updateLogHeight()
    })

    watchEffect(() => {
      if (logContent.value) {
        nextTick(() => {
          logRef.value?.scrollTo({ position: 'bottom', silent: true })
        })
      }
    })

    onMounted(() => {
      nextTick(() => {
        updateLogHeight()
      })
    })

    onUnmounted(() => {
      mittBus.off('resizeLog')
    })

    return {
      logContent,
      maxLogHeight,
      logRef,
      containerRef,
    }
  },
  render() {
    return (
      <div class={styles.container}>
        <n-log
          ref="logRef"
          class={styles.log}
          style={{ maxHeight: `${this.maxLogHeight}px` }}
          line-height={1.8}
          log={this.logContent}
          language="log"
          rows={40}
        />
      </div>
    )
  },
})
