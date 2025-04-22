package com.ecommerce.project.security.services;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse fetchAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
