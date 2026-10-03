package com.bank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.dto.AccountResponse;
import com.bank.dto.CustomerResponse;
import com.bank.entity.Customer;
import com.bank.service.AdminService;

import jakarta.servlet.http.HttpSession;

<<<<<<< HEAD
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
=======
>>>>>>> d3cb49920535d2a3d6a61a26ea04cdcfbeb645af
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        ResponseEntity<?> auth = requireAdmin(session);
        if (auth != null) return auth;

        return ResponseEntity.ok(Map.of(
                "message", "Admin dashboard",
                "role", "ADMIN"
        ));
    }

    @GetMapping("/customers")
    public ResponseEntity<?> customers(HttpSession session) {
        ResponseEntity<?> auth = requireAdmin(session);
        if (auth != null) return auth;

        List<CustomerResponse> list = adminService.getCustomers()
                .stream()
                .map(CustomerResponse::from)
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> accounts(HttpSession session) {
        ResponseEntity<?> auth = requireAdmin(session);
        if (auth != null) return auth;

        List<AccountResponse> list = adminService.getAccounts()
                .stream()
                .map(AccountResponse::from)
                .toList();

        return ResponseEntity.ok(list);
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
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Unable to delete customer", "error", e.getMessage()));
        }
    }

    private ResponseEntity<?> requireAdmin(HttpSession session) {
        Customer user = (Customer) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first"));
        }

        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Admin access required"));
        }

        return null;
    }
}
