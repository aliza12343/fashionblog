package org.example.capstone2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.example.capstone2.controller.ContactController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ContactController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void submitInquiry_ShouldReturn200_WhenValidRequest() throws Exception {
        String body = """
                {
                  "firstName": "Aliza",
                  "lastName": "Rai",
                  "email": "aliza@example.com",
                  "phone": "614-555-0100",
                  "service": "Bridal Styling",
                  "message": "Interested in bridal package"
                }
                """;

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Thank you, Aliza. Our studio will be in touch within 24 hours."));
    }

    @Test
    void submitInquiry_ShouldReturn400_WhenFirstNameMissing() throws Exception {
        String body = """
                {
                  "email": "aliza@example.com",
                  "service": "Bridal Styling"
                }
                """;

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitInquiry_ShouldReturn400_WhenEmailInvalid() throws Exception {
        String body = """
                {
                  "firstName": "Aliza",
                  "email": "not-an-email",
                  "service": "Bridal Styling"
                }
                """;

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitInquiry_ShouldReturn400_WhenServiceMissing() throws Exception {
        String body = """
                {
                  "firstName": "Aliza",
                  "email": "aliza@example.com"
                }
                """;

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
