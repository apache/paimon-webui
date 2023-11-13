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

import { CaretForwardCircleOutline, CheckmarkCircleOutline, CloseCircleOutline, FileTrayFullOutline, FlashOffOutline } from '@vicons/ionicons5'
import type { Component } from 'vue'

export type JobStatusType = 'RUNNING' | 'FINISH' | 'CANCEL' | 'FAIL'

export type JobCardProps = JobStatusType | 'TOTAL'

export enum JobMapping {
  RUNNING = 'running',
  FINISH = 'finish',
  CANCEL = 'cancel',
  FAIL = 'fail',
  TOTAL = 'total',
}

type StatusPrototype = {
  bgColor: string
  primaryColor: string
  icon: Component
}

export const JobStatusProps: Record<JobCardProps, StatusPrototype> = {
  TOTAL: {
    primaryColor: '#80acf6',
    bgColor: '#D1E2FC',
    icon: <FileTrayFullOutline />,
  },
  RUNNING: {
    primaryColor: '#7ce998',
    bgColor: '#dafbe7',
    icon: <CaretForwardCircleOutline />,
  },
  FINISH: {
    primaryColor: '#f6b658',
    bgColor: '#ffe7bc',
    icon: <CheckmarkCircleOutline />,
  },
  CANCEL: {
    primaryColor: '#a48aff',
    bgColor: '#e9e2ff',
    icon: <CloseCircleOutline />,
  },
  FAIL: {
    primaryColor: '#f9827c',
    bgColor: '#ffdfdb',
    icon: <FlashOffOutline />,
  }
}
