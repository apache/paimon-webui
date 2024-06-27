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

import { Stomp } from '@stomp/stompjs'
import { onUnmounted, ref } from 'vue'
import type { Ref } from 'vue'
import type { Client, Frame, IMessage } from '@stomp/stompjs'

interface WebSocketOptions {
  onOpen?: (frame: Frame) => void
  onMessage?: (message: IMessage, topic: string) => void
  onError?: (event: Event) => void
  onClose?: (event: CloseEvent) => void
}

export function useWebSocket(url: string, options?: WebSocketOptions) {
  const client: Ref<Client | null> = ref(null)
  const subscriptions: Ref<Map<string, { unsubscribe: () => void }>> = ref(new Map())

  const connect = (): void => {
    const stompClient = Stomp.client(url)
    stompClient.reconnect_delay = 1000
    stompClient.heartbeatIncoming = 30000
    stompClient.heartbeatOutgoing = 30000

    stompClient.onConnect = (frame: Frame) => {
      options?.onOpen?.(frame)
    }

    stompClient.onStompError = (frame: Frame) => {
      console.error('Broker reported error:', frame.headers.message)
      console.error('Additional details:', frame.body)
    }

    stompClient.activate()
    client.value = stompClient
  }

  const subscribe = (topic: string): void => {
    if (client.value && client.value.connected) {
      const subscription = client.value.subscribe(topic, (message: IMessage) => {
        options?.onMessage?.(message, topic)
      })
      subscriptions.value.set(topic, subscription)
    }
    else {
      console.error('STOMP client is not connected.')
    }
  }

  const unsubscribe = (topic: string): void => {
    const subscription = subscriptions.value.get(topic)
    if (subscription) {
      subscription.unsubscribe()
      subscriptions.value.delete(topic)
    }
  }

  const sendMessage = (destination: string, body: string, headers = {}): void => {
    if (client.value && client.value.connected) {
      client.value.publish({ destination, headers, body })
    }
    else {
      console.error('STOMP client is not connected.')
    }
  }

  const closeConnection = (): void => {
    subscriptions.value.forEach(sub => sub.unsubscribe())
    subscriptions.value.clear()
    client.value?.deactivate()
  }

  onUnmounted(() => {
    closeConnection()
  })

  return { connect, subscribe, unsubscribe, sendMessage, closeConnection }
}
