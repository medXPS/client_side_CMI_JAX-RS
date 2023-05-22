package com.eBankingApp.jabak_lah_backend.repository;

import com.eBankingApp.jabak_lah_backend.entity.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount,Long> {
}
