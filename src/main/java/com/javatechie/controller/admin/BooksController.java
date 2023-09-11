package com.javatechie.controller.admin;

import com.javatechie.controller.errors.NotFoundException;
import com.javatechie.dto.BooksDTO;
import com.javatechie.dto.PaginationDTO;
import com.javatechie.dto.SearchCriteriaDTO;
import com.javatechie.dto.SearchPaginationDTO;
import com.javatechie.entity.Books;
import com.javatechie.repository.BooksRepository;
import com.javatechie.service.BooksService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class BooksController {

    private final BooksService booksService;
    private final BooksRepository booksRepository;

    private final ModelMapper modelMapper;


    @PostMapping("/books")
    public List<BooksDTO> saveBooks(@RequestBody List<Books> booksList) {
        List<Books> savedBooks = booksService.saveBooksData(booksList);

        return savedBooks.stream()
                .map(book -> modelMapper.map(book, BooksDTO.class))
                .toList();
    }

    @PutMapping("/books/{isbn}")
    public BooksDTO updateBook(@PathVariable String isbn,@Valid @RequestBody BooksDTO bookDTO) {
        Books updatedBook = booksService.updateBook(isbn, bookDTO);

        return modelMapper.map(updatedBook, BooksDTO.class);
    }


    @PostMapping("/books/search")
    public Page<Books> searchBooksPagination(@RequestBody SearchPaginationDTO searchPaginationDTO) {
        SearchCriteriaDTO searchCriteria = searchPaginationDTO.getSearchCriteria();
        PaginationDTO pagination = searchPaginationDTO.getPagination();

        return booksService.searchBooksPagination(searchCriteria, pagination);
    }

    @GetMapping("/books")
    public List < Books > getBooks() {
        return booksService.getBooks();
    }


    @GetMapping("/getBooksById/{isbn}")
    public BooksDTO getBooksByIsbn(@PathVariable String isbn) {
        Books book = booksService.getBooksByIsbn(isbn);

        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(book, BooksDTO.class);
    }



    @GetMapping("/searchTimeRange")
    public ResponseEntity<List<Books>> searchBooksByTimeRange(
            @RequestParam("fromDateTime") String fromDateTimeString,
            @RequestParam("toDateTime") String toDateTimeString) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("ddMMyyyy HHmmss");

        LocalDateTime fromDateTime = LocalDateTime.parse(fromDateTimeString, inputFormatter);
        LocalDateTime toDateTime = LocalDateTime.parse(toDateTimeString, inputFormatter);

        List<Books> books = booksService.searchBooksByTimeRange(fromDateTime, toDateTime);

        return ResponseEntity.ok(books);
    }


    @DeleteMapping("/books/{isbn}")
    public ResponseEntity < String > deleteBooksByIsbn(@PathVariable("isbn") String isbn) {
        Books deletedBooks = booksService.deleteBooksByIsbn(isbn);

        if (deletedBooks == null) {
            return ResponseEntity.notFound().build();
        }

        if (deletedBooks.isDeleted()) {
            booksService.deleteBookPermanently(deletedBooks);
            return ResponseEntity.ok("Books with ISBN " + isbn + " has been permanently deleted.");
        } else {
            return ResponseEntity.ok("Books with ISBN " + isbn + " has been marked as deleted.");
        }
    }

    @PostMapping("/image/{isbn}")
    public ResponseEntity < String > uploadBookImage(
            @PathVariable("isbn") String isbn,
            @RequestParam("file") MultipartFile file) {
        try {
            booksService.saveBookImage(isbn, file);
            return new ResponseEntity < > ("Book image uploaded successfully", HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity < > (e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity < > (e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/image/{isbn}")
    public ResponseEntity < String > deleteBookImage(@PathVariable("isbn") String isbn) {
        try {
            booksService.deleteBookImage(isbn);
            return new ResponseEntity < > ("Book image deleted successfully", HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity < > (e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity < > (e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/image/{isbn}")
    public ResponseEntity < String > updateBookImage(
            @PathVariable("isbn") String isbn,
            @RequestParam("file") MultipartFile file) {
        try {
            booksService.updateBookImage(isbn, file);
            return new ResponseEntity < > ("Book image updated successfully", HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity < > (e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity < > (e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/image/{isbn}")
    public ResponseEntity<byte[]> getBookImage(@PathVariable String isbn) {
        try {
            byte[] imageBytes = booksService.getBookImage(isbn);

            if (imageBytes != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book image not found");
            }
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }

//    @GetMapping("/getBooksByTitle/{title}")
//    public Books getBooksByTitle(@PathVariable String title) {
//        return booksService.getBooksByTitle(title);
//    }

//    @RequestMapping(value = "/pagingAndShortingBooks/{pageNumber}/{pageSize}", method = RequestMethod.GET)
//    public Page < Books > booksPagination(@PathVariable Integer pageNumber,
//                                          @PathVariable Integer pageSize) {
//
//        return booksService.getBooksPagination(pageNumber, pageSize, null);
//    }

//    @GetMapping("/search")
//    public List < Books > searchBooks(@RequestParam("keyword") String keyword) {
//        return booksService.searchBooks(keyword);
//    }





    @GetMapping("/searchByQuantityGreaterThan")
    public List < Books > searchBooksByQuantityGreaterThan(@RequestParam("quantity") int quantity) {
        return booksService.searchBooksByQuantityGreaterThan(quantity);
    }

    @GetMapping("/searchByQuantityLessThan")
    public List < Books > searchBooksByQuantityLessThan(@RequestParam("quantity") int quantity) {
        return booksService.searchBooksByQuantityLessThan(quantity);
    }

//    @GetMapping("/searchByPublisher")
//    public List < Books > searchBooksByPublisher(@RequestParam("publisher") String publisher) {
//        return booksService.searchBooksByPublisher(publisher);
//    }

    //    @GetMapping("/searchBooksByTimeRange")
    //    public ResponseEntity<List<Books>> searchBooksByTimeRange(
    //            @RequestParam("fromTime") LocalDateTime fromTime,
    //            @RequestParam("toTime") String toTime
    //    ) {
    //        LocalDateTime toDateTime = LocalDateTime.parse(toTime, DateTimeFormatter.ofPattern("ddMMyyyy HHmmss"));
    //        List<Books> books = booksService.searchBooksByTimeRange(fromTime, toDateTime);
    //        return new ResponseEntity<>(books, HttpStatus.OK);
    //    }

    @PostMapping("/import")
    public ResponseEntity < String > importBooks(@RequestParam("file") MultipartFile file) {
        try {
            List < Books > importedBooks = booksService.importBooksFromCSV(file);
            int importedCount = importedBooks.size();
            String responseMessage = "Imported " + importedCount + " books successfully.";
            return new ResponseEntity < > (responseMessage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity < > (e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @RequestMapping(value = "/pagingAndShortingBooks/{pageNumber}/{pageSize}/{sortProperty}",
//            method = RequestMethod.GET)
//    public Page < Books > booksPagination(@PathVariable Integer pageNumber,
//                                          @PathVariable Integer pageSize,
//                                          @PathVariable String sortProperty) {
//        return booksService.getBooksPagination(pageNumber, pageSize, sortProperty);
//    }
}