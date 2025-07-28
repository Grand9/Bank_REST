package com.example.bankcards.service;

import com.example.bankcards.dto.filter.UserFilter;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.user.UserServiceImpl;
import com.example.bankcards.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("tester");
        user.setEmail("test@example.com");
        user.setIsBanned(false);

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("tester");
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername("tester");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("unknown"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenExists() {
        when(userRepository.existsByUsername("tester")).thenReturn(true);

        assertThat(userService.existsByUsername("tester")).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThat(userService.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

        var result = userService.getUserById(1L);

        assertThat(result).isEqualTo(userResponse);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void deleteUser_ShouldCallDeleteById() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void banUserByIds_ShouldBanUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.banUserByIds(1L);

        assertThat(user.getIsBanned()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        UserFilter filter = new UserFilter(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(userMapper.toDto(user)).thenReturn(userResponse);

        var result = userService.getAllUsers(filter);

        assertThat(result.getContent()).containsExactly(userResponse);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = userService.findById(1L);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found1");
    }
}