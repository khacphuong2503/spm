package com.javatechie.service.Impl;

import com.javatechie.dto.ProductDTO;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.ProductService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final UserInfoRepository repository;
    private final PasswordEncoder passwordEncoder;

    private List<ProductDTO> productList;
//    List < ProductDTO > productList = null;

    @PostConstruct
    public void loadProductsFromDB() {
        productList = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> ProductDTO.builder()
                        .productId(i)
                        .name("product " + i)
                        .qty(new Random().nextInt(10))
                        .price(new Random().nextInt(5000)).build()
                ).collect(Collectors.toList());
    }

    @Override
    public List < ProductDTO > getProducts() {
        return productList;
    }

    @Override
    public ProductDTO getProduct(int id) {
        return productList.stream()
                .filter(product -> product.getProductId() == id)
                .findAny()
                .orElseThrow(() -> new RuntimeException("product " + id + " not found"));
    }

    @Override
    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "Sign Up Success ";
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword, String sortBy, String sortOrder, int page, int size) {
        // Tìm kiếm sản phẩm dựa trên từ khóa
        List<ProductDTO> filteredProducts = productList.stream()
                .filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        // Sắp xếp sản phẩm
        Comparator<ProductDTO> comparator;
        if (sortOrder.equalsIgnoreCase("asc")) {
            comparator = Comparator.comparing(ProductDTO::getName);
        } else {
            comparator = Comparator.comparing(ProductDTO::getName).reversed();
        }
        filteredProducts.sort(comparator);

        // Áp dụng phân trang
        int totalProducts = filteredProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalProducts);

        // Trả về danh sách sản phẩm theo trang và kích thước trang yêu cầu
        if (startIndex >= endIndex) {
            return Collections.emptyList(); // Trang không có sản phẩm
        } else {
            return filteredProducts.subList(startIndex, endIndex);
        }
    }

}