package co.com.atm.model.transaction;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionAction {
    private  Long accountId;
    private  BigDecimal amount;
}
