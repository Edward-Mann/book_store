package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.BookDto;
import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Publisher;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T12:37:45+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDto toDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookDto bookDto = new BookDto();

        bookDto.setPublisherName( bookPublisherName( book ) );
        bookDto.setPublisherId( bookPublisherId( book ) );
        bookDto.setAuthorIds( authorsToIds( book.getAuthors() ) );
        bookDto.setAuthorNames( authorsToNames( book.getAuthors() ) );
        bookDto.setId( book.getId() );
        bookDto.setTitle( book.getTitle() );
        bookDto.setIsbn( book.getIsbn() );
        bookDto.setDescription( book.getDescription() );
        bookDto.setPrice( book.getPrice() );
        if ( book.getPublishedDate() != null ) {
            bookDto.setPublishedDate( DateTimeFormatter.ISO_LOCAL_DATE.format( book.getPublishedDate() ) );
        }
        bookDto.setStockQuantity( book.getStockQuantity() );

        return bookDto;
    }

    @Override
    public Book toEntity(BookDto dto) {
        if ( dto == null ) {
            return null;
        }

        Book book = new Book();

        book.setId( dto.getId() );
        book.setTitle( dto.getTitle() );
        book.setIsbn( dto.getIsbn() );
        book.setDescription( dto.getDescription() );
        book.setPrice( dto.getPrice() );
        if ( dto.getPublishedDate() != null ) {
            book.setPublishedDate( LocalDate.parse( dto.getPublishedDate() ) );
        }
        book.setStockQuantity( dto.getStockQuantity() );

        return book;
    }

    private String bookPublisherName(Book book) {
        if ( book == null ) {
            return null;
        }
        Publisher publisher = book.getPublisher();
        if ( publisher == null ) {
            return null;
        }
        String name = publisher.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long bookPublisherId(Book book) {
        if ( book == null ) {
            return null;
        }
        Publisher publisher = book.getPublisher();
        if ( publisher == null ) {
            return null;
        }
        Long id = publisher.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
