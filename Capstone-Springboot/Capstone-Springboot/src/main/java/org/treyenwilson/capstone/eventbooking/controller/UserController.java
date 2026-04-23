package org.treyenwilson.capstone.eventbooking.controller;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.treyenwilson.capstone.eventbooking.dto.UserRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;
import org.treyenwilson.capstone.eventbooking.service.UserService;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    /** Whitelist of field names accepted for ORDER BY to prevent ORDER BY injection. */
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "username", "role");

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
    @PreAuthorize("hasRole('ADMIN')")
    // Example call: http://localhost:8080/api/users?page=0&size=10&sortBy=username&ascending=true
    public Page<User> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {

        String resolvedSort = resolveSortField(sortBy);
        Sort sort = ascending ? Sort.by(resolvedSort).ascending() : Sort.by(resolvedSort).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return userService.findAll(pageable);
    }

    /**
     * Resolves a user-supplied sort field name to a whitelisted entity field name.
     * Falls back to "id" if the supplied value is not in the allowed set,
     * preventing ORDER BY injection via unvalidated sort parameters.
     */
    private String resolveSortField(String sortBy) {
        // All allowed fields are already the canonical names; no alias mapping needed.
        return ALLOWED_SORT_FIELDS.contains(sortBy.toLowerCase()) ? sortBy.toLowerCase() : "id";
    }
}
