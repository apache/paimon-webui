/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.apache.paimon.web.api.action.context.factory;

import com.google.auto.service.AutoService;
import org.apache.paimon.web.api.action.context.ActionContext;
import org.apache.paimon.web.api.action.context.ActionContextUtil;
import org.apache.paimon.web.api.action.context.MysqlSyncTableActionContext;
import org.apache.paimon.web.api.action.context.options.FlinkCdcOptions;
import org.apache.paimon.web.api.enums.FlinkCdcType;
import org.apache.paimon.web.api.enums.FlinkJobType;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@AutoService(FlinkCdcActionContextFactory.class)
public class MysqlSyncTableActionContextFactory implements FlinkCdcActionContextFactory {


    @Override
    public String sourceType() {
        return "Mysql";
    }

    @Override
    public String targetType() {
        return "Paimon";
    }

    @Override

    public FlinkCdcType cdcType() {
        return FlinkCdcType.SINGLE_TABLE_SYNC;
    }

    @Override
    public ActionContext getActionContext(Properties flinkProperties, Properties sourceProperties, Properties targetProperties) {
        return MysqlSyncTableActionContext.builder()
                .sessionUrl(String.valueOf(flinkProperties.getOrDefault(FlinkCdcOptions.SESSION_URL,"http://127.0.0.1:8081")))
                .flinkJobType(FlinkJobType.SESSION)
                .warehouse(targetProperties.getProperty(FlinkCdcOptions.WAREHOUSE))
                .database(targetProperties.getProperty(FlinkCdcOptions.DATABASE))
                .table(targetProperties.getProperty(FlinkCdcOptions.TABLE))
                .primaryKeys(targetProperties.getProperty(FlinkCdcOptions.PRIMARY_KEYS))
                .actionPath(ActionContextUtil.getActionJarPath())
                .catalogConfList(ActionContextUtil.getConfListFromString(sourceProperties.getProperty("paimonCatalogConf"),";"))
                .mysqlConfList(ActionContextUtil.getConfListFromString(targetProperties.getProperty(FlinkCdcOptions.MYSQL_CONF),";"))
                .build();
    }


}
