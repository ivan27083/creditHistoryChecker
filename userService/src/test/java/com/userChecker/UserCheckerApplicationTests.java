package com.userChecker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserCheckerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
	void contextLoads() {
	}

    @Test
    public void getAll() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getOne() throws Exception {
        mockMvc.perform(get("/users/{0}", "0"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getMany() throws Exception {
        mockMvc.perform(get("/users/by-ids")
                        .param("ids", ""))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void create() throws Exception {
        String dto = """
                {
                    "id": 0,
                    "username": "",
                    "email": "",
                    "created_at": "2025-07-01"
                }""";

        mockMvc.perform(post("/users")
                        .content(dto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void patch() throws Exception {
        String patchNode = "[]";

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{0}", "0")
                        .content(patchNode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void patchMany() throws Exception {
        String patchNode = "[]";

        mockMvc.perform(MockMvcRequestBuilders.patch("/users")
                        .param("ids", "")
                        .content(patchNode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void delete1() throws Exception {
        mockMvc.perform(delete("/users/{0}", "0"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteMany() throws Exception {
        mockMvc.perform(delete("/users")
                        .param("ids", ""))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
