package br.org.fideles.banking.repository;

import br.org.fideles.banking.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT a FROM Account a WHERE a.id = :id")
//    Optional<AccountEntity> findByIdWithLock(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AccountEntity> findById(@Param("id") Long id);

    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}