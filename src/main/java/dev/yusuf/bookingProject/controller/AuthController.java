package dev.yusuf.bookingProject.controller;

import dev.yusuf.bookingProject.business.abstracts.UserService;
import dev.yusuf.bookingProject.dto.requests.LoginRequest;
import dev.yusuf.bookingProject.dto.responses.JwtResponse;
import dev.yusuf.bookingProject.exception.UserAlreadyExistsException;
import dev.yusuf.bookingProject.model.User;
import dev.yusuf.bookingProject.security.jwt.JwtUtils;
import dev.yusuf.bookingProject.security.user.HotelUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful!");
        } catch (UserAlreadyExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForUser(authentication);
        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                jwt,
                roles
        ));
    }

}
