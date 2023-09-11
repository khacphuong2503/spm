package com.javatechie.controller.admin;


import com.javatechie.controller.errors.NotFoundException;
import com.javatechie.dto.UserInfoDTO;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UsersController {

    private final UsersService usersService;
    private final UserInfoRepository userInfoRepository;

    @PostMapping("/add")
    public ResponseEntity<?> saveUserData(@Valid @RequestBody List<UserInfoDTO> userInfoList, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        List<UserInfoDTO> savedUserDTOs = usersService.saveUserData(userInfoList, bindingResult);
        return ResponseEntity.ok(savedUserDTOs);
    }

    @PutMapping("/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email, @Valid @RequestBody UserInfoDTO userInfoDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            UserInfo updatedUser = usersService.updateUser(email, userInfoDTO,bindingResult);
            return ResponseEntity.ok(updatedUser);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        try {
            usersService.deleteUser(email);
            return ResponseEntity.ok("User deleted successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/getUserInfo")
    public List<UserInfo> getBooks(){
        return usersService.getUserInfo();
    }


    @GetMapping("/searchandpaging")
    public ResponseEntity<Page<UserInfo>> searchUserInfoPagination(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "sortProperty", required = false) String sortProperty
    ) {
        Page<UserInfo> userInfoPage = usersService.searchUserInfoPagination(name, phoneNumber, pageNumber, pageSize, sortProperty);
        return ResponseEntity.ok(userInfoPage);
    }

//    @GetMapping("/search")
//    public ResponseEntity<Page<UserInfo>> searchUserInfo(
//            @RequestParam("keyword") String keyword,
//            @RequestParam("pageNumber") Integer pageNumber,
//            @RequestParam("pageSize") Integer pageSize,
//            @RequestParam(value = "sortProperty", required = false) String sortProperty
//    ) {
//        Page<UserInfo> userInfoPage = usersService.searchUserInfoPagination(keyword, pageNumber, pageSize, sortProperty);
//        return ResponseEntity.ok(userInfoPage);
//    }


    @RequestMapping(value = "/pagingAndShortingBooks/{pageNumber}/{pageSize}/{sortProperty}",
            method = RequestMethod.GET)
    public Page<UserInfo> booksPagination(@PathVariable Integer pageNumber,
                                       @PathVariable Integer pageSize,
                                       @PathVariable String sortProperty) {
        return usersService.getUserInfoPagination(pageNumber, pageSize, sortProperty);
    }

}
