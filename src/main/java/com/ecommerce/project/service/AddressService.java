package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.payload.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);
    AddressResponse getAddresses(Integer pageNumber, Integer pageSize, String orderBy, String sortBy);
    AddressDTO getAddressesById(Long addressId);
    List<AddressDTO> getUserAddresses(User user);
    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);
    String deleteAddress(Long addressId);
}
