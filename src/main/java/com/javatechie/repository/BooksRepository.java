package com.javatechie.repository;

import com.javatechie.entity.Books;
import com.javatechie.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Books, Long>, JpaSpecificationExecutor<Books> {
    Books findByIsbn(String isbn);

//    Page<Books> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Books> findByAuthorContaining(String author);

    List<Books> deleteByTitle(String title);

    List<Books> findByTitleContainingOrAuthorContainingOrPublisherContaining(String title, String author, String publisher);

    List<Books> findByQuantityGreaterThan(int quantity);

    List<Books> findByQuantityLessThan(int quantity);

    List<Books> findByPublisherContaining(String publisher);

    Books findTopByTitleOrderByQuantityDesc(String title);

//    List<Books> findByTimeRange(LocalDateTime fromDateTime, LocalDateTime toDateTime);

//    List<Books> findByTimeRange(LocalDateTime fromDateTime, LocalDateTime toDateTime);


//    List<Books> findByTimeBetween(LocalDateTime fromTime, LocalDateTime toTime);

    List<Books> findByTimeBetween(LocalDateTime fromDateTime, LocalDateTime toDateTime);

    List<Books> findByTitleContainingIgnoreCaseAndQuantityGreaterThan(String title, int quantity);
    List<Books> findByTitle(String title);

    // Phương thức tìm sách theo tác giả
    List<Books> findByAuthor(String author);

    // Phương thức tìm sách theo nhà xuất bản
    List<Books> findByPublisher(String publisher);




}
