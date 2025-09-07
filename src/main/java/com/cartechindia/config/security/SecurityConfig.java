package com.cartechindia.config.security;

import com.cartechindia.serviceImpl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService uds;
    private final SecurityRulesProperties rulesProperties;
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthFilter filter,
                          CustomUserDetailsService uds,
                          SecurityRulesProperties rulesProperties,
                          AccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthFilter = filter;
        this.uds = uds;
        this.rulesProperties = rulesProperties;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    // Role hierarchy

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                """
                        ROLE_ADMIN > ROLE_DEALER
                        ROLE_ADMIN > ROLE_SELLER
                        ROLE_ADMIN > ROLE_BUYER
                        ROLE_DEALER > ROLE_USER
                        ROLE_SELLER > ROLE_USER
                        ROLE_BUYER > ROLE_USER"""
        );
    }

    // Hook hierarchy into method security
    @Bean
    public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // Public endpoints
                    auth.requestMatchers(
                            "/user/login",
                            "/user/register",
                            "/dealer/login",
                            "/dealer/register",
                            "/car/add",
                            "/cartech/swagger-ui/**",
                            "/cartech/api-docs/**",
                            "/v3/api-docs/**",
                            "/swagger-ui.html/**",
                            "/swagger-ui/**"
                    ).permitAll();

                    // Dynamic rules from properties
                    for (SecurityRulesProperties.Rule rule : rulesProperties.getRules()) {
                        auth.requestMatchers(rule.getPattern())
                                .hasAnyRole(rule.getRoles().split(","));
                    }

                    // Everything else must be authenticated
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler))
                .userDetailsService(uds)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
