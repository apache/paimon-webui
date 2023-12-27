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

module.exports = (mockUtil) => ({
  '/catalog/getAllCatalogs': mockUtil({
    code: 200,
    msg: 'Successfully',
    'data|5': [
      {
        'id|+1': 1,
        createTime: '@date("yyyy-MM-dd HH:mm:ss")',
        updateTime: '@date("yyyy-MM-dd HH:mm:ss")',
        catalogType: 'hlv',
        catalogName: `@title`,
        warehouse: undefined,
        hiveUri: undefined,
        hiveConfDir: undefined
      }
    ]
  }),
  'get /database/list': mockUtil({
    code: 200,
    msg: 'Successfully',
    'data|5': [
      {
        "name": "@name",
        "catalogId": '1-52',
        "catalogName": "@title",
        "description": ""
      },
    ]
  }),
  'post /table/list': mockUtil({
    code: 200,
    msg: 'Successfully',
    'data|5': [
      {
        "catalogId": '1-52',
        "catalogName": "@name",
        "databaseName": "@name",
        "name": "@name"
      },
    ]
  }),
  'post /metadata/query/schema': mockUtil({
    code: 200,
    msg: 'Successfully',
    'data|2': [
      {
        "schemaId|+1": 0,
        "fields|10": [
          {
            "id|+1": 0,
            "name": "@id",
            "type": "@word",
            "comment": null
          },
        ],
        "partitionKeys": "[]",
        "primaryKeys": "[\"id\"]",
        "comment": "",
        "option|3": [
          {
            "key": "@name",
            "value": "@float"
          }
        ],
        "updateTime": "@datetime"
      }
    ]
  }),
  'post /metadata/query/snapshot': mockUtil({
    code: 200,
    msg: 'Successfully',
    'data|2': [
      {
        "snapshotId": "@float",
        "schemaId": "@float",
        "commitUser": "@tld",
        "commitIdentifier": "@tld",
        "commitKind": "APPEND",
        "commitTime": "@datetime",
        "baseManifestList": "@title",
        "deltaManifestList": "@title",
        "changelogManifestList": "@title",
        "totalRecordCount": "@float",
        "deltaRecordCount": "@float",
        "changelogRecordCount": "@float",
        "addedFileCount": "@float",
        "deletedFileCount": "@float",
        "watermark": "@float",
      },
    ]
  }),
  'post /metadata/query/manifest': mockUtil({
    code: 200,
    msg: 'Successfully',
    'data|2': [
      {
        "fileName": "@title",
        "fileSize": "@float",
        "numAddedFiles": "@float",
        "numDeletedFiles": "@float",
        "schemaId": "@float"
      }
    ]
  }),
  'post /metadata/query/dataFile': mockUtil({
    code: 200,
    msg: 'Successfully',
    'data|2': [
      {
        "partition": "[]",
        "bucket": "@float",
        "filePath": "@title",
        "fileFormat": "orc",
        "schemaId": "0",
        "level": "@float",
        "recordCount": "4",
        "fileSizeInBytes": "3999",
        "minKey": "[152]",
        "maxKey": "[160]",
        "nullValueCounts": "{Path=0, Status=0, assetPath=0, cachePath=0, canCreateUser=0, canCreateUserStime=0, dbhost=0, dbname=0, dbport=0, dbpw=0, dbuser=0, groupid=0, host=0, id=0, ifshow=0, ipv6=0, is_review=0, lid=0, mergeServerTime=0, mtime=0, name=0, openDateTime=0, port=0, ready_open_weight=0, recommend_weight=0, server_type=0, sid=0, testflag=0, uncachePath=0, url=0, version=0}",
        "minValueStats": "{Path=http://static.zuiyouxi.com/game/20120730/, Status=false, assetPath=http://static.zuiyouxi.com/game/20120730/assets/, cachePath=, canCreateUser=true, canCreateUserStime=0, dbhost=192.168.1.1, dbname=pirate5900003, dbport=3306, dbpw=admin, dbuser=rd, groupid=5900002, host=10.0.18.34, id=152, ifshow=false, ipv6=, is_review=0, lid=11, mergeServerTime=0, mtime=1644390163, name=S1-国内测试服, openDateTime=1636077600, port=12002, ready_open_weight=0, recommend_weight=0, server_type=false, sid=1, testflag=0, uncachePath=, url=http://static.zuiyouxi.com/game/index.html, version=}",
        "maxValueStats": "{Path=http://static.zuiyouxi.com/game/20120730/, Status=true, assetPath=http://static.zuiyouxi.com/game/20120730/assets/, cachePath=, canCreateUser=true, canCreateUserStime=0, dbhost=192.168.1.1, dbname=pirate5920001, dbport=3306, dbpw=admin, dbuser=rd, groupid=5920001, host=192.168.2.162, id=160, ifshow=true, ipv6=, is_review=0, lid=13, mergeServerTime=0, mtime=1659498303, name=兼容性测试服, openDateTime=1659492000, port=20009, ready_open_weight=0, recommend_weight=0, server_type=false, sid=11, testflag=0, uncachePath=, url=http://static.zuiyouxi.com/game/index.html, version=}",
        "minSequenceNumber": "0",
        "maxSequenceNumber": "3",
        "creationTime": "@now"
      }
    ]
  })
})
