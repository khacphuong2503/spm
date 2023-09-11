package com.javatechie.entity;

import org.springframework.data.jpa.domain.Specification;

public class UserInfoSpecifications {
    public static Specification<UserInfo> hasNameLike(String keyword) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<UserInfo> hasPhoneNumberLike(String keyword) {
        return (root, query, builder) ->
                builder.like(root.get("phoneNumber"), "%" + keyword + "%");
    }
}
