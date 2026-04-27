package org.example.capstone2;

import org.example.capstone2.entity.User;
import org.example.capstone2.entity.UserRole;
import org.example.capstone2.repository.UserRepository;
import org.example.capstone2.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedpw");
        user.setRole(UserRole.USER);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("testuser");

        assertEquals("testuser", details.getUsername());
        assertEquals("hashedpw", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ShouldReturnAdminRole_WhenUserIsAdmin() {
        User user = new User();
        user.setUsername("adminuser");
        user.setPassword("hashedpw");
        user.setRole(UserRole.ADMIN);

        when(userRepository.findByUsername("adminuser")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("adminuser");

        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown"));
    }
}
