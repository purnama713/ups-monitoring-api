package com.hikariman.ups_monitoring_api.controller;

import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.CreateUserRequest;
import com.hikariman.ups_monitoring_api.model.UpdateUserRequest;
import com.hikariman.ups_monitoring_api.model.UserResponse;
import com.hikariman.ups_monitoring_api.model.WebResponse;
import com.hikariman.ups_monitoring_api.repository.DeviceRepository;
import com.hikariman.ups_monitoring_api.repository.UserRepository;
import com.hikariman.ups_monitoring_api.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerSuccess() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("usertest");
        request.setPassword("test123");
        request.setName("hikariman");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });

            assertEquals("OK", response.getData());
        });
    }

    @Test
    void registerFail() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void registerDuplicate() throws Exception {
        User user = new User();
        user.setUsername("usertest");
        user.setPassword(BCrypt.hashpw("test123", BCrypt.gensalt()));
        user.setName("hikariman");
        userRepository.save(user);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("usertest");
        request.setPassword("test123");
        request.setName("hikariman");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getSuccess() throws Exception {
        User user = new User();
        user.setUsername("usertest");
        user.setPassword(BCrypt.hashpw("test123", BCrypt.gensalt()));
        user.setName("hikariman");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000000L);
        userRepository.save(user);


        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertEquals("usertest", response.getData().getUsername());
            assertEquals("hikariman", response.getData().getName());
        });
    }

    @Test
    void getUnauthorized() throws Exception {
        User user = new User();
        user.setUsername("usertest");
        user.setPassword(BCrypt.hashpw("test123", BCrypt.gensalt()));
        user.setName("hikariman");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testsalah")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>() {});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUnauthorizedTokenNotSend() throws Exception {
        User user = new User();
        user.setUsername("usertest");
        user.setPassword(BCrypt.hashpw("test123", BCrypt.gensalt()));
        user.setName("hikariman");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>() {});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test123",  BCrypt.gensalt()));
        user.setName("hikariman");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000L);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("purnama");
        request.setPassword("testpw");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertEquals("test", response.getData().getUsername());
            assertEquals("purnama", response.getData().getName());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertTrue(BCrypt.checkpw("testpw", userDb.getPassword()));
        });
    }

    @Test
    void updateFail() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test123",  BCrypt.gensalt()));
        user.setName("hikariman");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000L);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("");
        request.setPassword("");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>() {});

            assertNotNull(response.getErrors());
        });
    }
}