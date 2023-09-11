package com.javatechie.dto;

import lombok.Data;

@Data
public class SearchPaginationDTO {
    private SearchCriteriaDTO searchCriteria;
    private PaginationDTO pagination;

}
