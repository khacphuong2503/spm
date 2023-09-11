package com.javatechie.controller.user;


import com.javatechie.dto.BorrowBooksDTO;
import com.javatechie.entity.Books;
import com.javatechie.entity.BorrowBooks;
import com.javatechie.service.BooksService;
import com.javatechie.service.BorrowBooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrowbooks")
//@PreAuthorize("hasAuthority('ROLE_USER')")
@RequiredArgsConstructor
public class BorrowBooksController {

    private final BorrowBooksService borrowBooksService;

    private final BooksService booksService;

    @GetMapping("/books/can-borrow")
    public List<Books> getBooksForMemberCanBorrow(@RequestParam String searchQuery) {
        return borrowBooksService.searchBooksForMemberCanBorrow(searchQuery);
    }

    @GetMapping("/borrow")
    public BorrowBooks borrowBook(@RequestParam("email") String email, @RequestParam("isbn") String isbn) {
        return booksService.borrowBook(email, isbn);
    }

    @GetMapping("/users/{email}")
    public ResponseEntity<List<BorrowBooksDTO>> getBorrowedBooksByUser(@PathVariable("email") String email) {
        List<BorrowBooksDTO> borrowedBooksDTO = borrowBooksService.getBorrowedBooksByUser(email);

        if (borrowedBooksDTO.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(borrowedBooksDTO);
        }
    }

    @PostMapping("/borrow/{borrowId}/return")
    public ResponseEntity<String> returnBook(@PathVariable("borrowId") Long borrowId) {
        borrowBooksService.returnBook(borrowId);
        return ResponseEntity.ok("Book returned successfully");
    }
}
