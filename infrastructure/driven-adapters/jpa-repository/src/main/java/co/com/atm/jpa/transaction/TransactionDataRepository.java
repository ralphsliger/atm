package co.com.atm.jpa.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface TransactionDataRepository extends JpaRepository<TransactionDataEntity, Long>, QueryByExampleExecutor<TransactionDataEntity> {

    List<TransactionDataEntity> findAllByAccountId(Long accountId);

}
