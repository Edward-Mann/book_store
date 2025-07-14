package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.OrderItemDto;
import com.bnpparibasfortis.book_store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface OrderItemMapper {

    @Mapping(source = "book", target = "book")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "order.id", target = "orderId")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "book", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDto dto);
}