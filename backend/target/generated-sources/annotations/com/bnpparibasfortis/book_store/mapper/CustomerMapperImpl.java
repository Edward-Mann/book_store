package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.CustomerDto;
import com.bnpparibasfortis.book_store.dto.RegisterRequest;
import com.bnpparibasfortis.book_store.model.Cart;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.CustomerStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T11:03:16+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerDto toDto(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerDto customerDto = new CustomerDto();

        customerDto.setOrderIds( ordersToIds( customer.getOrders() ) );
        customerDto.setCartId( customerCartId( customer ) );
        customerDto.setId( customer.getId() );
        customerDto.setName( customer.getName() );
        customerDto.setEmail( customer.getEmail() );
        customerDto.setPhone( customer.getPhone() );
        customerDto.setAddress( customer.getAddress() );
        customerDto.setRegisteredDate( customer.getRegisteredDate() );
        customerDto.setRole( customer.getRole() );
        if ( customer.getStatus() != null ) {
            customerDto.setStatus( customer.getStatus().name() );
        }
        customerDto.setUsername( customer.getUsername() );

        return customerDto;
    }

    @Override
    public Customer toEntity(CustomerDto dto) {
        if ( dto == null ) {
            return null;
        }

        Customer customer = new Customer();

        customer.setId( dto.getId() );
        customer.setName( dto.getName() );
        customer.setEmail( dto.getEmail() );
        customer.setPhone( dto.getPhone() );
        customer.setAddress( dto.getAddress() );
        customer.setRegisteredDate( dto.getRegisteredDate() );
        customer.setUsername( dto.getUsername() );
        customer.setRole( dto.getRole() );
        if ( dto.getStatus() != null ) {
            customer.setStatus( Enum.valueOf( CustomerStatus.class, dto.getStatus() ) );
        }

        return customer;
    }

    @Override
    public Customer toEntityForUpdate(CustomerDto dto) {
        if ( dto == null ) {
            return null;
        }

        Customer customer = new Customer();

        customer.setName( dto.getName() );
        customer.setEmail( dto.getEmail() );
        customer.setPhone( dto.getPhone() );
        customer.setAddress( dto.getAddress() );

        return customer;
    }

    @Override
    public Customer toEntity(RegisterRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Customer customer = new Customer();

        customer.setName( dto.getName() );
        customer.setEmail( dto.getEmail() );
        customer.setPhone( dto.getPhone() );
        customer.setAddress( dto.getAddress() );
        customer.setUsername( dto.getUsername() );
        customer.setPassword( dto.getPassword() );

        return customer;
    }

    private Long customerCartId(Customer customer) {
        if ( customer == null ) {
            return null;
        }
        Cart cart = customer.getCart();
        if ( cart == null ) {
            return null;
        }
        Long id = cart.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
