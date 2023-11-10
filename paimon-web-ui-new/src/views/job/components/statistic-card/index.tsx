/* Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
'License'); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License. */

import { CaretForwardCircleOutline, CheckmarkCircleOutline, CloseCircleOutline, FileTrayFullOutline, FlashOffOutline } from '@vicons/ionicons5'

import { type JobCardProps, JobMapping } from '../../constant'

import styles from './index.module.scss'


const IconProps = {
  TOTAL: {
    color: '#D1E2FC',
    iconColor: '#80acf6',
    iconSize: 30,
    icon: <FileTrayFullOutline />,
  },
  RUNNING: {
    color: '#dafbe7',
    iconColor: '#7ce998',
    iconSize: 40,
    icon: <CheckmarkCircleOutline />,
  },
  FINISH: {
    color: '#ffe7bc',
    iconColor: '#f6b658',
    iconSize: 40,
    icon: <CaretForwardCircleOutline />,
  },
  CANCEL: {
    color: '#e9e2ff',
    iconColor: '#a48aff',
    iconSize: 40,
    icon: <CloseCircleOutline />,
  },
  FAIL: {
    color: '#ffdfdb',
    iconColor: '#f9827c',
    iconSize: 30,
    icon: <FlashOffOutline />,
  }
}

export default defineComponent({
  name: 'JobStatisticCard',
  props: {
    type: {
      type: String as PropType<JobCardProps>,
      required: true,
    },
    value: {
      type: Number,
      required: true,
    },
  },
  setup(props) {
    const { t } = useLocaleHooks()

    return {
      t,
      props,
      IconProps
    }
  },
  render() {
    return (
      <n-card>
        <n-space align='center' justify='start' size={30}>
          <div>
            <n-icon-wrapper color={this.IconProps[this.props.type].color} icon-color={this.IconProps[this.props.type].iconColor} size='54' border-radius={27}>
              <n-icon size={this.IconProps[this.props.type].iconSize}>
                {this.IconProps[this.props.type].icon}
              </n-icon>
            </n-icon-wrapper>
          </div>
          <div>
            <div>{this.t(`job.${JobMapping[this.props.type]}`)}</div>
            <div class={styles.value}>{this.props.value}</div>
          </div>
        </n-space>
      </n-card>
    )
  }
})
