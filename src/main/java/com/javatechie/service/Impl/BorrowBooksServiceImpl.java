package com.javatechie.service.Impl;

import com.javatechie.controller.errors.NotFoundException;
import com.javatechie.dto.BorrowBooksDTO;
import com.javatechie.entity.*;
import com.javatechie.repository.BooksRepository;
import com.javatechie.repository.BorrowBooksRepository;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.BorrowBooksService;
import com.javatechie.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowBooksServiceImpl implements BorrowBooksService {

    private final BorrowBooksRepository borrowBooksRepository;
    private final BooksRepository booksRepository;
    private final UserInfoRepository userInfoRepository;


    @Override
    public List<Books> searchBooksForMemberCanBorrow(String searchQuery) {
        int quantityThreshold = 0;
        return booksRepository.findByTitleContainingIgnoreCaseAndQuantityGreaterThan(searchQuery, quantityThreshold);
    }


    @Override
    public List<BorrowBooksDTO> getBorrowedBooksByUser(String email) {
        UserInfo user = userInfoRepository.findByEmail(email);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        List<BorrowBooks> borrowedBooks = borrowBooksRepository.findByUser(user);
        return convertToDTOList(borrowedBooks);
    }


    private List<BorrowBooksDTO> convertToDTOList(List<BorrowBooks> borrowedBooks) {
        List<BorrowBooksDTO> dtoList = new ArrayList<>();
        for (BorrowBooks borrowBooks : borrowedBooks) {
            BorrowBooksDTO dto = new BorrowBooksDTO();
            dto.setId(borrowBooks.getId());
            dto.setBookTitle(borrowBooks.getBook().getTitle());
            dto.setUserEmail(borrowBooks.getUser().getEmail());
            dto.setBorrowedDate(borrowBooks.getBorrowedDate());
            dto.setReturnDate(borrowBooks.getReturnDate());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public void deleteBorrowedBook(Long borrowId) {
        BorrowBooks borrowBooks = borrowBooksRepository.findById(borrowId)
                .orElseThrow(() -> new NotFoundException("Borrow record not found"));

        borrowBooksRepository.delete(borrowBooks);
    }

    @Override
    @Transactional
    public void returnBook(Long borrowId) {
        BorrowBooks borrowBooks = borrowBooksRepository.findById(borrowId)
                .orElseThrow(() -> new NotFoundException("Borrow record not found"));

        // Check if the book has been returned or not
        if (borrowBooks.getReturnDate() != null) {
            throw new IllegalStateException("Book has already been returned");
        }

        //Update book return information
        borrowBooks.setReturnDate(LocalDateTime.now());

        // Update book inventory
        Books book = borrowBooks.getBook();
        book.setQuantity(book.getQuantity() + 1);
        book.setBorrowedQuantity(book.getBorrowedQuantity() - 1);

        // Save changes to the database
        borrowBooksRepository.save(borrowBooks);
        booksRepository.save(book);

        // Delete the BorrowBooks record from the database
        deleteBorrowedBook(borrowId);
    }
}
