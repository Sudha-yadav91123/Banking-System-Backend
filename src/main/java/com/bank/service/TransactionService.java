package com.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.repository.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private AccountService accountService;

	@Autowired
	private TransactionRepository txnRepo;

	// DEPOSIT
	@Transactional
	public boolean deposit(String accNo, double amount) {
		Account acc = accountService.getByAccountNumber(accNo);
		if (acc == null || amount <= 0)
			return false;
		acc.setBalance(acc.getBalance() + amount);
		accountService.save(acc);
		Transaction txn = new Transaction();
		txn.setReceiverAccount(accNo);
		txn.setType("DEPOSIT");
		txn.setAmount(amount);
		txnRepo.save(txn);
		return true;
	}

	// WITHDRAW
	@Transactional
	public boolean withdraw(String accNo, double amount) {
		Account acc = accountService.getByAccountNumber(accNo);
		if (acc == null || amount <= 0)
			return false;
		if (acc.getBalance() < amount)
			return false;
		acc.setBalance(acc.getBalance() - amount);
		accountService.save(acc);
		Transaction txn = new Transaction();
		txn.setSenderAccount(accNo);
		txn.setType("WITHDRAW");
		txn.setAmount(amount);
		txnRepo.save(txn);
		return true;
	}

	// FUND TRANSFER ⭐
	@Transactional
	public boolean transfer(String sender, String receiver, double amount) {

		Account senderAcc = accountService.getByAccountNumber(sender);

		Account receiverAcc = accountService.getByAccountNumber(receiver);

		if (senderAcc == null || receiverAcc == null)
			return false;

		if (amount <= 0)
			return false;

		if (senderAcc.getBalance() < amount)
			return false;

		senderAcc.setBalance(senderAcc.getBalance() - amount);
		receiverAcc.setBalance(receiverAcc.getBalance() + amount);

		accountService.save(senderAcc);
		accountService.save(receiverAcc);

		Transaction txn = new Transaction();
		txn.setSenderAccount(sender);
		txn.setReceiverAccount(receiver);
		txn.setType("TRANSFER");
		txn.setAmount(amount);

		txnRepo.save(txn);

		return true;
	}

	public List<Transaction> history(String accNo) {
		return txnRepo.findBySenderAccountOrReceiverAccount(accNo, accNo);
	}
}
