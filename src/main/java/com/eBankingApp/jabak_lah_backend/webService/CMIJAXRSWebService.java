package com.eBankingApp.jabak_lah_backend.webService;
import com.eBankingApp.jabak_lah_backend.entity.Client;
import com.eBankingApp.jabak_lah_backend.entity.PaymentAccount;
import com.eBankingApp.jabak_lah_backend.entity.Transaction;
import com.eBankingApp.jabak_lah_backend.model.TransactionRequest;
import com.eBankingApp.jabak_lah_backend.model.TransactionResponse;
import com.eBankingApp.jabak_lah_backend.model.TransactionStatus;
import com.eBankingApp.jabak_lah_backend.repository.PaymentAccountRepository;
import com.eBankingApp.jabak_lah_backend.repository.TransactionRepository;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/fim/est3Dgate")
public class CMIJAXRSWebService {
    @Autowired
    private PaymentAccountRepository paymentAccountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private VonageClient vonageClient;
    private final String BRAND_NAME = "Vonage";
    private String generateVerificationCode() {
        int min = 1000;
        int max = 9999;
        int verificationCodeInt = min + (int) (Math.random() * (max - min + 1));
        return String.valueOf(verificationCodeInt);
    }
    @Path("/sendVerificationCode/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String sendVerificationCode(@PathParam("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));

        Client client = account.getClient();
        if (client == null) {
            return "Client not found for the specified account.";
        }

        String verificationCode = generateVerificationCode();

        // Send the SMS with the verification code
        String phoneNumber = client.getPhoneNumber();
        TextMessage message = new TextMessage(BRAND_NAME, phoneNumber, "Your ibchi ninak is : " + verificationCode);

        SmsSubmissionResponse response = vonageClient.getSmsClient().submitMessage(message);
        if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
            // Save the verification code in the PaymentAccount entity
            account.setVerificationCode(verificationCode);
            paymentAccountRepository.save(account);
            return "Verification code: [" + verificationCode + "] sent to phone number: " + phoneNumber;
        } else {
           return "message not sent ";
        }

    }

    @Path("/{verificationCode}/makeTransaction")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public TransactionResponse executeTransaction(@PathParam("verificationCode") String verificationCode ,  TransactionRequest transactionRequest) {
        PaymentAccount account = paymentAccountRepository.findById(transactionRequest.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        // Check the verification code
        if (!verificationCode.equals(account.getVerificationCode())) {
            return TransactionResponse.builder()
                    .message("Invalid verification code. Transaction not allowed.")
                    .build();
        }
        Transaction transaction = Transaction.builder()
                .amount(transactionRequest.getAmount())
                .paymentAccount(account)
                .creditor(transactionRequest.getCreditor())
                .date(transactionRequest.getDate())
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        if (account.getAccountBalance() >= transactionRequest.getAmount()) {
            double updateBalance = account.getAccountBalance() - transactionRequest.getAmount();
            account.setAccountBalance(updateBalance);
            transaction.setTransactionStatus(TransactionStatus.SUCCEEDED);
            transactionRepository.save(transaction);

            List<Transaction> transactions = account.getTransactions();
            transactions.add(transaction);
            account.setTransactions(transactions);
            paymentAccountRepository.save(account);
            return TransactionResponse.builder()
                    .message("Transaction executed successfully")
                    .build();
        } else {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            return TransactionResponse.builder()
                    .message("Transaction Failed!! try again later ")
                    .build();
        }
    }
    @Path("/getAccountBalance/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(readOnly = true)
    public double getAccountBalance(@PathParam("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        return account.getAccountBalance();
    }

    @Path("/getTransactionHistories/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistories(@PathParam("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        account.getTransactions().size();
        return account.getTransactions();
    }

    @Path("/getFailedTransactions/{accountId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(readOnly = true)
    public List<Transaction> getFailedTransactions(@PathParam("accountId") long accountId) {
        PaymentAccount account = paymentAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));
        account.getTransactions().size();
        return account.getTransactions().stream()
                .filter(transaction -> transaction.getTransactionStatus() == TransactionStatus.FAILED)
                .collect(Collectors.toList());
}

}
