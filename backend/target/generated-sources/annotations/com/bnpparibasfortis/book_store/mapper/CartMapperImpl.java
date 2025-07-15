package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.CartDto;
import com.bnpparibasfortis.book_store.dto.CartItemDto;
import com.bnpparibasfortis.book_store.model.Cart;
import com.bnpparibasfortis.book_store.model.CartItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T11:03:16+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public CartDto toDto(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartDto cartDto = new CartDto();

        cartDto.setId( cart.getId() );
        cartDto.setCreatedAt( cart.getCreatedAt() );
        cartDto.setItems( cartItemListToCartItemDtoList( cart.getItems() ) );

        return cartDto;
    }

    @Override
    public Cart toEntity(CartDto dto) {
        if ( dto == null ) {
            return null;
        }

        Cart cart = new Cart();

        cart.setId( dto.getId() );

        return cart;
    }

    protected List<CartItemDto> cartItemListToCartItemDtoList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemDto> list1 = new ArrayList<CartItemDto>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( cartItemMapper.toDto( cartItem ) );
        }

        return list1;
    }
}
