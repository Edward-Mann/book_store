package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.CartDto;
import com.bnpparibasfortis.book_store.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    CartDto toDto(Cart cart);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Cart toEntity(CartDto dto);
}
