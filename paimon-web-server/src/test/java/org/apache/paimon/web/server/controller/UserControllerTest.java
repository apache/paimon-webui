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

package org.apache.paimon.web.server.controller;

import org.apache.paimon.web.server.data.enums.UserType;
import org.apache.paimon.web.server.data.model.User;
import org.apache.paimon.web.server.data.result.PageR;
import org.apache.paimon.web.server.data.result.R;
import org.apache.paimon.web.server.data.vo.UserVO;
import org.apache.paimon.web.server.mapper.UserMapper;
import org.apache.paimon.web.server.util.ObjectMapperUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for {@link UserController}. */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest extends ControllerTestBase {

    private static final String userPath = "/api/user";

    private static final int userId = 3;
    private static final String username = "test";

    @Autowired private UserMapper userMapper;

    @Test
    @Order(1)
    public void testAddUser() throws Exception {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setNickname(username);
        user.setPassword("test");
        user.setUserType(UserType.LOCAL);
        user.setEnabled(true);
        user.setIsDelete(false);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(userPath)
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(user))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(2)
    public void testGetUser() throws Exception {
        UserVO user = getUser(userId);
        assertNotNull(user);
        assertEquals(user.getUsername(), username);
        assertNotNull(user.getCreateTime());
        assertNotNull(user.getUpdateTime());
    }

    @Test
    @Order(3)
    public void testUpdateUser() throws Exception {
        String newUserName = username + "-edit";
        User user = new User();
        user.setId(userId);
        user.setUsername(newUserName);
        user.setNickname(newUserName);
        user.setUserType(UserType.LOCAL);
        user.setEnabled(true);
        user.setIsDelete(false);

        mockMvc.perform(
                        MockMvcRequestBuilders.put(userPath)
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(user))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(userPath + "/" + userId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<UserVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<UserVO>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(r.getData().getUsername(), newUserName);
    }

    @Test
    @Order(4)
    public void testAllocateRole() throws Exception {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setNickname(username);
        user.setUserType(UserType.LOCAL);
        user.setEnabled(true);
        user.setIsDelete(false);
        user.setRoleIds(new Integer[] {2});

        mockMvc.perform(
                        MockMvcRequestBuilders.post(userPath + "/allocate")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(user))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(userPath + "/" + userId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<UserVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<UserVO>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertTrue(r.getData().getRoles().size() > 0);
        assertEquals("common", r.getData().getRoles().get(0).getRoleName());
    }

    @Test
    @Order(5)
    public void testListUsers() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(userPath + "/list")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        PageR<UserVO> r =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<PageR<UserVO>>() {});
        assertTrue(
                r.getData() != null
                        && ((r.getTotal() > 0 && r.getData().size() > 0)
                                || (r.getTotal() == 0 && r.getData().size() == 0)));

        UserVO firstUser = r.getData().get(0);
        assertEquals("admin", firstUser.getUsername());
        assertEquals("Admin", firstUser.getNickname());
        assertEquals("admin@paimon.com", firstUser.getEmail());
        assertEquals(UserType.LOCAL, firstUser.getUserType());
        assertNotNull(firstUser.getCreateTime());
        assertNotNull(firstUser.getUpdateTime());
        assertTrue(firstUser.getEnabled());

        UserVO secondUser = r.getData().get(1);
        assertEquals("common", secondUser.getUsername());
        assertEquals("common", secondUser.getNickname());
        assertEquals("common@paimon.com", secondUser.getEmail());
        assertEquals(UserType.LOCAL, secondUser.getUserType());
        assertNotNull(secondUser.getCreateTime());
        assertNotNull(secondUser.getUpdateTime());
        assertTrue(secondUser.getEnabled());
    }

    @Test
    @Order(6)
    public void testChangeUserStatus() throws Exception {
        User user = new User();
        user.setId(2);
        user.setEnabled(false);

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.put(userPath + "/changeStatus")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(user))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());
        UserVO changeUser = getUser(2);
        assertEquals(changeUser.getEnabled(), false);
    }

    @Test
    @Order(7)
    public void testDeleteUser() throws Exception {
        String delResponseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(
                                                userPath + "/" + userId + "," + userId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<?> result = ObjectMapperUtils.fromJSON(delResponseString, R.class);
        assertEquals(200, result.getCode());
    }

    @Test
    @Order(8)
    public void testChangePassword() throws Exception {
        User user = new User();
        user.setId(2);
        user.setPassword("common");
        mockMvc.perform(
                        MockMvcRequestBuilders.post(userPath + "/change/password")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(user))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        User newUser = userMapper.selectById(2);
        assertEquals("9efab2399c7c560b34de477b9aa0a465", newUser.getPassword());
    }

    @Test
    @Order(9)
    public void testValidMobile() {
        String validMobile = "13411112222";
        User user = new User();
        user.setUsername(username);
        user.setMobile(validMobile);
        user.setRoleIds(new Integer[] {1});
        user.setEmail("test@paimon.com");
        Set<ConstraintViolation<User>> violations = getValidator().validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @Order(10)
    public void testInvalidMobile() {
        String inValidMobile = "12311112222";
        User user = new User();
        user.setUsername(username);
        user.setMobile(inValidMobile);
        user.setRoleIds(new Integer[] {1});
        user.setEmail("test@paimon.com");
        Set<ConstraintViolation<User>> violations = getValidator().validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("invalid.phone.format", violation.getMessageTemplate());
    }

    @Test
    @Order(11)
    public void testValidEmail() {
        String validEmail = "test@paimon.com";
        User user = new User();
        user.setUsername(username);
        user.setMobile("13311112222");
        user.setRoleIds(new Integer[] {1});
        user.setEmail(validEmail);
        Set<ConstraintViolation<User>> violations = getValidator().validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @Order(12)
    public void testInvalidEmail() {
        String inValidEmail = "paimon.com";
        User user = new User();
        user.setUsername(username);
        user.setMobile("13311112222");
        user.setRoleIds(new Integer[] {1});
        user.setEmail(inValidEmail);
        Set<ConstraintViolation<User>> violations = getValidator().validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("invalid.email.format", violation.getMessageTemplate());
    }

    private UserVO getUser(Integer userId) throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(userPath + "/" + userId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<UserVO> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<UserVO>>() {});
        assertEquals(200, r.getCode());
        return r.getData();
    }

    private Validator getValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
