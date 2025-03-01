package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.BASE_URL + "/products")
public class ProductController {
}
