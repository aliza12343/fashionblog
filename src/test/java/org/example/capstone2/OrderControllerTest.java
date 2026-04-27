package org.example.capstone2;

import exception.ResourceNotFoundException;
import org.example.capstone2.Config.SecurityConfig;
import org.example.capstone2.controller.OrderController;
import org.example.capstone2.entity.Order;
import org.example.capstone2.jwt.JwtAuthFilter;
import org.example.capstone2.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = OrderController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthFilter.class})
)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_ShouldReturn201_WhenValid() throws Exception {
        Order saved = new Order();
        saved.setId(1L);
        saved.setStatus("PENDING");
        saved.setCustomerName("Jane Doe");
        saved.setEmail("jane@example.com");
        saved.setShippingAddress("123 Main St");
        saved.setCity("Columbus");
        saved.setZip("43215");
        saved.setCountry("United States");

        when(orderService.createOrder(any(), any())).thenReturn(saved);

        String body = """
                {
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "email": "jane@example.com",
                  "phone": "614-555-0192",
                  "address": "123 Main St",
                  "city": "Columbus",
                  "zip": "43215",
                  "country": "United States",
                  "total": 3400.0
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Order placed successfully"));
    }

    @Test
    void createOrder_ShouldReturn400_WhenFirstNameMissing() throws Exception {
        String body = """
                {
                  "lastName": "Doe",
                  "email": "jane@example.com",
                  "address": "123 Main St",
                  "city": "Columbus",
                  "zip": "43215",
                  "country": "United States"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOrders_ShouldReturn200_WithList() throws Exception {
        Order o1 = new Order();
        o1.setId(1L);
        o1.setStatus("PENDING");
        o1.setCustomerName("Jane Doe");
        o1.setEmail("jane@example.com");
        o1.setShippingAddress("123 Main St");
        o1.setCity("Columbus");
        o1.setZip("43215");
        o1.setCountry("United States");

        when(orderService.getAllOrders()).thenReturn(List.of(o1));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getOrderById_ShouldReturn200_WhenFound() throws Exception {
        Order order = new Order();
        order.setId(5L);
        order.setStatus("PENDING");
        order.setCustomerName("Jane Doe");
        order.setEmail("jane@example.com");
        order.setShippingAddress("123 Main St");
        order.setCity("Columbus");
        order.setZip("43215");
        order.setCountry("United States");

        when(orderService.getOrderById(5L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void getOrderById_ShouldReturn404_WhenNotFound() throws Exception {
        when(orderService.getOrderById(99L))
                .thenThrow(new ResourceNotFoundException("Order not found: 99"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }
}
