package com.example.bankcards.service.user;

import com.example.bankcards.dto.filter.UserFilter;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.debug("loadUserByUsername");
        return userRepository.findByUsername(username).orElseThrow(() -> {return new UserNotFoundException(username);});
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("exists by username {}", username);
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("exists by Email {}", email);
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserResponse getUserById(long id) {
        log.debug("get user by id {}", id);
        User user = findById(id);
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(long id) {
        log.debug("Deleting user: {}", id);
        userRepository.deleteById(id);
        log.debug("Deleted user: {}", id);
    }

    @Override
    public void banUserByIds(long id) {
        log.debug("Banning user: {}", id);
        User user = findById(id);
        user.setIsBanned(true);
        userRepository.save(user);
        log.debug("Banned user: {}", id);
    }

    @Override
    public Page<UserResponse> getAllUsers(UserFilter userFilter) {
        log.debug("get all users");
        Page<User> page = userRepository.findAll(PageRequest.of(userFilter.getOffset(), userFilter.getLimit()));
        return page.map(userMapper::toDto);
    }

    public User findById(Long id) {
        log.debug("find by user: {}", id);
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found" + id));
    }
}