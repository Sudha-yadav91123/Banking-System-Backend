package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.bank.entity.Transaction;

import jakarta.transaction.Transactional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findBySenderAccountOrReceiverAccount(String sender, String receiver);

	@Transactional
	@Modifying
	void deleteBySenderAccountOrReceiverAccount(String senderAccount, String receiverAccount);

}
