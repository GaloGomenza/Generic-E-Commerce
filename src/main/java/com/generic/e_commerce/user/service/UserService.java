package com.ecommerce.user.service;

import com.ecommerce.user.dto.request.CreateUserRequest;
import com.ecommerce.user.dto.request.LoginRequest;
import com.ecommerce.user.dto.request.UpdateUserRequest;
import com.ecommerce.user.dto.response.LoginResponse;
import com.ecommerce.user.dto.response.UserResponse;
import com.ecommerce.user.model.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    UserResponse getUserById(Long id);
    UserResponse getCurrentUser(String email);
    List<UserResponse> getAllUsers();
    void deleteUser(Long id);
    LoginResponse login(LoginRequest request);
    User getUserByEmail(String email);
}
