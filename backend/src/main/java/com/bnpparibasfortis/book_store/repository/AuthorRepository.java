package com.bnpparibasfortis.book_store.repository;

import com.bnpparibasfortis.book_store.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {}
