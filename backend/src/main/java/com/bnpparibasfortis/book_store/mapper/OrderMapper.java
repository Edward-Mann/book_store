package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.OrderDto;
import com.bnpparibasfortis.book_store.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "items", target = "items")
    OrderDto toDto(Order order);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "items", ignore = true)
    Order toEntity(OrderDto dto);
}
