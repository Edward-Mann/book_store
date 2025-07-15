package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.AuthorDto;
import com.bnpparibasfortis.book_store.model.Author;
import com.bnpparibasfortis.book_store.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(source = "books", target = "bookIds", qualifiedByName = "booksToIds")
    AuthorDto toDto(Author author);

    @Named("booksToIds")
    default List<Long> booksToIds(Set<Book> books) {
        if (books == null) {
            return new ArrayList<>();
        }
        return books.stream()
                .map(Book::getId)
                .toList();
    }

    @Mapping(target = "books", ignore = true)
    Author toEntity(AuthorDto dto);
}