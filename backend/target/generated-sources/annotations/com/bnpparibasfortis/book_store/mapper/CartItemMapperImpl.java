package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.CartItemDto;
import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.CartItem;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T12:37:45+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class CartItemMapperImpl implements CartItemMapper {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public CartItemDto toDto(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.setBook( bookMapper.toDto( cartItem.getBook() ) );
        cartItemDto.setBookId( cartItemBookId( cartItem ) );
        cartItemDto.setId( cartItem.getId() );
        cartItemDto.setQuantity( cartItem.getQuantity() );

        return cartItemDto;
    }

    @Override
    public CartItem toEntity(CartItemDto dto) {
        if ( dto == null ) {
            return null;
        }

        CartItem cartItem = new CartItem();

        cartItem.setId( dto.getId() );
        cartItem.setQuantity( dto.getQuantity() );

        return cartItem;
    }

    private Long cartItemBookId(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Book book = cartItem.getBook();
        if ( book == null ) {
            return null;
        }
        Long id = book.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
