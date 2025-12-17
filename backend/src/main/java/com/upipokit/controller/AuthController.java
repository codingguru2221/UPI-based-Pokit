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
        // Only allow parent registration through this endpoint
        if (!signUpRequest.getRole().equals("PARENT")) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Only parent registration is allowed through this endpoint. Children must be created by parents."));
        }
        
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

        // Create a parent record
        Parent parent = new Parent(user, signUpRequest.getUpiId(), signUpRequest.getBankAccountNumber());
        parentRepository.save(parent);

        return ResponseEntity.ok(new MessageResponse("Parent registered successfully!"));
    }
    
    @PostMapping("/child")
    public ResponseEntity<?> createChild(@RequestHeader("Authorization") String authHeader,
                                      @Valid @RequestBody SignupRequest signUpRequest) {
        // Extract token and validate parent
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        String parentUsername = jwtUtils.getUserNameFromJwtToken(token);
        
        User parentUser = userRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
                
        if (parentUser.getRole() != User.Role.PARENT) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Only parents can create child accounts"));
        }
        
        Parent parent = parentRepository.findById(parentUser.getId())
                .orElseThrow(() -> new RuntimeException("Parent record not found"));
        
        // Validate child data
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

        // Create child user account
        User childUser = new User(signUpRequest.getName(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPhone(),
                encoder.encode(signUpRequest.getPassword()),
                User.Role.CHILD,
                signUpRequest.getDateOfBirth());

        userRepository.save(childUser);

        // Create child record linked to parent
        Child child = new Child(childUser, parent);
        childRepository.save(child);

        return ResponseEntity.ok(new MessageResponse("Child account created successfully!"));
    }
}