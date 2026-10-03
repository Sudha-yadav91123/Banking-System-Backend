package com.bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.service.AdminService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        ResponseEntity<?> auth = requireAdmin(session);
        if (auth != null) return auth;
        return ResponseEntity.ok(Map.of("message", "Admin dashboard", "role", "ADMIN"));
    }

    @GetMapping("/customers")
    public ResponseEntity<?> customers(HttpSession session) {
        ResponseEntity<?> auth = requireAdmin(session);
        if (auth != null) return auth;
        return ResponseEntity.ok(adminService.getCustomers().stream().map(this::safeCustomer).toList());
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> accounts(HttpSession session) {
        ResponseEntity<?> auth = requireAdmin(session);
        if (auth != null) return auth;
        return ResponseEntity.ok(adminService.getAccounts().stream().map(this::safeAccount).toList());
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> transactions(HttpSession session) {
        ResponseEntity<?> auth = requireAdmin(session);
        if (auth != null) return auth;
        return ResponseEntity.ok(adminService.getTransactions());
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id, HttpSession session) {
        ResponseEntity<?> auth = requireAdmin(session);
        if (auth != null) return auth;
        try {
            adminService.deleteCustomer(id);
            return ResponseEntity.ok(Map.of("message", "Customer deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Unable to delete customer", "error", e.getMessage()));
        }
    }

    private Map<String, Object> safeCustomer(Customer customer) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", customer.getId());
        data.put("name", customer.getName());
        data.put("email", customer.getEmail());
        data.put("role", customer.getRole());
        return data;
    }

    private Map<String, Object> safeAccount(Account account) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", account.getId());
        data.put("accountNumber", account.getAccountNumber());
        data.put("balance", account.getBalance());
        return data;
    }

    private ResponseEntity<?> requireAdmin(HttpSession session) {
        Customer user = (Customer) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please login first"));
        if (!"ADMIN".equals(user.getRole())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Admin access required"));
        return null;
    }
}
