package com.javatechie.dto;

import lombok.Data;

@Data
public class PaginationDTO {
    private Integer pageNumber;
    private Integer pageSize;
    private String sortProperty;

}
