package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.PublisherDto;
import com.bnpparibasfortis.book_store.model.Publisher;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-15T12:37:45+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class PublisherMapperImpl implements PublisherMapper {

    @Override
    public PublisherDto toDto(Publisher publisher) {
        if ( publisher == null ) {
            return null;
        }

        PublisherDto publisherDto = new PublisherDto();

        publisherDto.setBookIds( booksToIds( publisher.getBooks() ) );
        publisherDto.setId( publisher.getId() );
        publisherDto.setName( publisher.getName() );
        publisherDto.setAddress( publisher.getAddress() );
        publisherDto.setWebsite( publisher.getWebsite() );

        return publisherDto;
    }

    @Override
    public Publisher toEntity(PublisherDto dto) {
        if ( dto == null ) {
            return null;
        }

        Publisher publisher = new Publisher();

        publisher.setId( dto.getId() );
        publisher.setName( dto.getName() );
        publisher.setAddress( dto.getAddress() );
        publisher.setWebsite( dto.getWebsite() );

        return publisher;
    }
}
