package br.org.fideles.banking.mapper;

import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import br.org.fideles.banking.entity.AccountEntity;
import br.org.fideles.banking.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "ownerName", target = "ownerName")
    @Mapping(source = "balance", target = "balance")
    Account toModel(AccountEntity accountEntity);

    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "ownerName", target = "ownerName")
    @Mapping(source = "balance", target = "balance")
    AccountEntity toEntity(Account account);
}
