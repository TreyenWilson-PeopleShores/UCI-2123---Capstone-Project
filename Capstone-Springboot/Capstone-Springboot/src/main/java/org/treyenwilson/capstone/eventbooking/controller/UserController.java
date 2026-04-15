package org.treyenwilson.capstone.eventbooking.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.treyenwilson.capstone.eventbooking.dto.UserRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;
import org.treyenwilson.capstone.eventbooking.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("id/{id}")
    // Example call: http://localhost:8080/api/users/id/1
    public ResponseEntity<UserResponse> getByUserId(@PathVariable Long id){
        return ResponseEntity.ok(userService.getByUserId(id));
    }

    @PostMapping()
    // Example call: POST http://localhost:8080/api/users with JSON body
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    // Example call: http://localhost:8080/api/users?page=0&size=10&sortBy=username&ascending=true
    public Page<User> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        try {
            // Map common property names to actual entity field names
            String mappedSortBy = sortBy;
            if ("username".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "username";
            } else if ("role".equalsIgnoreCase(sortBy)) {
                mappedSortBy = "role";
            }

            Sort sort = ascending ? Sort.by(mappedSortBy).ascending() : Sort.by(mappedSortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return userService.findAll(pageable);
        } catch (Exception e) {
            // Fallback to default sorting if there's an issue with the provided sortBy parameter
            Sort sort = Sort.by("id").ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return userService.findAll(pageable);
        }
    }
}
