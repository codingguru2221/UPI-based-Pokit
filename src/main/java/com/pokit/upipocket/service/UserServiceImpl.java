package com.pokit.upipocket.service;

import com.pokit.upipocket.model.User;
import com.pokit.upipocket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public List<User> getAllChildrenByParentId(Long parentId) {
        return userRepository.findByParentId(parentId);
    }
    
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public List<User> getAllParents() {
        return userRepository.findByUserType(User.UserType.PARENT);
    }
}