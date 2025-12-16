package com.upipokit.controller;

import com.upipokit.dto.LoginRequest;
import com.upipokit.dto.SignupRequest;
import com.upipokit.dto.JwtResponse;
import com.upipokit.dto.MessageResponse;
import com.upipokit.entity.User;
import com.upipokit.entity.Parent;
import com.upipokit.entity.Child;
import com.upipokit.repository.UserRepository;
import com.upipokit.repository.ParentRepository;
import com.upipokit.repository.ChildRepository;
import com.upipokit.security.JwtUtils;
import com.upipokit.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ParentRepository parentRepository;

    @Autowired
    ChildRepository childRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getName(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPhone(),
                encoder.encode(signUpRequest.getPassword()),
                User.Role.valueOf(signUpRequest.getRole()),
                signUpRequest.getDateOfBirth());

        userRepository.save(user);

        // If the user is a parent, create a parent record
        if (signUpRequest.getRole().equals("PARENT")) {
            Parent parent = new Parent(user, signUpRequest.getUpiId(), signUpRequest.getBankAccountNumber());
            parentRepository.save(parent);
        }
        // If the user is a child, create a child record
        else if (signUpRequest.getRole().equals("CHILD")) {
            // Find the parent by ID
            Parent parent = parentRepository.findById(signUpRequest.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            Child child = new Child(user, parent);
            childRepository.save(child);
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}