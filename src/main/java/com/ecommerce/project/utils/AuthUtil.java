package com.ecommerce.project.utils;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    private final UserRepository userRepository;
    
    public AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public String loggedInEmail() {
        Authentication authentication = getAuthenticationObj();
        User user = userRepository.findByUserName(getAuthenticationObj().getName())
            .orElseThrow(
                () -> new UsernameNotFoundException("User with username: " + authentication.getName() + "Not Found")
            );
        
        return user.getEmail();
    }
    
    public Long loggedInUserId() {
        Authentication authentication = getAuthenticationObj();
        User user = userRepository.findByUserName(authentication.getName())
            .orElseThrow(
                () -> new UsernameNotFoundException("User with username: " + authentication.getName() + "Not Found")
            );
        
        return user.getUserId();
    }
    
    public User loggedInUser() {
        Authentication authentication = getAuthenticationObj();

        return userRepository.findByUserName(authentication.getName()).orElseThrow(
            () -> new UsernameNotFoundException("User with username: " + authentication.getName() + "Not Found")
        );
    }
    
    private Authentication getAuthenticationObj() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
