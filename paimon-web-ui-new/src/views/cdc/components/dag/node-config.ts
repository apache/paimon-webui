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

export enum NodeType {
  INPUT = 'INPUT', // 数据输入
  OUTPUT = 'OUTPUT', // 数据输出
}

export const getPortsByType = (type: NodeType, nodeId: string) => {
  let ports = []
  switch (type) {
    case NodeType.INPUT:
      ports = [
        {
          id: `${nodeId}-out`,
          group: 'out',
        },
      ]
      break
    case NodeType.OUTPUT:
      ports = [
        {
          id: `${nodeId}-in`,
          group: 'in',
        },
      ]
      break
    default:
      ports = [
        {
          id: `${nodeId}-in`,
          group: 'in',
        },
        {
          id: `${nodeId}-out`,
          group: 'out',
        },
      ]
      break
  }
  return ports
}

export const PORT = {
  groups: {
    in: {
      position: 'left',
      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: 'transparent',
          strokeWidth: 1,
          fill: 'transparent',
        },
      },
    },
    out: {
      position: {
        name: 'right',
        args: {
          dx: 5,
        },
      },
      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: 'transparent',
          strokeWidth: 1,
          fill: 'transparent',
        },
      },
    },
  },
}

export const EDGE = {
  attrs: {
    line: {
      stroke: '#1890ff',
      strokeDasharray: 5,
      targetMarker: 'classic',
      style: {
        animation: 'ant-line 30s infinite linear',
      },
    },
  },
  connector: {
    name: 'smooth'
  }
}
