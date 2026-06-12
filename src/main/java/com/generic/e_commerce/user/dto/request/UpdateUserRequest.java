package com.generic.e_commerce.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    @Email
    private String email;

    private String password;

    private String name;
}
