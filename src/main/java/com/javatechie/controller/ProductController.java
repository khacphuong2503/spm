package com.javatechie.controller;

import com.javatechie.dto.ProductDTO;
import com.javatechie.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import com.javatechie.repository.UserInfoRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library")
public class ProductController {

    private final ProductService service;

    private final AuthenticationManager authenticationManager;

    private final UserInfoRepository userInfoRepository;

    private final ProductService productService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ProductDTO>> getAllTheProducts() {
        List<ProductDTO> products = service.getProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/BookManagement")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> welcomeBookmanagement() {
        return ResponseEntity.ok("Welcome to the book management page!");
    }

    @GetMapping("/MemberManagement")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> welcomeMemberManagement() {
        return ResponseEntity.ok("Welcome to the member management page!");
    }

    @GetMapping("/CreateMembers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> welcomeCreateMembers() {
        return ResponseEntity.ok("Welcome to the member creation page!");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        ProductDTO product = service.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/ChangePassword")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> welcomeChangePassword() {
        return ResponseEntity.ok("Welcome to change password page!");
    }

    @GetMapping("/ResetPass")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> welcomeResetPass() {
        return ResponseEntity.ok("Welcome to reset password page!");
    }

    @GetMapping("/ChangeMail")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> welcomeChangeMail() {
        return ResponseEntity.ok("Welcome to change Mail page!");
    }


    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam("keyword") String keyword,
                                                           @RequestParam("sortBy") String sortBy,
                                                           @RequestParam("sortOrder") String sortOrder,
                                                           @RequestParam("page") int page,
                                                           @RequestParam("size") int size) {
        List<ProductDTO> products = productService.searchProducts(keyword, sortBy, sortOrder, page, size);
        return ResponseEntity.ok(products);
    }

}