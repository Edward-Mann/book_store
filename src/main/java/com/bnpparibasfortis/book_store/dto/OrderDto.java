package com.bnpparibasfortis.book_store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalPrice;
    private Long customerId;
    private List<OrderItemDto> items;
}
