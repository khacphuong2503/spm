package com.javatechie.service;

import com.javatechie.dto.BooksDTO;
import com.javatechie.dto.PaginationDTO;
import com.javatechie.dto.SearchCriteriaDTO;
import com.javatechie.entity.Books;
import com.javatechie.entity.BorrowBooks;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface BooksService {
    List < Books > saveBooksData(List < Books > BooksList);

    public Books updateBook(String isbn, BooksDTO bookDTO);

    //    public List<Books> searchBooks(String title, String author, String publisher);

    List < Books > getBooks();

    Books getBooksByIsbn(String isbn);

    List < Books > getBooksByAuthorContaining(String author);

    public Books deleteBooksByIsbn(String isbn);

    public void deleteBookPermanently(Books book);

    Books getBooksByTitle(String title);

    List < Books > searchBooks(String keyword);

    List < Books > searchBooksByQuantityGreaterThan(int quantity);

    List < Books > searchBooksByQuantityLessThan(int quantity);

    List < Books > searchBooksByPublisher(String publisher);

    public void saveBookImage(String isbn, MultipartFile imageFile);

    public void deleteBookImage(String isbn);

    public void updateBookImage(String isbn, MultipartFile imageFile);

    //    public List<Books> searchBooksByTimeRange(String fromTime, String toTime);

//    List<Books> searchBooksByTimeRange(LocalDateTime fromDateTime, LocalDateTime toDateTime);

    List<Books> searchBooksByTimeRange(LocalDateTime fromDateTime, LocalDateTime toDateTime);

    byte[] getBookImage(String isbn);

    public List < Books > importBooksFromCSV(MultipartFile file);

    Page < Books > getBooksPagination(Integer pageNumber, Integer pageSize, String sortProperty);

    Page<Books> searchBooksPagination(SearchCriteriaDTO searchCriteria, PaginationDTO pagination);


    BorrowBooks borrowBook(String userId, String bookId);
}