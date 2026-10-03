package com.bank.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        if (user == null) return unauthorized();

        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));

        Map<String, Object> customer = new HashMap<>();
        customer.put("id", user.getId());
        customer.put("name", user.getName());
        customer.put("email", user.getEmail());
        customer.put("role", user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("id", acc.getId());
        response.put("accountNumber", acc.getAccountNumber());
        response.put("balance", acc.getBalance());
        response.put("customer", customer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> request, HttpSession session) {
        Customer user = getUser(session);
        if (user == null) return unauthorized();
        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return notFound("Account not found");

        double amount = number(request.get("amount"));
        boolean status = txnService.deposit(acc.getAccountNumber(), amount);
        if (!status) return badRequest("Deposit failed. Enter a valid amount.");
        return ResponseEntity.ok(Map.of("message", "₹ " + amount + " deposited successfully!", "success", true));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> request, HttpSession session) {
        Customer user = getUser(session);
        if (user == null) return unauthorized();
        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return notFound("Account not found");

        double amount = number(request.get("amount"));
        boolean status = txnService.withdraw(acc.getAccountNumber(), amount);
        if (!status) return badRequest("Insufficient balance or invalid amount.");
        return ResponseEntity.ok(Map.of("message", "Withdrawal successful!", "success", true));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> request, HttpSession session) {
        Customer user = getUser(session);
        if (user == null) return unauthorized();
        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return notFound("Account not found");

        Object receiverValue = request.get("receiverAccount");
        String receiverAccount = receiverValue == null ? null : receiverValue.toString().trim();
        if (receiverAccount == null || receiverAccount.isBlank()) return badRequest("Receiver account number is required.");

        double amount = number(request.get("amount"));
        boolean status = txnService.transfer(acc.getAccountNumber(), receiverAccount, amount);
        if (!status) return badRequest("Transfer failed. Check balance or account number.");
        return ResponseEntity.ok(Map.of("message", "₹ " + amount + " transferred successfully!", "success", true));
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> transactions(HttpSession session) {
        Customer user = getUser(session);
        if (user == null) return unauthorized();
        Account acc = accountService.getAccount(user.getId());
        if (acc == null) return notFound("Account not found");
        return ResponseEntity.ok(txnService.history(acc.getAccountNumber()));
    }

    private double number(Object value) {
        if (value == null) return -1;
        try { return Double.parseDouble(value.toString()); }
        catch (NumberFormatException e) { return -1; }
    }

    private Customer getUser(HttpSession session) { return (Customer) session.getAttribute("user"); }
    private ResponseEntity<Map<String, String>> unauthorized() { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please login first")); }
    private ResponseEntity<Map<String, String>> notFound(String message) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", message)); }
    private ResponseEntity<Map<String, String>> badRequest(String message) { return ResponseEntity.badRequest().body(Map.of("message", message)); }
}
