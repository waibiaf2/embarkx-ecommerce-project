package com.ecommerce.project.security.services;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.UserDTO;
import com.ecommerce.project.payload.UserResponse;
import com.ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public UserServiceImpl(
        UserRepository userRepository,
        ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserResponse fetchAllUsers(
        Integer pageNumber,
        Integer pageSize,
        String sortBy,
        String sortOrder
    ) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ?
            Sort.by(sortBy).ascending() :
            Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> usersPage = userRepository.findAll(pageDetails);
        List<User> userList = usersPage.getContent();

        if (userList.isEmpty())
            throw new APIException("No Users Found");

        List<UserDTO> userDTOs = userList.stream().map(
            user -> modelMapper.map(user, UserDTO.class)
        ).toList();

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDTOs);
        userResponse.setPageNumber(pageNumber);
        userResponse.setPageSize(pageSize);
        userResponse.setTotalElements(usersPage.getTotalElements());
        userResponse.setLastPage(usersPage.isLast());
        userResponse.setTotalPages(usersPage.getTotalPages());

        return userResponse;
    }
}
