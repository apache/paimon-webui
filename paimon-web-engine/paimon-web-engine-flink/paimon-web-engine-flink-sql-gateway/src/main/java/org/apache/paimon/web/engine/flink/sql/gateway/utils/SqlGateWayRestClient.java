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

package org.apache.paimon.web.engine.flink.sql.gateway.utils;

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

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/** SQL gateway rest client. */
public class SqlGateWayRestClient extends RestClient {
    private final String sqlGatewayHost;
    private final int sqlGatewayPort;

    public SqlGateWayRestClient(String sqlGatewayHost, int sqlGatewayPort)
            throws ConfigurationException {
        super(new Configuration(), Executors.newDirectExecutorService());
        this.sqlGatewayHost = sqlGatewayHost;
        this.sqlGatewayPort = sqlGatewayPort;
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
                () -> {
                    try {
                        return super.sendRequest(
                                sqlGatewayHost,
                                sqlGatewayPort,
                                messageHeaders,
                                messageParameters,
                                request,
                                fileUploads,
                                apiVersion);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public <
                    M extends MessageHeaders<R, P, U>,
                    U extends MessageParameters,
                    R extends RequestBody,
                    P extends ResponseBody>
            CompletableFuture<P> sendRequest(M messageHeaders, U messageParameters, R request) {
        return throwExceptionHandler(
                () -> {
                    try {
                        return super.sendRequest(
                                sqlGatewayHost,
                                sqlGatewayPort,
                                messageHeaders,
                                messageParameters,
                                request);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
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
                () -> {
                    try {
                        return super.sendRequest(
                                sqlGatewayHost,
                                sqlGatewayPort,
                                messageHeaders,
                                messageParameters,
                                request,
                                fileUploads);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public <
                    M extends MessageHeaders<EmptyRequestBody, P, EmptyMessageParameters>,
                    P extends ResponseBody>
            CompletableFuture<P> sendRequest(M messageHeaders) {
        return throwExceptionHandler(
                () -> {
                    try {
                        return super.sendRequest(sqlGatewayHost, sqlGatewayPort, messageHeaders);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private <T> T throwExceptionHandler(Supplier<T> func0) {
        try {
            return func0.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
