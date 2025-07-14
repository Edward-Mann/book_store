package com.bnpparibasfortis.book_store.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private String description;
    private BigDecimal price;
    private String publishedDate;
    private String publisherName;
    private Long publisherId;
    private List<String> authorNames;
    private List<Long> authorIds;
    private int stockQuantity;
}
