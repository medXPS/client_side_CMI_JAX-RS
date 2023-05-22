package com.eBankingApp.jabak_lah_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String CIN;
    private String phoneNumber;
    private String password;
    private Date createdDate;

    @JsonManagedReference // Add this annotation to handle the circular reference
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentAccountId", referencedColumnName = "paymentAccountId")
    private PaymentAccount paymentAccount;
}
