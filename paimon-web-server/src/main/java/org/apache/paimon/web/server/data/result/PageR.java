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

package org.apache.paimon.web.server.data.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** Paging Results. */
@Data
@Builder
public class PageR<T> implements Serializable {

    private static final long serialVersionUID = -5143774412936881374L;
    /** total. */
    private final long total;
    /** is success. */
    private final boolean success;
    /** result data. */
    private final List<T> data;

    public PageR(
            @JsonProperty("total") long total,
            @JsonProperty("success") boolean success,
            @JsonProperty("data") List<T> data) {
        this.total = total;
        this.success = success;
        this.data = data;
    }
}
