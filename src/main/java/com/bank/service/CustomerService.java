package com.bank.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository repo;

	public boolean existsByEmail(String email) {
		return repo.findByEmail(email).isPresent();
	}

	public Customer register(Customer customer) {
		if(customer.getEmail().equalsIgnoreCase("admin@gmail.com")) {
			customer.setRole("ADMIN");
		}else {
			customer.setRole("USER");
		}
		return repo.save(customer);
	}

	public Customer login(String email, String password) {

		Optional<Customer> opt = repo.findByEmail(email);

		if (opt.isPresent() && opt.get().getPassword().equals(password)) {
			return opt.get();
		}
		return null;
	}
}
