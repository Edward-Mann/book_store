package com.bnpparibasfortis.book_store.repository;

import com.bnpparibasfortis.book_store.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}

