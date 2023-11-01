package co.com.atm.model.transaction;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transfer {
    private  Long sourceAccountId;

    private  Long destinationAccountId;

    private  BigDecimal amount;
}
