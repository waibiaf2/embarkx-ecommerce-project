package com.ecommerce.project.security.services;

import com.ecommerce.project.model.User;

import java.util.List;

public interface UserService {
    List<User> fetchAllUsers();
}
