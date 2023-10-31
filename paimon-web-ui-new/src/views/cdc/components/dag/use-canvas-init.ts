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

import { Graph } from '@antv/x6'
import { Dnd } from '@antv/x6-plugin-dnd'
import { register } from '@antv/x6-vue-shape'
import CustomNode from './custom-node'
import { EDGE, PORT } from './node-config'

register({
  shape: 'custom-node',
  width: 150,
  height: 40,
  component: CustomNode,
  ports: {
    ...PORT
  }
})

export function useCanvasInit() {
  const graph = ref<Graph>()
  const dnd = ref<Dnd>()

  const graphInit = () => {
    return new Graph({
      container: document.getElementById('dag-container') || undefined,
      // background: {
      //   color: '#F2F7FA',
      // },
      autoResize: true,
      panning: true,
      mousewheel: true,
      grid: {
        visible: true,
        type: 'dot',
        size: 20,
        args: {
          color: '#a0a0a0',
          thickness: 1,
        },
      },
      connecting: {
        // Whether multiple edges can be created between the same start node and end
        allowMulti: false,
        // Whether a point is allowed to connect to a blank position on the canvas
        allowBlank: false,
        // The start node and the end node are the same node
        allowLoop: false,
        // Whether an edge is allowed to link to another edge
        allowEdge: false,
        // Whether edges are allowed to link to nodes
        allowNode: true,
        // Whether to allow edge links to ports
        allowPort: true,
        // Whether all available ports or nodes are highlighted when you drag the edge
        highlight: true,
        createEdge() {
          return graph.value?.createEdge({
            shape: 'dag-edge'
          })
        },
        anchor: {
          name: 'left',
        },
      }
    })
  }

  const dndInit = () => {
    const actualGraph = graph.value // get the actual Graph object from the Ref
    if (!actualGraph) {
      throw new Error('Graph object is undefined')
    }
    return new Dnd({
      target: actualGraph,
      dndContainer: document.getElementById('dag-slider') || undefined,
      getDragNode: (node) => node.clone({ keepId: true }),
      getDropNode: (node) => node.clone({ keepId: true }),
    })
  }

  const registerCustomCells = () => {
    Graph.unregisterEdge('dag-edge')
    Graph.registerEdge('dag-edge', { ...EDGE })
  }

  onMounted(() => {
    const actualGraph = graphInit() // get the actual Graph object from the function
    graph.value = actualGraph // set the Ref to the actual Graph object
    const actualDnd = dndInit() // get the actual Dnd object from the function
    dnd.value = actualDnd // set the Ref to the actual Dnd object
    registerCustomCells()
  })

  return {
    graph,
    dnd
  }
}
