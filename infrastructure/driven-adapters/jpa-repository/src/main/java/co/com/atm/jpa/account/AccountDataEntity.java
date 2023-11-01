package co.com.atm.jpa.account;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;

    @Column(precision = 10, scale = 1)
    private BigDecimal balance;



}
