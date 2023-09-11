package com.javatechie.service;

import com.javatechie.dto.BooksDTO;
import com.javatechie.dto.UserInfoDTO;
import com.javatechie.entity.Books;
import com.javatechie.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;

public interface UsersService {
    List<UserInfoDTO> saveUserData(List<UserInfoDTO> UserInfoList , BindingResult bindingResult);

    UserInfo updateUser(String email, @Valid UserInfoDTO userInfoDTO, BindingResult bindingResult);


    void deleteUser(String email);

    List<UserInfo> getUserInfo();

    Page<UserInfo> getUserInfoPagination(Integer pageNumber, Integer pageSize, String sortProperty);

    List<UserInfo> searchUserInfo(String keyword);

//    List<UserInfo> searchSpecificationshUserInfo(String keyword);

    Page<UserInfo> searchUserInfoPagination(String name, String phoneNumber, Integer pageNumber, Integer pageSize, String sortProperty);

}
