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
package org.apache.paimon.web.server.configrue;

import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.result.enums.Status;
import org.apache.paimon.web.server.data.result.exception.BaseException;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** GlobalExceptionHandler. */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler
    public R<Void> bizException(BaseException e) {
        if (e.getStatus() == null) {
            return R.failed(Status.INTERNAL_SERVER_ERROR_ARGS);
        }
        return R.failed(e.getStatus());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    public R<Void> notLoginException(NotLoginException e) {
        return R.failed(Status.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public R<Void> notPermissionException(NotPermissionException e) {
        return R.failed(Status.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler
    public R<Void> notLoginException(HttpRequestMethodNotSupportedException e) {
        return R.failed(Status.METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler()
    public R<Void> notLoginException(DataAccessException e) {
        log.error("SQLException", e);
        return R.failed(Status.UNKNOWN_ERROR, "sql error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public R<Void> bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Optional<String> msg =
                fieldErrors.stream()
                        .filter(Objects::nonNull)
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .filter(StringUtils::isNotBlank)
                        .findFirst();
        return msg.<R<Void>>map(s -> R.failed(Status.REQUEST_PARAMS_ERROR, s))
                .orElseGet(() -> R.failed(Status.REQUEST_PARAMS_ERROR));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        Optional<String> msg =
                constraintViolations.stream()
                        .filter(Objects::nonNull)
                        .map(ConstraintViolation::getMessage)
                        .findFirst();
        return msg.<R<Void>>map(s -> R.failed(Status.REQUEST_PARAMS_ERROR, s))
                .orElseGet(() -> R.failed(Status.REQUEST_PARAMS_ERROR));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> constraintViolationExceptionHandler(HttpMessageNotReadableException e) {
        return R.failed(Status.REQUEST_PARAMS_ERROR, "Required request body is missing");
    }

    @ExceptionHandler
    public R<Void> unknownException(Exception e) {
        log.error("UnknownException", e);
        return R.failed(Status.UNKNOWN_ERROR, e.getMessage());
    }
}
