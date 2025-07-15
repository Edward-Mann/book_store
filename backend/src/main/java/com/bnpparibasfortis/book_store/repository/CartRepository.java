package com.bnpparibasfortis.book_store.repository;

import com.bnpparibasfortis.book_store.model.Cart;
import com.bnpparibasfortis.book_store.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerId(Long customerId);

    Optional<Cart> findByCustomer(Customer customer);
}

