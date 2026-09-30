package com.bank.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.dto.AccountResponse;
import com.bank.dto.TransactionRequest;
import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/bank")
public class BankController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService txnService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        Customer user = getUser(session);
        if (user == null) {
            return unauthorized();
        }

        Account acc = accountService.getAccount(user.getId());
        if (acc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));
        }

        return ResponseEntity.ok(AccountResponse.from(acc));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody TransactionRequest request, HttpSession session) {
        Customer user = getUser(session);
        if (user == null) return unauthorized();

        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return notFound("Account not found");

        boolean status = txnService.deposit(acc.getAccountNumber(), request.amount());
        if (!status) return badRequest("Deposit failed. Enter a valid amount.");

        return ResponseEntity.ok(Map.of(
                "message", "₹ " + request.amount() + " deposited successfully!",
                "success", true
        ));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody TransactionRequest request, HttpSession session) {
        Customer user = getUser(session);
        if (user == null) return unauthorized();

        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return notFound("Account not found");

        boolean status = txnService.withdraw(acc.getAccountNumber(), request.amount());
        if (!status) return badRequest("Insufficient balance or invalid amount.");

        return ResponseEntity.ok(Map.of(
                "message", "Withdrawal successful!",
                "success", true
        ));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionRequest request, HttpSession session) {
        Customer user = getUser(session);
        if (user == null) return unauthorized();

        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return notFound("Account not found");

        if (request.receiverAccount() == null || request.receiverAccount().isBlank()) {
            return badRequest("Receiver account number is required.");
        }

        boolean status = txnService.transfer(
                acc.getAccountNumber(),
                request.receiverAccount(),
                request.amount()
        );

        if (!status) return badRequest("Transfer failed. Check balance or account number.");

        return ResponseEntity.ok(Map.of(
                "message", "₹ " + request.amount() + " transferred successfully!",
                "success", true
        ));
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> transactions(HttpSession session) {
        Customer user = getUser(session);
        if (user == null) return unauthorized();

        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return notFound("Account not found");

        return ResponseEntity.ok(txnService.history(acc.getAccountNumber()));
    }

    private Customer getUser(HttpSession session) {
        return (Customer) session.getAttribute("user");
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please login first"));
    }

    private ResponseEntity<Map<String, String>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", message));
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
