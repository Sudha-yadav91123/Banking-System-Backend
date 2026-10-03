package com.bank.dto;

import com.bank.entity.Customer;

public record CustomerResponse(Long id, String name, String email, String role) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
            customer.getId(), customer.getName(), customer.getEmail(), customer.getRole()
        );
    }
}
