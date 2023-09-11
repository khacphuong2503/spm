package com.javatechie.entity;

import org.springframework.data.jpa.domain.Specification;

public class BorrowBooksSpecifications {



    public static Specification<Books> hasQuantityGreaterThanZero() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.gt(root.get("quantity"), 0);
    }

    public static Specification<Books> hasTitleLike(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern);
            } else {
                return null;
            }
        };
    }

    public static Specification<Books> hasAuthorLike(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), likePattern);
            } else {
                return null;
            }
        };
    }

    public static Specification<Books> hasPublisherLike(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("publisher")), likePattern);
            } else {
                return null;
            }
        };
    }
}
