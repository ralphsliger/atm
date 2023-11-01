package co.com.atm.jpa.transaction;

import co.com.atm.model.transactiontype.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TransactionDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(precision = 10, scale = 1)
    private BigDecimal amount;

    @Column(name="final_balance", precision = 10, scale = 1)
    private BigDecimal finalBalance;
    @Column(name = "description")
    private String description;

    private Long accountId;

}
