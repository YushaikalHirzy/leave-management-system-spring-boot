package com.example.leavemanagementsystem.service;

import com.example.leavemanagementsystem.dto.JwtResponseDto;
import com.example.leavemanagementsystem.dto.LoginRequestDto;
import com.example.leavemanagementsystem.dto.SignupRequestDto;
import com.example.leavemanagementsystem.exception.ResourceNotFoundException;
import com.example.leavemanagementsystem.model.User;
import com.example.leavemanagementsystem.repository.UserRepository;
import com.example.leavemanagementsystem.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate<String, String> redisTemplate; // RedisTemplate for token caching

    // Login and cache token session in Redis
    public JwtResponseDto authenticateUser(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername(); // this is usually the unique username

        // Check if a token already exists for this user in Redis
        String existingToken = redisTemplate.opsForValue().get("token:" + username);

        if (existingToken != null) {
            // Restore reverse mapping to support logout properly
            redisTemplate.opsForValue().set("user:" + existingToken, username, 30, TimeUnit.MINUTES);

            User cachedUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            return new JwtResponseDto(
                    existingToken,
                    cachedUser.getId(),
                    cachedUser.getUsername(),
                    cachedUser.getEmail(),
                    cachedUser.getRole().name()
            );
        }


        // ❌ No token exists, generate new one
        String newToken = jwtTokenProvider.generateToken(authentication);

        // Store token by username
        redisTemplate.opsForValue().set("token:" + username, newToken, 30, TimeUnit.MINUTES);

        // Also store reverse (token -> username) so you can validate by token
        redisTemplate.opsForValue().set("user:" + newToken, username, 30, TimeUnit.MINUTES);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new JwtResponseDto(
                newToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }


    public JwtResponseDto validateSession(String token) {
        String username = redisTemplate.opsForValue().get("user:" + token);  // ✅ Use the correct key

        if (username == null) {
            System.out.println("Token not found in cache: " + token);
            return null;
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return new JwtResponseDto(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }


    // Logout and remove token from Redis (invalidate session)
    public void logout(String token) {
        System.out.println("🔒 Logout requested for token: " + token);

        String username = redisTemplate.opsForValue().get("user:" + token);

        if (username != null) {
            System.out.println("✅ Username found in Redis for token: " + username);

            Boolean tokenDeleted = redisTemplate.delete("token:" + username);
            Boolean userTokenDeleted = redisTemplate.delete("user:" + token);

            System.out.println("🧹 token:" + username + " deleted: " + tokenDeleted);
            System.out.println("🧹 user:" + token + " deleted: " + userTokenDeleted);
        } else {
            System.out.println("⚠️ No username found in Redis for token: " + token);
        }
    }


    // Register a new user
    public User registerUser(SignupRequestDto signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword())
        );

        user.setRole(signupRequest.getRole());
        return userRepository.save(user);
    }

    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
