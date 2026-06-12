package com.generic.e_commerce.user.service;

import com.generic.e_commerce.shared.exception.ResourceNotFoundException;
import com.generic.e_commerce.shared.exception.UnauthorizedException;
import com.generic.e_commerce.user.dto.request.CreateUserRequest;
import com.generic.e_commerce.user.dto.request.LoginRequest;
import com.generic.e_commerce.user.dto.request.UpdateUserRequest;
import com.generic.e_commerce.user.dto.response.LoginResponse;
import com.generic.e_commerce.user.dto.response.UserResponse;
import com.generic.e_commerce.user.model.User;
import com.generic.e_commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements com.generic.e_commerce.user.service.UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .role(request.getRole() != null ? request.getRole() : User.Role.USER)
                .build();
        user = userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) {
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) user.setPassword(request.getPassword());
        user = userRepository.save(user);
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserResponse.fromEntity(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return new LoginResponse("Basic " + user.getEmail() + ":" + user.getPassword(), user.getEmail(), user.getName(), user.getRole().name());
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
