package com.bank.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.entity.Customer;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        try {
            if (customer.getName() == null || customer.getName().trim().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("message", "Name is required"));
            if (customer.getEmail() == null || customer.getEmail().trim().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            if (customer.getPassword() == null || customer.getPassword().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));

            customer.setName(customer.getName().trim());
            customer.setEmail(customer.getEmail().trim().toLowerCase());

            if (customerService.existsByEmail(customer.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Email is already registered"));
            }

            Customer saved = customerService.register(customer);
            accountService.createAccount(saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("message", "Registration successful. Please login."));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Registration failed",
                    "error", e.getMessage() == null ? "Unable to complete registration" : e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null)
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));

        Customer user = customerService.login(email.trim().toLowerCase(), password);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));

        session.setAttribute("user", user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("user", safeCustomer(user));
        response.put("redirect", "ADMIN".equals(user.getRole()) ? "/admin/dashboard" : "/dashboard");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {
        Customer user = getUser(session);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not logged in"));
        return ResponseEntity.ok(safeCustomer(user));
    }

    private Map<String, Object> safeCustomer(Customer customer) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", customer.getId());
        data.put("name", customer.getName());
        data.put("email", customer.getEmail());
        data.put("role", customer.getRole());
        return data;
    }

    private Customer getUser(HttpSession session) {
        return (Customer) session.getAttribute("user");
    }
}
