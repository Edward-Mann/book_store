package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.UserDto;
import com.bnpparibasfortis.book_store.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto dto);
}
