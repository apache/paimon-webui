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

package org.apache.paimon.web.server.validator;

import org.apache.paimon.shade.org.apache.commons.lang3.StringUtils;
import org.apache.paimon.web.server.validator.annotation.PhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * A validator implementation for checking the validity of phone numbers.
 *
 * <p>This class is designed to validate strings against common phone number formats. It is used in
 * conjunction with the {@code @PhoneNumber} annotation to ensure that fields or method parameters
 * annotated with it adhere to acceptable phone number patterns.
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(
            String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(phoneNumber)) {
            return true;
        }
        return phoneNumber.matches(
                "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");
    }
}
