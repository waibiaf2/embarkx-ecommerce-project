package com.ecommerce.project.security.services;

import com.ecommerce.project.payload.UserResponse;

public interface UserService {
    UserResponse fetchAllUsers(
        Integer pageNumber,
        Integer pageSize,
        String sortBy,
        String sortOrder
    );
}
