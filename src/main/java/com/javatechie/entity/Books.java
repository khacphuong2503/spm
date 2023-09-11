package com.javatechie.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "borrowed_quantity")
    private int borrowedQuantity;

    @Column(name="time")
    private LocalDateTime time;

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "image_path")
    private String imagePath;

}
