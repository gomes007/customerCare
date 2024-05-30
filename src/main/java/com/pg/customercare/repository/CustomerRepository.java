package com.pg.customercare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pg.customercare.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{
    
}
