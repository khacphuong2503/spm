package com.javatechie.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowBooksDTO {
    private Long id;
    private String bookTitle;
    private String userEmail;
    private LocalDateTime borrowedDate;
    private LocalDateTime returnDate;

}