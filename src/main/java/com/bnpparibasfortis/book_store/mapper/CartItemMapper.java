package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.CartItemDto;
import com.bnpparibasfortis.book_store.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface CartItemMapper {

    @Mapping(source = "book", target = "book")
    @Mapping(source = "book.id", target = "bookId")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "book", ignore = true)
    @Mapping(target = "cart", ignore = true)
    CartItem toEntity(CartItemDto dto);
}
