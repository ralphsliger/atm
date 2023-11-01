package co.com.atm.model.account;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Account {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
}
