package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.OrderDto;
import com.bnpparibasfortis.book_store.dto.OrderItemDto;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.Order;
import com.bnpparibasfortis.book_store.model.OrderItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T12:37:45+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setCustomerId( orderCustomerId( order ) );
        orderDto.setItems( orderItemListToOrderItemDtoList( order.getItems() ) );
        orderDto.setId( order.getId() );
        orderDto.setOrderDate( order.getOrderDate() );
        orderDto.setStatus( order.getStatus() );
        orderDto.setTotalPrice( order.getTotalPrice() );

        return orderDto;
    }

    @Override
    public Order toEntity(OrderDto dto) {
        if ( dto == null ) {
            return null;
        }

        Order order = new Order();

        order.setId( dto.getId() );
        order.setOrderDate( dto.getOrderDate() );
        order.setStatus( dto.getStatus() );
        order.setTotalPrice( dto.getTotalPrice() );

        return order;
    }

    private Long orderCustomerId(Order order) {
        if ( order == null ) {
            return null;
        }
        Customer customer = order.getCustomer();
        if ( customer == null ) {
            return null;
        }
        Long id = customer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<OrderItemDto> orderItemListToOrderItemDtoList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemDto> list1 = new ArrayList<OrderItemDto>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemMapper.toDto( orderItem ) );
        }

        return list1;
    }
}
