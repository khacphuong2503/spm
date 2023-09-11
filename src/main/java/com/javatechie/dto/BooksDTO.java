package com.javatechie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
public class BooksDTO {
    private Long id;

    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Author is required.")
    private String author;

    @NotBlank(message = "Publisher is required.")
    private String publisher;

    @NotBlank(message = "ISBN is required.")
    private String isbn;

    @PositiveOrZero(message = "Quantity cannot be negative.")
    private int quantity;

    @Min(value = 0, message = "Borrowed quantity cannot be negative.")
    private int borrowedQuantity;

    @NotNull(message = "Time is required.")
    private LocalDateTime time;
}
