package com.bank.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.repository.AccountRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository repo;

	// create account automatically
	public Account createAccount(Customer customer) {

		Account acc = new Account();
		acc.setCustomer(customer);
		acc.setBalance(0);
		acc.setAccountNumber(generateAccountNumber());

		return repo.save(acc);
	}

	public Account getAccount(Long customerId) {
		return repo.findByCustomerId(customerId);
	}

	public Account getByAccountNumber(String accNo) {
		return repo.findByAccountNumber(accNo).orElse(null);
	}

	private String generateAccountNumber() {
		return "ACC" + (100000 + new Random().nextInt(900000));
	}

	public void save(Account account) {
		repo.save(account);
	}
}
