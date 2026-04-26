package org.example.capstone2;

import exception.ResourceNotFoundException;
import org.example.capstone2.dto.OrderDTO;
import org.example.capstone2.entity.Order;
import org.example.capstone2.entity.User;
import org.example.capstone2.repository.OrderRepository;
import org.example.capstone2.repository.UserRepository;
import org.example.capstone2.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderDTO validDto;

    @BeforeEach
    void setUp() {
        validDto = new OrderDTO();
        validDto.setFirstName("Jane");
        validDto.setLastName("Doe");
        validDto.setEmail("jane@example.com");
        validDto.setPhone("614-555-0192");
        validDto.setAddress("123 Main St");
        validDto.setCity("Columbus");
        validDto.setState("OH");
        validDto.setZip("43215");
        validDto.setCountry("United States");
        validDto.setTotal(3400.0);
    }

    @Test
    void createOrder_ShouldSaveOrderAsGuest_WhenUsernameIsNull() {
        Order saved = new Order();
        saved.setId(1L);
        saved.setStatus("PENDING");
        saved.setCustomerName("Jane Doe");

        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        Order result = orderService.createOrder(validDto, null);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDING", result.getStatus());
        verify(orderRepository).save(any(Order.class));
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void createOrder_ShouldAssociateUser_WhenLoggedIn() {
        User user = new User();
        user.setUsername("jdoe");

        Order saved = new Order();
        saved.setId(2L);
        saved.setUser(user);
        saved.setStatus("PENDING");

        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        Order result = orderService.createOrder(validDto, "jdoe");

        assertNotNull(result.getUser());
        assertEquals("jdoe", result.getUser().getUsername());
    }

    @Test
    void createOrder_ShouldSetFullName_FromFirstAndLastName() {
        Order saved = new Order();
        saved.setCustomerName("Jane Doe");
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        Order result = orderService.createOrder(validDto, null);

        assertEquals("Jane Doe", result.getCustomerName());
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        Order o1 = new Order();
        o1.setId(1L);
        Order o2 = new Order();
        o2.setId(2L);

        when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(o1, o2));

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenFound() {
        Order order = new Order();
        order.setId(5L);

        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(5L);

        assertEquals(5L, result.getId());
    }

    @Test
    void getOrderById_ShouldThrow_WhenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(99L));
    }
}
