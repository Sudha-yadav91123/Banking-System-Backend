package com.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.entity.Customer;
import com.bank.entity.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;

@Service
public class AdminService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private TransactionRepository txnRepo;

	// GET ALL CUSTOMERS
	public List<Customer> getCustomers() {
		return customerRepo.findAll();
	}

	// GET ALL ACCOUNTS
	public List<Account> getAccounts() {
		return accountRepo.findAll();
	}

	// GET ALL TRANSACTIONS
	public List<Transaction> getTransactions() {
		return txnRepo.findAll();
	}

	// DELETE CUSTOMER
	public void deleteCustomer(Long customerId) {

		Account acc = accountRepo.findByCustomerId(customerId);

		if (acc != null) {

			txnRepo.deleteBySenderAccountOrReceiverAccount(
					acc.getAccountNumber(),
					acc.getAccountNumber());

			accountRepo.delete(acc);
		}

		customerRepo.deleteById(customerId);
	}
}

