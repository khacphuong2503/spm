package com.javatechie.dto;

import com.javatechie.entity.Books;
import com.javatechie.entity.UserInfo;
import lombok.Data;

@Data
public class BorrowRequestDTO {

    private UserInfo user;
    private Books book;

}
