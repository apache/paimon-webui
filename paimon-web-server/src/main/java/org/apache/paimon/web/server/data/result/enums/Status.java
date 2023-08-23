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

package org.apache.paimon.web.server.data.result.enums;

/**
 * Status enum.
 *
 * <p><b>NOTE:</b> This enumeration is used to define status codes and internationalization messages
 * for response data. <br>
 * This is mainly responsible for the internationalization information returned by the interface
 */
public enum Status {
    /** response data msg. */
    SUCCESS(200, "successfully"),
    FAILED(400, "failed"),
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),
    METHOD_NOT_ALLOWED(405, "method.not.allowed"),

    // TODO
    UNKNOWN_ERROR(500, "unknown.error"),
    INTERNAL_SERVER_ERROR_ARGS(501, "internal.server.error"),

    REQUEST_PARAMS_NOT_VALID_ERROR(4001, "invalid.request.parameter"),
    REQUEST_PARAMS_ERROR(4002, "request.parameter.error"),

    USER_NOT_EXIST(10001, "user.not.exist"),
    USER_PASSWORD_ERROR(10002, "user.password.error"),
    USER_DISABLED_ERROR(10003, "user.is.disabled"),
    USER_NOT_BING_TENANT(10004, "user.not.bing.tenant"),
    /** ------------role-----------------. */
    ROLE_IN_USED(10101, "role.in.used"),
    ROLE_NAME_IS_EXIST(10102, "role.name.exist"),
    ROLE_KEY_IS_EXIST(10103, "role.key.exist"),
    /** ------------menu-----------------. */
    MENU_IN_USED(10201, "menu.in.used"),
    MENU_NAME_IS_EXIST(10202, "menu.name.exist"),
    MENU_PATH_INVALID(10203, "menu.path.invalid"),

    /** ------------catalog-----------------. */
    CATALOG_NAME_IS_EXIST(10301, "catalog.name.exist"),
    CATALOG_CREATE_ERROR(10302, "catalog.create.error"),

    /** ------------database-----------------. */
    DATABASE_NAME_IS_EXIST(10401, "database.name.exist"),
    DATABASE_CREATE_ERROR(10402, "database.create.error"),

    /** ------------table-----------------. */
    TABLE_NAME_IS_EXIST(10501, "table.name.exist"),
    TABLE_CREATE_ERROR(10502, "table.create.error"),
    ;

    private final int code;
    private final String msg;

    Status(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
