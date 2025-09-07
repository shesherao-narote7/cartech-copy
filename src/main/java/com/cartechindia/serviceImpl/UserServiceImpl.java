package com.cartechindia.serviceImpl;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.entity.Role;
import com.cartechindia.entity.User;
import com.cartechindia.exception.InvalidCredentialsException;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public String login(LoginDetailDto loginDetailDto) {
        User user = userRepository.findByEmail(loginDetailDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email/Password Invalid!"));

        // Use PasswordEncoder.matches instead of equals
        if (!passwordEncoder.matches(loginDetailDto.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Email/Password Invalid!");
        }

        return "Successful Login...";
    }

    @Override
    public String register(UserDetailDto userDetailDto) {
        if (userDetailDto == null) {
            throw new RuntimeException("Invalid User Details...");
        }

        if (userRepository.existsByEmail(userDetailDto.getEmail())) {
            return "Email already exists!";
        }

        if (userRepository.existsByMobileNumber(userDetailDto.getMobileNumber())) {
            return "Phone already exists!";
        }

        User user = modelMapper.map(userDetailDto, User.class);

        // Encrypt password before saving
        user.setPasswordHash(passwordEncoder.encode(userDetailDto.getPasswordHash()));
        user.setActive(true);

        // Use roles from DTO if provided, otherwise default to ROLE_USER
        if (userDetailDto.getRoles() == null || userDetailDto.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.ROLE_USER));
        } else {
            user.setRoles(userDetailDto.getRoles());
        }

        userRepository.save(user);
        return "User successfully Registered...";
    }

}
