package com.javatechie.service;

import com.javatechie.dto.BorrowBooksDTO;
import com.javatechie.entity.Books;

import java.util.List;

public interface BorrowBooksService {
    List<Books> searchBooksForMemberCanBorrow(String searchQuery);

    List<BorrowBooksDTO> getBorrowedBooksByUser(String email);

    void deleteBorrowedBook(Long borrowId);

    void returnBook(Long borrowId);

//void borrowBook(UserInfo user, Books book);
//    Books getBookByIsbn(String isbn);
}
