package com.bnpparibasfortis.book_store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    @NotNull(message = "Book ID must be provided")
    private Long bookId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private BookDto book;
}
