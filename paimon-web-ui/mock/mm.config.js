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

/**
 * @see: https://www.hongqiye.com/doc/mockm/config/option.html
 * @type {import('mockm/@types/config').Config}
 */

const { setTimeout } = require('timers')
const mockData = require(`./modules`)

module.exports = util => {
  return  {
    port: 10088,
    testPort: 10090,
    replayPort: 10091,

    api: {
      async 'use /' (req, res, next) {
        setTimeout(next, 1000)
      },

      // demo
      '/api/1': {msg: `ok`},

      'post /api/3': {msg: `ok`},

      'ws /api/4' (ws) {
        ws.on(`message`, (msg) => ws.send(msg))
      },

      '/status/:code' (req, res) {
        res.json({statusCode: req.params.code})
      },

      // mock
      ...rename(util.libObj.mockjs.mock)
    },
    
    dbCover: true,
    db: util.libObj.mockjs.mock({
      'books': [
        {
          'id|+1': 1,
          user: /\d\d/,
          view: /\d\d\d\d/,
          'type|1': [`js`, `css`, `html`],
          'discount|1': [`0`, `1`],
          author: {
            'name|1': [`Davin`, `labbomb`],
          },
          title: `@ctitle`,
        },
      ],
    }),
  }
}

function rename(mockUtil) {
  const result = {}
  for (const key in mockData) {
    const apiData = mockData[key](mockUtil)

    for (const k in apiData) {
      const urlGap = k.split(' ')

      if (urlGap.length === 2) {
        result[`${urlGap[0]} /mock/api${urlGap[1]}`] = apiData[k]
      } else {
        result[`/mock/api${k}`] = apiData[k]
      }
    }
  }

  return result
}
