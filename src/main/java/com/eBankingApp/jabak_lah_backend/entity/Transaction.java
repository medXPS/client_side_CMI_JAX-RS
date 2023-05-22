package com.eBankingApp.jabak_lah_backend.entity;

import com.eBankingApp.jabak_lah_backend.model.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    private double amount;
    private String creditor;
    @Temporal(TemporalType.DATE)
    private Date date;
    private TransactionStatus transactionStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paymentAccountId")
    @JsonIgnoreProperties("transactions") // Add this annotation to ignore the transactions field during serialization
    private PaymentAccount paymentAccount;
}
