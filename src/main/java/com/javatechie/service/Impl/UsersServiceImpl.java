package com.javatechie.service.Impl;

import com.javatechie.controller.errors.NotFoundException;
import com.javatechie.dto.UserInfoDTO;
import com.javatechie.entity.Books;
import com.javatechie.entity.UserInfo;
import com.javatechie.entity.UserInfoSpecifications;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.UsersService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UserInfoRepository userInfoRepository;


    @Override
    public List<UserInfoDTO> saveUserData(List<UserInfoDTO> userInfoList, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            throw new ValidationException(String.valueOf(errors));
        }

        List<UserInfoDTO> savedUserDTOs = new ArrayList<>();

        for (UserInfoDTO userInfoDTO : userInfoList) {
            UserInfo existingUser = userInfoRepository.findByEmail(userInfoDTO.getEmail());

            if (existingUser != null) {
                // User already exists, update their data
                existingUser.setName(userInfoDTO.getName());
                existingUser.setPhoneNumber(userInfoDTO.getPhoneNumber());
                existingUser.setPassword(userInfoDTO.getPassword());

                validateAndFixUserData(existingUser);
                UserInfo savedUser = userInfoRepository.save(existingUser);

                UserInfoDTO savedUserDTO = new UserInfoDTO();
                savedUserDTO.setName(savedUser.getName());
                savedUserDTO.setPhoneNumber(savedUser.getPhoneNumber());
                savedUserDTO.setPassword(savedUser.getPassword());
                savedUserDTO.setEmail(savedUser.getEmail());
                savedUserDTOs.add(savedUserDTO);
            } else {
                // User does not exist, save as a new user
                UserInfo newUser = new UserInfo();
                newUser.setName(userInfoDTO.getName());
                newUser.setPhoneNumber(userInfoDTO.getPhoneNumber());
                newUser.setPassword(userInfoDTO.getPassword());
                newUser.setEmail(userInfoDTO.getEmail());

                validateAndFixUserData(newUser);
                UserInfo savedUser = userInfoRepository.save(newUser);

                UserInfoDTO savedUserDTO = new UserInfoDTO();
                savedUserDTO.setName(savedUser.getName());
                savedUserDTO.setPhoneNumber(savedUser.getPhoneNumber());
                savedUserDTO.setPassword(savedUser.getPassword());
                savedUserDTO.setEmail(savedUser.getEmail());
                savedUserDTOs.add(savedUserDTO);
            }
        }

        return savedUserDTOs;
    }


    private void validateAndFixUserData(UserInfo user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required.");
        }

//        if (user.getPhoneNumber().length() != 11) {
//            throw new IllegalArgumentException("Phone number must have 10 digits.");
//        }

        if (user.getPassword() == null || user.getPassword().length() < 8 ||
                !user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$")) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one uppercase letter, " +
                    "one lowercase letter, one digit, and one special character.");
        }

    }


    @Override
    public UserInfo updateUser(String email, @Valid UserInfoDTO userInfoDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            throw new ValidationException(String.valueOf(errors));
        }

        UserInfo existingUser = userInfoRepository.findByEmail(email);

        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        existingUser.setName(userInfoDTO.getName());
        existingUser.setPhoneNumber(userInfoDTO.getPhoneNumber());
        existingUser.setPassword(userInfoDTO.getPassword());
        existingUser.setEmail(userInfoDTO.getEmail());

        return userInfoRepository.save(existingUser);
    }


    public void deleteUser(String email) {
        UserInfo existingUser = userInfoRepository.findByEmail(email);

        if (existingUser == null) {
            throw new NotFoundException("User not found");
        }

        if (existingUser.getStatus().equals("active")) {
            existingUser.setStatus("deleted");
            userInfoRepository.save(existingUser);
        } else {
            userInfoRepository.delete(existingUser);
        }
    }


    @Override
    public List<UserInfo> getUserInfo() {
        return userInfoRepository.findAll();
    }

    @Override
    public Page<UserInfo> getUserInfoPagination(Integer pageNumber, Integer pageSize, String sortProperty) {
        Pageable pageable = null;
        if(null!=sortProperty){
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC,sortProperty);
        }else {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC,"title");
        }
        return userInfoRepository.findAll(pageable);
    }

    @Override
    public List<UserInfo> searchUserInfo(String keyword) {
        Specification<UserInfo> spec = Specification.where(UserInfoSpecifications.hasNameLike(keyword))
                .or(UserInfoSpecifications.hasPhoneNumberLike(keyword));
        return userInfoRepository.findAll(spec);
    }

    @Override
    public Page<UserInfo> searchUserInfoPagination(String name, String phoneNumber, Integer pageNumber, Integer pageSize, String sortProperty) {
        Specification<UserInfo> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(UserInfoSpecifications.hasNameLike(name));
        }

        if (phoneNumber != null) {
            spec = spec.and(UserInfoSpecifications.hasPhoneNumberLike(phoneNumber));
        }

        Pageable pageable = null;
        if (sortProperty != null) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sortProperty);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "name");
        }

        return userInfoRepository.findAll(spec, pageable);
    }

}
