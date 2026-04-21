package org.treyenwilson.capstone.eventbooking.mapper;

import org.springframework.stereotype.Component;
import org.treyenwilson.capstone.eventbooking.dto.UserRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        // Do NOT include password in response for security
        response.setPassword(null);
        response.setRole(user.getRole());
        return response;
    }

    public User toEntity(UserRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        return user;
    }
}
