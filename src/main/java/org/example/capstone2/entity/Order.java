package org.example.capstone2.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String customerName;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    private String phone;

    @NotBlank
    @Column(nullable = false)
    private String shippingAddress;

    @NotBlank
    @Column(nullable = false)
    private String city;

    private String state;

    @NotBlank
    @Column(nullable = false)
    private String zip;

    @NotBlank
    @Column(nullable = false)
    private String country;

    private Double total;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"posts", "password", "hibernateLazyInitializer"})
    private User user;
}
