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

package org.apache.paimon.web.flink.sql.gateway;

import org.apache.paimon.web.common.func.Supplier;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.rest.FileUpload;
import org.apache.flink.runtime.rest.RestClient;
import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.MessageHeaders;
import org.apache.flink.runtime.rest.messages.MessageParameters;
import org.apache.flink.runtime.rest.messages.RequestBody;
import org.apache.flink.runtime.rest.messages.ResponseBody;
import org.apache.flink.runtime.rest.versioning.RestAPIVersion;
import org.apache.flink.util.ConfigurationException;
import org.apache.flink.util.concurrent.Executors;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class SqlGateWayRestClient extends RestClient {
    private final String targetAddress;
    private final Integer targetPort;

    public SqlGateWayRestClient(String targetAddress, Integer targetPort)
            throws ConfigurationException {
        super(new Configuration(), Executors.newDirectExecutorService());
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
    }

    public <
                    M extends MessageHeaders<R, P, U>,
                    U extends MessageParameters,
                    R extends RequestBody,
                    P extends ResponseBody>
            CompletableFuture<P> sendRequest(
                    M messageHeaders,
                    U messageParameters,
                    R request,
                    Collection<FileUpload> fileUploads,
                    RestAPIVersion<? extends RestAPIVersion<?>> apiVersion) {
        return throwExceptionHandler(
                () ->
                        super.sendRequest(
                                targetAddress,
                                targetPort,
                                messageHeaders,
                                messageParameters,
                                request,
                                fileUploads,
                                apiVersion));
    }

    public <
                    M extends MessageHeaders<R, P, U>,
                    U extends MessageParameters,
                    R extends RequestBody,
                    P extends ResponseBody>
            CompletableFuture<P> sendRequest(M messageHeaders, U messageParameters, R request) {
        return throwExceptionHandler(
                () ->
                        super.sendRequest(
                                targetAddress,
                                targetPort,
                                messageHeaders,
                                messageParameters,
                                request));
    }

    public <
                    M extends MessageHeaders<R, P, U>,
                    U extends MessageParameters,
                    R extends RequestBody,
                    P extends ResponseBody>
            CompletableFuture<P> sendRequest(
                    M messageHeaders,
                    U messageParameters,
                    R request,
                    Collection<FileUpload> fileUploads) {
        return throwExceptionHandler(
                () ->
                        super.sendRequest(
                                targetAddress,
                                targetPort,
                                messageHeaders,
                                messageParameters,
                                request,
                                fileUploads));
    }

    public <
                    M extends MessageHeaders<EmptyRequestBody, P, EmptyMessageParameters>,
                    P extends ResponseBody>
            CompletableFuture<P> sendRequest(M messageHeaders) {
        return throwExceptionHandler(
                () -> super.sendRequest(targetAddress, targetPort, messageHeaders));
    }

    private <T> T throwExceptionHandler(Supplier<T> func0) {
        try {
            return func0.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
