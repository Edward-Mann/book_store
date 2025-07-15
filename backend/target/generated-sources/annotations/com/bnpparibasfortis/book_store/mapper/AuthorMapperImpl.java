package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.AuthorDto;
import com.bnpparibasfortis.book_store.model.Author;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T11:03:17+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class AuthorMapperImpl implements AuthorMapper {

    @Override
    public AuthorDto toDto(Author author) {
        if ( author == null ) {
            return null;
        }

        AuthorDto authorDto = new AuthorDto();

        authorDto.setBookIds( booksToIds( author.getBooks() ) );
        authorDto.setId( author.getId() );
        authorDto.setFirstName( author.getFirstName() );
        authorDto.setLastName( author.getLastName() );
        authorDto.setBio( author.getBio() );

        return authorDto;
    }

    @Override
    public Author toEntity(AuthorDto dto) {
        if ( dto == null ) {
            return null;
        }

        Author author = new Author();

        author.setId( dto.getId() );
        author.setFirstName( dto.getFirstName() );
        author.setLastName( dto.getLastName() );
        author.setBio( dto.getBio() );

        return author;
    }
}
