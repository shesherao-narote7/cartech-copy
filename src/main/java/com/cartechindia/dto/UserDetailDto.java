package com.cartechindia.dto;

import com.cartechindia.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Set;

@Data
public class UserDetailDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean active;

    private String mobileNumber;
    private String email;
    private String passwordHash;
    private String username;
    private String fullName;
    private String dateOfBirth;
    private Set<Role> roles;
}
