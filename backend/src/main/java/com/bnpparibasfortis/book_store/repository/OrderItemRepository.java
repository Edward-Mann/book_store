package com.bnpparibasfortis.book_store.repository;

import com.bnpparibasfortis.book_store.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}

