package com.bank.dto;

import com.bank.entity.Account;

public record AccountResponse(Long id, String accountNumber, double balance, CustomerResponse customer) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getBalance(),
            account.getCustomer() == null ? null : CustomerResponse.from(account.getCustomer())
        );
    }
}
