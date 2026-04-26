package org.example.capstone2.service;

import exception.ResourceNotFoundException;
import org.example.capstone2.dto.OrderDTO;
import org.example.capstone2.entity.Order;
import org.example.capstone2.repository.OrderRepository;
import org.example.capstone2.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order createOrder(OrderDTO dto, String username) {
        Order order = new Order();
        order.setCustomerName(dto.getFirstName() + " " + dto.getLastName());
        order.setEmail(dto.getEmail());
        order.setPhone(dto.getPhone());
        order.setShippingAddress(dto.getAddress());
        order.setCity(dto.getCity());
        order.setState(dto.getState());
        order.setZip(dto.getZip());
        order.setCountry(dto.getCountry());
        order.setTotal(dto.getTotal());
        order.setStatus("PENDING");

        if (username != null) {
            userRepository.findByUsername(username).ifPresent(order::setUser);
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }
}
