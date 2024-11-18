package br.org.fideles.banking.mapper;

import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "role", target = "role")
    User toModel(UserEntity user);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "role", target = "role")
    UserEntity toEntity(User user);
}


