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

package org.apache.paimon.web.api.common;

/** paimon catalog properties. */
public class CatalogProperties {

    public static final String METASTORE = "metastore";

    public static final String WAREHOUSE = "warehouse";

    public static final String URI = "uri";

    public static final String S3_ENDPOINT = "s3.endpoint";

    public static final String S3_SECRET_KEY = "s3.secret-key";

    public static final String S3_ACCESS_KEY = "s3.access-key";

    public static final String OSS_ENDPOINT = "fs.oss.endpoint";

    public static final String OSS_ACCESS_KEY = "fs.oss.accessKeyId";

    public static final String OSS_SECRET_KEY = "fs.oss.accessKeySecret";

    public static final String HIVE_CONF_DIR = "hive-conf-dir";
}
