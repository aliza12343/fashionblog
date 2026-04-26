package org.example.capstone2;

import org.example.capstone2.dto.RegisterDTO;
import org.example.capstone2.entity.User;
import org.example.capstone2.entity.UserRole;
import org.example.capstone2.repository.UserRepository;
import org.example.capstone2.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_ShouldSaveUser_WhenUsernameIsNew() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("new@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");

        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setRole(UserRole.USER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(dto);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals(UserRole.USER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("existinguser");
        dto.setPassword("password123");
        dto.setEmail("test@example.com");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("taken@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findByUsername_ShouldThrowException_WhenNotFound() {
        when(userRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findByUsername("ghost"));
    }

    @Test
    void register_ShouldEncodePassword() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("plainpassword");
        dto.setEmail("new@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainpassword")).thenReturn("encodedpassword");

        User savedUser = new User();
        savedUser.setPassword("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(dto);

        assertEquals("encodedpassword", result.getPassword());
        verify(passwordEncoder).encode("plainpassword");
    }
}
