package com.bnpparibasfortis.book_store.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookDto {
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    private String isbn;
    private String description;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;
    private String publishedDate;
    private String publisherName;
    private Long publisherId;
    private List<String> authorNames;
    private List<Long> authorIds;
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;
}
