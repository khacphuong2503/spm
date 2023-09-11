//package com.javatechie.entity;
//
//import com.javatechie.dto.BooksDTO;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class BooksMapper {
//
//    public static List<Books> mapToBooksList(List<BooksDTO> booksDTOList) {
//        List<Books> booksList = new ArrayList<>();
//        for (BooksDTO booksDTO : booksDTOList) {
//            Books books = new Books();
//            // Ánh xạ dữ liệu từ BooksDTO sang Books
//            books.setTitle(booksDTO.getTitle());
//            books.setAuthor(booksDTO.getAuthor());
//            books.setPublisher(booksDTO.getPublisher());
//            // Điều chỉnh các thuộc tính khác tùy thuộc vào yêu cầu của bạn
//            booksList.add(books);
//        }
//        return booksList;
//    }
//
//    // Các phương thức ánh xạ khác (nếu cần)
//
//}
