/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.flink.constant;

/** Constants. */
public class Constants {

    public static final String YARN_SITE_CONF = "yarn-site.xml";
    public static final String CORE_SITE_CONF = "core-site.xml";
    public static final String HDFS_SITE_CONF = "hdfs-site.xml";
    public static final String FS_HDFS_HADOOP_CONF = "fs.hdfs.hadoopconf";
    public static final String DEFAULT_MEMORY_SIZE = "1g";
    public static final int MAX_ATTEMPTS = 30;
    public static final int DELAY_MILLIS = 1000;
    public static final String EXECUTION_CHECKPOINTING_ENABLED = "execution.checkpointing.enabled";
    public static final String EXECUTION_CHECKPOINTING_INTERVAL =
            "execution.checkpointing.interval";
}
