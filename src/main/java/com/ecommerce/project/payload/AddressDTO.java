package com.ecommerce.project.payload;

import com.ecommerce.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for {@link com.ecommerce.project.model.Address}
 */
@AllArgsConstructor
@Getter
public class AddressDTO {
    private final Long addressId;
    private final String street;
    private final String buildingName;
    private final String city;
    private final String state;
    private final String country;
    private final String pincode;
}