package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.PublisherDto;
import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    @Mapping(source = "books", target = "bookIds", qualifiedByName = "booksToIds")
    PublisherDto toDto(Publisher publisher);

    @Named("booksToIds")
    default List<Long> booksToIds(List<Book> books) {
        if (books == null) {
            return new ArrayList<>();
        }
        return books.stream()
                .map(Book::getId)
                .toList();
    }

    @Mapping(target = "books", ignore = true)
    Publisher toEntity(PublisherDto dto);
}