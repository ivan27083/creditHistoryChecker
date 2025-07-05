package com.userService.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link UserController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private int id;

    @BeforeEach
    public void setup() {

    }

    @Test
    @Order(5)
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/{0}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(4)
    public void findAll() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(3)
    public void findById() throws Exception {
        mockMvc.perform(get("/id/{0}", id))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(2)
    public void findByEmail() throws Exception {
        mockMvc.perform(get("/email/{0}", "ivan@gmail.com"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    @Order(1)
    public void save() throws Exception {
        String entity = """
                {
                    "email": "ivan@gmail.com",
                    "password": "123456"
                }""";

        mockMvc.perform(post("/")
                        .content(entity)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
