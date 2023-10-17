/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.web.server.data.vo;

/** VO of metadata data file. */
public class DataFileVo {

    private final String partition;
    private final long bucket;
    private final String filePath;
    private final String fileFormat;

    public DataFileVo(String partition, long bucket, String filePath, String fileFormat) {
        this.partition = partition;
        this.bucket = bucket;
        this.filePath = filePath;
        this.fileFormat = fileFormat;
    }

    public String getPartition() {
        return partition;
    }

    public long getBucket() {
        return bucket;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** DataFileInfoVo Builder. */
    public static class Builder {
        private String partition;
        private long bucket;
        private String filePath;
        private String fileFormat;

        public Builder setPartition(String partition) {
            this.partition = partition;
            return this;
        }

        public Builder setBucket(long bucket) {
            this.bucket = bucket;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setFileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
            return this;
        }

        public DataFileVo build() {
            return new DataFileVo(partition, bucket, filePath, fileFormat);
        }
    }
}
