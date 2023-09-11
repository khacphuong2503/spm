package com.javatechie.service.Impl;

import com.javatechie.controller.errors.NotFoundException;
import com.javatechie.controller.errors.OutOfStockException;
import com.javatechie.dto.BooksDTO;
import com.javatechie.dto.PaginationDTO;
import com.javatechie.dto.SearchCriteriaDTO;
import com.javatechie.entity.Books;
import com.javatechie.entity.BooksSpecifications;
import com.javatechie.entity.BorrowBooks;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.BooksRepository;
import com.javatechie.repository.BorrowBooksRepository;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.BooksService;
import com.javatechie.service.EmailVerificationService;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opencsv.CSVReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BooksServiceImpl implements BooksService {

    private final BooksRepository booksRepository;
    private final UserInfoRepository userInfoRepository;
    private final BorrowBooksRepository borrowBooksRepository;

    private final EmailVerificationService emailVerificationService;
    @Override
    public List < Books > saveBooksData(List < Books > booksList) {
        List < Books > validatedBooks = new ArrayList < > ();

        for (Books book: booksList) {
            // Check if the book already exists in the database
            String isbn = String.valueOf(book.getIsbn());
            Books existingBook = booksRepository.findByIsbn(isbn);

            if (existingBook != null) {
                // The book already exists->update
                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPublisher(book.getPublisher());
                existingBook.setQuantity(book.getQuantity());

                validateAndFixBookData(existingBook);
                validatedBooks.add(existingBook);
            } else {
                // Book does not exist, make a new save
                validateAndFixBookData(book);
                validatedBooks.add(book);
            }
        }

        return booksRepository.saveAll(validatedBooks);
    }

    @Override
    public Books updateBook(String isbn, BooksDTO bookDTO) {
        // Check if the book exists in the database
        Books existingBook = booksRepository.findByIsbn(isbn);

        if (existingBook == null) {
            throw new NotFoundException("Book not found");
        }

        // Update book
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setPublisher(bookDTO.getPublisher());
        existingBook.setQuantity(bookDTO.getQuantity());
        existingBook.setBorrowedQuantity(bookDTO.getBorrowedQuantity());
        existingBook.setTime(bookDTO.getTime());

        validateAndFixBookData(existingBook);

        // Save updated books to the database
        return booksRepository.save(existingBook);
    }

    @Override
    public Page < Books > getBooksPagination(Integer pageNumber, Integer pageSize, String sortProperty) {
        Pageable pageable = null;
        if (null != sortProperty) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sortProperty);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "title");
        }
        return booksRepository.findAll(pageable);
    }


    @Override
    public Page<Books> searchBooksPagination(SearchCriteriaDTO searchCriteria, PaginationDTO pagination) {
        Specification<Books> spec = Specification.where(null);

        if (searchCriteria.getTitle() != null) {
            spec = spec.or(BooksSpecifications.hasTitle(searchCriteria.getTitle()));
        }

        if (searchCriteria.getAuthor() != null) {
            spec = spec.and(BooksSpecifications.hasAuthor(searchCriteria.getAuthor()));
        }

        if (searchCriteria.getPublisher() != null) {
            spec = spec.and(BooksSpecifications.hasPublisher(searchCriteria.getPublisher()));
        }

        Pageable pageable = null;
        if (pagination.getSortProperty() != null) {
            pageable = PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), Sort.Direction.ASC, pagination.getSortProperty());
        } else {
            pageable = PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), Sort.Direction.ASC, "title");
        }

        return booksRepository.findAll(spec, pageable);
    }


    @Override
    public BorrowBooks borrowBook(String email, String isbn) {
        // Check the existence of users and books
        UserInfo user = userInfoRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        Books book = booksRepository.findByIsbn(isbn);
        if (book == null) {
            throw new NotFoundException("Book not found");
        }

        if (book.getQuantity() <= 0) {
            throw new OutOfStockException("Book is out of stock");
        }

        // Check if the user has borrowed books of this type before
        List<BorrowBooks> borrowedBooks = borrowBooksRepository.findByUser(user);
        for (BorrowBooks borrowedBook : borrowedBooks) {
            if (borrowedBook.getBook().getIsbn().equals(isbn)) {
                throw new IllegalArgumentException("You have already borrowed this book");
            }
        }


        // Create a new BorrowedBook
        BorrowBooks borrowedBook = new BorrowBooks();
        borrowedBook.setUser(user);
        borrowedBook.setBook(book);
        borrowedBook.setBorrowedDate(LocalDateTime.now());

        // Save BorrowedBook to database
        borrowedBook = borrowBooksRepository.save(borrowedBook);
        Long borrowId = borrowedBook.getId();

        // Reduce inventory of books
        book.setQuantity(book.getQuantity() - 1);
        book.setBorrowedQuantity(book.getBorrowedQuantity() + 1);
        booksRepository.save(book);


        // Send email confirmation to borrow books
        String subject = "Borrow Confirmation";
        String message = "You have successfully borrowed a book. Borrow ID: " + borrowId;
        emailVerificationService.sendEmail(email, subject, message, book.getImagePath());

        return borrowedBook;
    }

    @Override
    public void saveBookImage(String isbn, MultipartFile imageFile) {
        Books book = booksRepository.findByIsbn(isbn);
        if (book == null) {
            throw new NotFoundException("Book not found");
        }

        validateImageFile(imageFile);

        try {
            // Generate a unique filename
            String fileName = generateUniqueFileName(imageFile.getOriginalFilename());

            // Define the file path to save the image
            Path filePath = Path.of("D:\\SparkMinds\\BT1\\258\\jwt-refresh-token\\src\\main\\resources" + fileName);

            // Save the image file to the specified path
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update the book's image path
            book.setImagePath(filePath.toString());

            // Save the updated book to the database
            booksRepository.save(book);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save book image", e);
        }
    }

    @Override
    public void deleteBookImage(String isbn) {
        Books book = booksRepository.findByIsbn(isbn);
        if (book == null) {
            throw new NotFoundException("Book not found");
        }

        String imagePath = book.getImagePath();
        if (imagePath != null) {
            // Delete images from server
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageFile.delete();
            }

            // Clear image path in book entity
            book.setImagePath(null);

            // Save the updated book to the database
            booksRepository.save(book);
        }
    }

    @Transactional
    @Override
    public void updateBookImage(String isbn, MultipartFile imageFile) {
        Books book = booksRepository.findByIsbn(isbn);
        if (book == null) {
            throw new NotFoundException("Book not found");
        }

        // Check and validate file size and file extension
        validateImageFile(imageFile);

        // Delete old images if exist
        String oldImagePath = book.getImagePath();
        if (oldImagePath != null) {
            File oldImageFile = new File(oldImagePath);
            if (oldImageFile.exists()) {
                oldImageFile.delete();
            }
        }

        try {
            // Generate a unique filename
            String fileName = generateUniqueFileName(imageFile.getOriginalFilename());

            // Define the file path to save the image
            Path filePath = Path.of("D:\\SparkMinds\\BT1\\258\\jwt-refresh-token\\src\\main\\resources" + fileName);

            // Save the image file to the specified path
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update the book's image path
            book.setImagePath(filePath.toString());

            // Save the updated book to the database
            booksRepository.save(book);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update book image", e);
        }
    }


    @Override
    public byte[] getBookImage(String isbn) {
        Books book = booksRepository.findByIsbn(isbn);
        if (book == null) {
            throw new NotFoundException("Book not found");
        }

        String imagePath = book.getImagePath();
        if (imagePath != null) {
            try {
                // Read the image file as bytes
                return Files.readAllBytes(Path.of(imagePath));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read book image", e);
            }
        }

        return null;
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        return UUID.randomUUID().toString() + extension;
    }

    @Override
    public List < Books > getBooks() {
        return booksRepository.findAll();
    }

    @Override
    public Books getBooksByIsbn(String isbn) {
        Books book = booksRepository.findByIsbn(isbn);
        if (book == null) {
            throw new NotFoundException("Book not found");
        }
        return book;
    }

    @Override
    public List < Books > getBooksByAuthorContaining(String author) {
        return booksRepository.findByAuthorContaining(author);
    }

    @Override
    public Books deleteBooksByIsbn(String isbn) {
        Books deletedBooks = booksRepository.findByIsbn(isbn);

        deletedBooks.setDeleted(true);
        booksRepository.save(deletedBooks);

        return deletedBooks;
    }

    public void deleteBookPermanently(Books book) {
        booksRepository.delete(book);
    }

    @Override
    public Books getBooksByTitle(String title) {
        return booksRepository.findTopByTitleOrderByQuantityDesc(title);
    }

    @Override
    public List < Books > searchBooks(String keyword) {
        return booksRepository.findByTitleContainingOrAuthorContainingOrPublisherContaining(keyword, keyword, keyword);
    }

    @Override
    public List < Books > searchBooksByQuantityGreaterThan(int quantity) {
        return booksRepository.findByQuantityGreaterThan(quantity);
    }

    @Override
    public List < Books > searchBooksByQuantityLessThan(int quantity) {
        return booksRepository.findByQuantityLessThan(quantity);
    }

    @Override
    public List < Books > searchBooksByPublisher(String publisher) {
        return booksRepository.findByPublisherContaining(publisher);
    }


    @Override
    public List<Books> searchBooksByTimeRange(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        if (fromDateTime.isAfter(toDateTime)) {
            throw new IllegalArgumentException("Invalid time range. From time must be less than or equal to To time.");
        }

        return booksRepository.findByTimeBetween(fromDateTime, toDateTime);
    }


    private void validateAndFixBookData(Books book) {

        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }

        if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
            throw new IllegalArgumentException("Author is required.");
        }

        if (book.getPublisher() == null || book.getPublisher().isEmpty()) {
            throw new IllegalArgumentException("Publisher is required.");
        }

        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            throw new IllegalArgumentException("ISBN is required.");
        }

        if (book.getQuantity() < 0) {
            book.setQuantity(0); //
        }

        if (book.getBorrowedQuantity() < 0) {
            book.setBorrowedQuantity(0); //
        }

    }

    private void validateImageFile(MultipartFile imageFile) {
        // Check file size
        long fileSize = imageFile.getSize();
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (fileSize > maxSize) {
            throw new IllegalArgumentException("File size exceeds the maximum allowed size");
        }

        // Check the file extension
        String fileExtension = FilenameUtils.getExtension(imageFile.getOriginalFilename());
        List < String > allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file extension. Only JPG, JPEG, and PNG files are allowed.");
        }
    }

    @Override
    public List < Books > importBooksFromCSV(MultipartFile file) {
        // Check header, extension and file size
        if (!isValidCSVHeader(file)) {
            throw new IllegalArgumentException("Invalid CSV header.");
        }

        String fileExtension = getFileExtension(file);
        if (!fileExtension.equalsIgnoreCase("csv")) {
            throw new IllegalArgumentException("Only CSV files are allowed.");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("File size should be less than 5MB.");
        }

        List < Books > importedBooks = new ArrayList < > ();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            int lineNumber = 0;
            while ((nextLine = reader.readNext()) != null) {
                lineNumber++;

                if (lineNumber == 1) {
                    continue;
                }

                try {
                    // Process each line of data in the CSV file
                    Books book = processCSVLine(nextLine);
                    importedBooks.add(book);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid CSV data at line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to read CSV file.");
        }

        // Save imported books in the database
        booksRepository.saveAll(importedBooks);

        return importedBooks;
    }

    private boolean isValidCSVHeader(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext();
            if (header != null) {
                return Arrays.asList(header).containsAll(Arrays.asList("title", "author", "publisher", "isbn", "quantity", "borrowed_quantity", "time"));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to read CSV header.");
        }

        return false;
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            return originalFilename.substring(dotIndex + 1);
        }
        return "";
    }

    private Books processCSVLine(String[] csvLine) {
        if (csvLine.length != 7) {
            throw new IllegalArgumentException("Invalid CSV line. Expected 7 columns, but found " + csvLine.length + " columns.");
        }

        String title = csvLine[0];
        String author = csvLine[1];
        String publisher = csvLine[2];
        String isbn = csvLine[3];
        int quantity = Integer.parseInt(csvLine[4]);
        int borrowedQuantity = Integer.parseInt(csvLine[5]);
        String timeString = csvLine[6];

        // Parse the time string into LocalDateTime
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse(timeString, timeFormatter);
        LocalDate currentDate = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.of(currentDate, time);

        Books book = new Books();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setIsbn(isbn);
        book.setQuantity(quantity);
        book.setBorrowedQuantity(borrowedQuantity);
        book.setTime(dateTime);

        return book;
    }
}