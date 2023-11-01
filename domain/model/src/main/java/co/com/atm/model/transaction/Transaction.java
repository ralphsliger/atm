package co.com.atm.model.transaction;

import co.com.atm.model.transactiontype.TransactionType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transaction {
    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal finalBalance;
    private Long accountId;
    private String description;
}
