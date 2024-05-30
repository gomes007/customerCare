package com.pg.customercare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Customer;
import com.pg.customercare.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }



    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Customer not found with id " + id);
        }
        customerRepository.deleteById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id " + id));
    }

    public Customer updateCustomer(Customer customer) {
        if (!customerRepository.existsById(customer.getId())) {
            throw new NotFoundException("Customer not found with id " + customer.getId());
        }
        customer.setId(customer.getId());
        return customerRepository.save(customer);
    }

    
}
