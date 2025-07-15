package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.BookDto;
import com.bnpparibasfortis.book_store.model.Author;
import com.bnpparibasfortis.book_store.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "publisher.name", target = "publisherName")
    @Mapping(source = "publisher.id", target = "publisherId")
    @Mapping(source = "authors", target = "authorIds", qualifiedByName = "authorsToIds")
    @Mapping(source = "authors", target = "authorNames", qualifiedByName = "authorsToNames")
    BookDto toDto(Book book);

    @Named("authorsToIds")
    default List<Long> authorsToIds(Set<Author> authors) {
        if (authors == null) {
            return new ArrayList<>();
        }
        return authors.stream()
                .map(Author::getId)
                .toList();
    }

    @Named("authorsToNames")
    default List<String> authorsToNames(Set<Author> authors) {
        return authors.stream()
                .map(Author::getFullName)
                .toList();
    }


    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    Book toEntity(BookDto dto);
}
