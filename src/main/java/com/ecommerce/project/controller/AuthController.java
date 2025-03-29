package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.jwt.CookieUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.services.UserDetailsImpl;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import com.ecommerce.project.security.services.UserServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(AppConstants.BASE_URL + "/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UserServiceImpl userServiceImpl;
    
    public AuthController(
        AuthenticationManager authenticationManager,
        JwtUtils jwtUtils,
        UserRepository userRepository,
        CookieUtils cookieUtils,
        PasswordEncoder passwordEncoder,
        RoleRepository roleRepository,
        UserDetailsServiceImpl userDetailsServiceImpl,
        UserServiceImpl userServiceImpl
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.cookieUtils = cookieUtils;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }
    
  /*  @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        
        try {
            authentication = authenticationManager
                .authenticate(
                    new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                    )
                );
        } catch (AuthenticationException ex) {
            Map<String, Object> map = new HashMap<>();
            
            map.put("message", "Bad credentials");
            map.put("status", false);
            
            return new ResponseEntity<Object>(map, HttpStatus.OK);
        }
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .toList();
        
        UserInfoResponse response = new UserInfoResponse(
            userDetails.getId(),
            userDetails.getUsername(),
            jwtToken,
            roles
        );
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
   */
    
    @PostMapping("/signin")
    public ResponseEntity<?> signinCookie(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
        } catch (AuthenticationException ex) {
            Map<String, Object> map = new HashMap<>();
            
            map.put("message", "Bad Credentials");
            map.put("status", false);
        }
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        assert authentication != null;
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = cookieUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(
            role -> role.getAuthority()
        ).toList();
        
        UserInfoResponse response = new UserInfoResponse(
            userDetails.getId(),
            userDetails.getUsername(),
            roles
        );
        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .body(response);
        
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        
        boolean userNameExists = userRepository.existsByUserName(signupRequest.getUsername());
        boolean emailExists = userRepository.existsByEmail(signupRequest.getEmail());
        
        if (userNameExists)
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        
        if (emailExists)
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        
        User user = new User(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword())
        );
        
        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(
                () -> new RuntimeException("Error: Role is not found.")
            );
            
            roles.add(userRole);
            
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(
                            () -> new RuntimeException("Error: Role is not found.")
                        );
                        
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(
                            () -> new RuntimeException("Error: Role is not found."));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(
                            () -> new RuntimeException("Error: Role is not found.")
                        );
                        roles.add(userRole);
                }
            });
        }
        
        user.setRoles(roles);
        userRepository.save(user);
        
        return new ResponseEntity<>(
            new MessageResponse("User registered successfully!"),
            HttpStatus.CREATED
        );
    }
    
    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication != null)
            return authentication.getName();
        else
            return "";
    }
    
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .toList();
        
        UserInfoResponse response = new UserInfoResponse(
            userDetails.getId(),
            userDetails.getUsername(),
            roles
        );
        
        return ResponseEntity.ok().body(response);
    }
    
    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie = cookieUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE,cookie.toString())
            .body(new MessageResponse("You've been signed out!"));
    }
}
