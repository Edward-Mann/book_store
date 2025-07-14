package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.CustomerDto;
import com.bnpparibasfortis.book_store.dto.RegisterRequest;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "orders", target = "orderIds", qualifiedByName = "ordersToIds")
    @Mapping(source = "cart.id", target = "cartId")
    CustomerDto toDto(Customer customer);

    @Named("ordersToIds")
    default List<Long> ordersToIds(List<Order> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }
        return orders.stream()
                .map(Order::getId)
                .toList();
    }

    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    Customer toEntity(CustomerDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "registeredDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "status", ignore = true)
    Customer toEntityForUpdate(CustomerDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "registeredDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    Customer toEntity(RegisterRequest dto);
}
