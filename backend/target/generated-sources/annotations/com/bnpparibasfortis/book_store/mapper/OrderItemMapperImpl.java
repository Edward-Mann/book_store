package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.OrderItemDto;
import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Order;
import com.bnpparibasfortis.book_store.model.OrderItem;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T12:37:45+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class OrderItemMapperImpl implements OrderItemMapper {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public OrderItemDto toDto(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemDto orderItemDto = new OrderItemDto();

        orderItemDto.setBook( bookMapper.toDto( orderItem.getBook() ) );
        orderItemDto.setBookId( orderItemBookId( orderItem ) );
        orderItemDto.setOrderId( orderItemOrderId( orderItem ) );
        orderItemDto.setId( orderItem.getId() );
        orderItemDto.setQuantity( orderItem.getQuantity() );
        orderItemDto.setPrice( orderItem.getPrice() );

        return orderItemDto;
    }

    @Override
    public OrderItem toEntity(OrderItemDto dto) {
        if ( dto == null ) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setId( dto.getId() );
        orderItem.setQuantity( dto.getQuantity() );
        orderItem.setPrice( dto.getPrice() );

        return orderItem;
    }

    private Long orderItemBookId(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }
        Book book = orderItem.getBook();
        if ( book == null ) {
            return null;
        }
        Long id = book.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long orderItemOrderId(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }
        Order order = orderItem.getOrder();
        if ( order == null ) {
            return null;
        }
        Long id = order.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
