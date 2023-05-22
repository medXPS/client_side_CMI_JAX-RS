package com.eBankingApp.jabak_lah_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentAccount {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long paymentAccountId;

    @OneToOne(mappedBy = "paymentAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Client client;

    private double accountBalance;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    private String bankName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "paymentAccountId")
    private List<Transaction> transactions = new ArrayList<>();
    private  String verificationCode;
}
