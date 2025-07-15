package com.bnpparibasfortis.book_store.repository;

import com.bnpparibasfortis.book_store.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {}

