package com.example.movie.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

@Sql(
    scripts = "/sql/cleanup.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
    scripts = "/sql/insert_user.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void me_ShouldReturn401_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(
        value = "flowuser",
        userDetailsServiceBeanName = "userDetailsServiceImpl"
    )
    void me_ShouldReturn200_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
               .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(
        value = "flowuser",
        userDetailsServiceBeanName = "userDetailsServiceImpl"
    )
    void logout_Should_WhenAuthenticated() throws Exception {
         mockMvc.perform(post("/api/auth/logout"))
             .andExpect(status().isOk());
    }
}
