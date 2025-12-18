package com.upipokit.service;

import com.upipokit.dto.ParentLoginRequest;
import com.upipokit.dto.ParentRegisterRequest;
import com.upipokit.dto.ParentResponse;
import com.upipokit.entity.Parent;
import com.upipokit.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ParentResponse register(ParentRegisterRequest req) {
        // Check unique email/phone
        Optional<Parent> byEmail = parentRepository.findByEmail(req.getEmail());
        if (byEmail.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        Optional<Parent> byPhone = parentRepository.findByPhone(req.getPhone());
        if (byPhone.isPresent()) {
            throw new IllegalArgumentException("Phone already registered");
        }

        Parent p = new Parent();
        p.setFullName(req.getFullName());
        p.setEmail(req.getEmail());
        p.setPhone(req.getPhone());
        p.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        p.setUpiId(req.getUpiId());

        Parent saved = parentRepository.save(p);
        return toDto(saved);
    }

    public ParentResponse login(ParentLoginRequest req) {
        Parent p = parentRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), p.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return toDto(p);
    }

    public ParentResponse getParentById(Integer parentId) {
        Parent p = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        return toDto(p);
    }

    private ParentResponse toDto(Parent p) {
        ParentResponse r = new ParentResponse();
        r.setParentId(p.getParentId());
        r.setFullName(p.getFullName());
        r.setEmail(p.getEmail());
        r.setPhone(p.getPhone());
        r.setUpiId(p.getUpiId());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
