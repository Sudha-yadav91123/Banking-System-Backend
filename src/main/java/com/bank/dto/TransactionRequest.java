package com.bank.dto;

public record TransactionRequest(double amount, String receiverAccount) {
}
