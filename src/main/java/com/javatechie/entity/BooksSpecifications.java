package com.javatechie.entity;

import org.springframework.data.jpa.domain.Specification;


public class BooksSpecifications {
    public static Specification<Books> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title != null) {
                return criteriaBuilder.like(root.get("title"), "%" + title + "%");
            } else {
                return null;
            }
        };
    }

    public static Specification<Books> hasAuthor(String author) {
        return (root, query, criteriaBuilder) -> {
            if (author != null) {
                return criteriaBuilder.like(root.get("author"), "%" + author + "%");
            } else {
                return null;
            }
        };
    }

    public static Specification<Books> hasPublisher(String publisher) {
        return (root, query, criteriaBuilder) -> {
            if (publisher != null) {
                return criteriaBuilder.like(root.get("publisher"), "%" + publisher + "%");
            } else {
                return null;
            }
        };
    }
}