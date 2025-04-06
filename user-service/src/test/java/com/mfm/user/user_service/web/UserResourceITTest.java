package com.mfm.user.user_service.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mfm.user.user_service.AbstractSpringIntegrationTest;
import com.mfm.user.user_service.WithMockJwt;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserResourceITTest extends AbstractSpringIntegrationTest {

    private static final String RESOURCE = "/user";

    @InjectWireMock("access-client")
    private WireMockServer accessClient;

    @Test
    @WithMockJwt
    void testGetProductById() throws Exception {

        Integer id = 1000;
        mockMvc.perform(get(RESOURCE + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", equalTo("Murilo")))
        ;
    }

    @Test
    @WithMockJwt
    void testGetProductById_notFound() throws Exception {
        mockMvc.perform(get(RESOURCE + "/{id}", 1001))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockJwt(roles = {"ROLE_MS_WRITE"})
    void testCrete_new_user() throws Exception {


        String body = """
                { "name": "Murilo", "email": "murilo@test.com", "userName": "mfm12345678a2", "password": "672343" }
                """;

        accessClient.stubFor(WireMock.post("/access/api/access").willReturn(okJson(
                """
                        {"id":2}
                        """
        )));


        MvcResult result = mockMvc.perform(post(RESOURCE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertNotNull(response);
    }

}
