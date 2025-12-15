package com.pokit.upipocket.service;

import com.pokit.upipocket.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllChildrenByParentId(Long parentId);
    User updateUser(User user);
    void deleteUser(Long id);
    List<User> getAllParents();
}