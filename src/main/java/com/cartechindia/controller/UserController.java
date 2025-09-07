package com.cartechindia.controller;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.exception.InvalidCredentialsException;
import com.cartechindia.service.LoginService;
import com.cartechindia.service.UserService;
import com.cartechindia.serviceImpl.CustomUserDetails;
import com.cartechindia.serviceImpl.CustomUserDetailsService;
import com.cartechindia.serviceImpl.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User Management", description = "APIs for user registration and authentication")
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService uds;
    private final LoginService loginService;

    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          CustomUserDetailsService uds,
                          LoginService loginService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.uds = uds;
        this.loginService = loginService;
    }

    @Operation(
            summary = "User login",
            description = "Authenticate user with email and password, return a JWT token if valid.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful, JWT returned",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials"
                    )
            }
    )
    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDetailDto req,
                                                     HttpServletRequest request) {
        boolean success = false;
        String token = null;
        Long userId = null;

        try {
            //Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            //Load user details
            UserDetails user = uds.loadUserByUsername(req.getEmail());
            token = jwtService.generateToken(user.getUsername(), user.getAuthorities());

            //Extract userId if CustomUserDetails exposes it
            if (user instanceof CustomUserDetails customUser) {
                userId = customUser.getId();
            }

            success = true;
            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            throw new InvalidCredentialsException("Email/Password Invalid!");
        } finally {
            //Record login attempt with lat/lng info
            loginService.recordLogin(
                    userId != null ? userId : -1L,
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    success,
                    req.getLongitude(),
                    req.getLatitude()
            );
        }
    }

    @Operation(
            summary = "Register user",
            description = "Register a new user with email, password and other details.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully registered"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed"
                    )
            }
    )
    @PreAuthorize("permitAll()")
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody UserDetailDto userDetailDto) {
        String status = userService.register(userDetailDto);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }
}
