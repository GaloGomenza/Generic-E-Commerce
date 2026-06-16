package com.generic.e_commerce.user.service;

import com.generic.e_commerce.user.dto.request.CreateUserRequest;
import com.generic.e_commerce.user.dto.request.LoginRequest;
import com.generic.e_commerce.user.dto.request.UpdateUserRequest;
import com.generic.e_commerce.user.dto.response.LoginResponse;
import com.generic.e_commerce.user.dto.response.UserResponse;
import com.generic.e_commerce.user.model.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    UserResponse getUserById(Long id);
    UserResponse getCurrentUser(String email);
    List<UserResponse> getAllUsers();
    void deleteUser(Long id);
    LoginResponse login(LoginRequest request);
    void logout(String email);
    User getUserByEmail(String email);
}
