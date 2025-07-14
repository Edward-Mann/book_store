package com.bnpparibasfortis.book_store.repository;

import com.bnpparibasfortis.book_store.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {}

