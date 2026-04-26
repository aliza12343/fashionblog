package org.example.capstone2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.capstone2.dto.OrderDTO;
import org.example.capstone2.entity.Order;
import org.example.capstone2.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Orders", description = "Order placement and management")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Place an order", description = "Creates a new order; works for both guest and authenticated users")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order placed successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody OrderDTO dto,
            Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : null;
        Order order = orderService.createOrder(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", order.getId(),
                "orderNumber", String.format("SA-%06d", order.getId()),
                "status", order.getStatus(),
                "message", "Order placed successfully"
        ));
    }

    @Operation(summary = "Get all orders (Admin only)")
    @ApiResponse(responseCode = "200", description = "List of all orders")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Get order by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
