package com.example.bankcards.service.user;

import com.example.bankcards.dto.filter.UserFilter;
import com.example.bankcards.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDetails loadUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserResponse getUserById(long id);

    void deleteUser(long id);

    void banUserByIds(long id);

    Page<UserResponse> getAllUsers(UserFilter userFilter);
}