package com.ecommerce.project.controller;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressServiceImpl;
import com.ecommerce.project.utils.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("AppConstants.BASE_URL" + "/addresses")
public class AddressController {
    AddressServiceImpl addressService;
    AuthUtil authUtils;
    
    public AddressController(
        AddressServiceImpl addressService,
        AuthUtil authUtils
    ) {
        this.addressService = addressService;
        this.authUtils = authUtils;
    }
    
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(
        @Valid @RequestBody AddressDTO address
    ) {
        User user = authUtils.loggedInUser();
        AddressDTO addressDTO = addressService.createAddress(address, user);
        return new ResponseEntity<>(addressDTO, HttpStatus.CREATED);
    }
}