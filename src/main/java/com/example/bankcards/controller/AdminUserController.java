package com.example.bankcards.controller;

import com.example.bankcards.dto.filter.UserFilter;
import com.example.bankcards.dto.request.RegistrationRequestDto;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.authentication.AuthenticationService;
import com.example.bankcards.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("v1/api/admin/user")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "create user",
            description = "Creates a new user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration user",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegistrationRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "user successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid create")
            }
    )
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid RegistrationRequestDto registrationRequestDto) {
        log.info("Create user: {}", registrationRequestDto);
        return authenticationService.registration(registrationRequestDto);
    }

    @Operation(
            summary = "Delete user",
            description = "Deleted a user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @Parameter @PathVariable("userId") Long userId) {
        log.info("Delete user: {}", userId);
        userService.deleteUser(userId);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Get user by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("get/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable("userId") Long userId) {
        log.info("Get user: {}", userId);
        return userService.getUserById(userId);
    }

    @Operation(
            summary = "Get user with filters",
            description = "Returns a paginated list of users filtered by criteria",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered user list returned successfully"),
                    @ApiResponse(responseCode = "404", description = "Filtered user list not found")
            }
    )
    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> allUsers(@RequestParam(defaultValue = "0", required = false) @Positive Integer offset,
                                       @RequestParam(defaultValue = "10", required = false) @Positive @Max(50) Integer limit) {
        log.info("Get users: {}", offset);
        UserFilter userFilter = new UserFilter(offset, limit);
        return userService.getAllUsers(userFilter);
    }
}