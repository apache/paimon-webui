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
package org.apache.paimon.web.server.service.impl;

import org.apache.paimon.web.server.data.enums.UserType;
import org.apache.paimon.web.server.data.model.User;
import org.apache.paimon.web.server.service.LdapService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import java.util.List;
import java.util.Optional;

/** ldap service impl. */
@Slf4j
@Service
public class LdapServiceImpl implements LdapService {

    private static final UserAttributesMapperMapper MAPPER = new UserAttributesMapperMapper();

    private static final String FILTER = "cn";

    @Autowired private LdapTemplate ldapTemplate;

    /**
     * get user info by ldap user identification.
     *
     * @param uid login name of ldap user
     * @return {@link Optional} of {@link User} when user not exist then return {@link
     *     Optional#empty()}
     */
    @Override
    public Optional<User> getUser(String uid) {
        LdapQuery query = LdapQueryBuilder.query().where(FILTER).is(uid);
        try {
            List<User> users = ldapTemplate.search(query, MAPPER);
            return CollectionUtils.isEmpty(users)
                    ? Optional.empty()
                    : Optional.ofNullable(users.get(0));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * authenticate by ldap.
     *
     * @param name login name of ldap user
     * @param password login password of ldap user
     * @return {@link Optional} of {@link User} when user authenticate failed then return {@link
     *     Optional#empty()}
     */
    @Override
    public Optional<User> authenticate(String name, String password) {
        EqualsFilter filter = new EqualsFilter(FILTER, name);
        if (ldapTemplate.authenticate(StringUtils.EMPTY, filter.toString(), password)) {
            return this.getUser(name);
        }
        return Optional.empty();
    }

    /** Attributes mapper from LDAP user to Local user. */
    private static class UserAttributesMapperMapper implements AttributesMapper<User> {

        /**
         * Map the LDAP attributes to User object.
         *
         * @param attributes LDAP attributes
         * @return User object
         * @throws NamingException if there is an error during mapping
         */
        @Override
        public User mapFromAttributes(Attributes attributes) throws NamingException {
            Attribute usernameAttr = attributes.get(FILTER);
            Attribute nicknameAttr = attributes.get("sn");
            Attribute email = attributes.get("email");

            if (usernameAttr != null && nicknameAttr != null) {
                User user = new User();
                user.setUsername(usernameAttr.get().toString());
                user.setNickname(nicknameAttr.get().toString());
                user.setEmail(
                        Optional.ofNullable(email)
                                .map(
                                        e -> {
                                            try {
                                                return e.get().toString();
                                            } catch (NamingException ignore) {
                                            }
                                            return StringUtils.EMPTY;
                                        })
                                .orElse(StringUtils.EMPTY));
                user.setUserType(UserType.LDAP.getCode());
                user.setEnabled(true);
                return user;
            }

            return null;
        }
    }
}
