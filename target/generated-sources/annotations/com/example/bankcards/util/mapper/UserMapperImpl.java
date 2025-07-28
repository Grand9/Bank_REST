package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-28T17:03:03+0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.username( user.getUsername() );
        userResponse.password( user.getPassword() );
        userResponse.email( user.getEmail() );
        if ( user.getIsBanned() != null ) {
            userResponse.isBanned( user.getIsBanned() );
        }
        if ( user.getRole() != null ) {
            userResponse.role( user.getRole().name() );
        }

        return userResponse.build();
    }
}
