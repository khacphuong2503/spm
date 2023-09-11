package com.javatechie.repository;

import com.javatechie.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer>, JpaSpecificationExecutor<UserInfo> {
    Optional<UserInfo> findByName(String name);

    Optional<UserInfo> findByStatus(String status);

    boolean existsByEmail(String email);

    UserInfo findByEmail(String email);

    UserInfo findByPhoneNumber(String phoneNumber);

    boolean existsByEmailVerified(boolean emailVerified);

    boolean existsByOtpVerified(boolean emaiVerifiedOTP);
    boolean existsByphoneNumber(String phoneNumber);

    boolean existsByName(String name);

    UserInfo findByEmailAndVerificationCode(String email, String verificationCode);

    UserInfo findByEmailAndEmailVerified(String email, boolean emailVerified);

    @Query("SELECT u FROM UserInfo u WHERE u.name LIKE %:keyword% OR u.email LIKE %:keyword%")
    List<UserInfo> findAllByKeyword(@Param("keyword") String keyword);


//    Page<UserInfo> findAllByKeyword(String keyword, Pageable pageable);
    List<UserInfo> findAll(Specification<UserInfo> spec);
}
