package com.bnpparibasfortis.book_store.repository;

import com.bnpparibasfortis.book_store.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByUsername(String name);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.orders WHERE c.username = :username")
    Optional<Customer> findByUsernameWithOrders(@Param("username") String username);

    Customer findCustomerById(Long id);
}
