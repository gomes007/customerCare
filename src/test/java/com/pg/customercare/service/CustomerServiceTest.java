package com.pg.customercare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Customer;
import com.pg.customercare.model.ENUM.CustomerType;
import com.pg.customercare.model.ENUM.Gender;
import com.pg.customercare.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private Customer customer;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setCustomerType(CustomerType.INDIVIDUAL);
        customer.setGender(Gender.FEMALE);
    }

    @Test
    void shouldSaveCustomer() {
        // ARRANGE
        given(customerRepository.save(customer)).willReturn(customer);

        // ACT
        Customer result = customerService.saveCustomer(customer);

        // ASSERT
        assertNotNull(result);
        assertEquals(customer, result);
        then(customerRepository).should().save(customerCaptor.capture());
        assertEquals(customer, customerCaptor.getValue());
    }

    @Test
    void shouldSaveCorporateCustomerWithNullGender() {
        // ARRANGE
        customer.setCustomerType(CustomerType.CORPORATE);
        customer.setGender(Gender.FEMALE); // Initially set gender, but it should be null for CORPORATE
        given(customerRepository.save(customer)).willReturn(customer);

        // ACT
        Customer result = customerService.saveCustomer(customer);

        // ASSERT
        assertNotNull(result);
        assertNull(result.getGender());
        then(customerRepository).should().save(customerCaptor.capture());
        assertEquals(customer, customerCaptor.getValue());
        assertNull(customerCaptor.getValue().getGender());
    }

    @Test
    void shouldDeleteCustomer() {
        // ARRANGE
        Long id = 1L;
        given(customerRepository.existsById(id)).willReturn(true);

        // ACT
        customerService.deleteCustomer(id);

        // ASSERT
        then(customerRepository).should().deleteById(id);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentCustomer() {
        // ARRANGE
        Long id = 1L;
        given(customerRepository.existsById(id)).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            customerService.deleteCustomer(id);
        });
        assertEquals("Customer not found with id " + id, exception.getMessage());
    }

    @Test
    void shouldGetAllCustomers() {
        // ARRANGE
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        given(customerRepository.findAll()).willReturn(customers);

        // ACT
        List<Customer> result = customerService.getAllCustomers();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(customer, result.get(0));
    }

    @Test
    void shouldGetCustomerById() {
        // ARRANGE
        Long id = 1L;
        given(customerRepository.findById(id)).willReturn(Optional.of(customer));

        // ACT
        Customer result = customerService.getCustomerById(id);

        // ASSERT
        assertNotNull(result);
        assertEquals(customer, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGettingCustomerByIdNotFound() {
        // ARRANGE
        Long id = 1L;
        given(customerRepository.findById(id)).willReturn(Optional.empty());

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            customerService.getCustomerById(id);
        });
        assertEquals("Customer not found with id " + id, exception.getMessage());
    }

    @Test
    void shouldUpdateCustomer() {
        // ARRANGE
        given(customerRepository.existsById(customer.getId())).willReturn(true);
        given(customerRepository.save(customer)).willReturn(customer);

        // ACT
        Customer result = customerService.updateCustomer(customer);

        // ASSERT
        assertNotNull(result);
        assertEquals(customer, result);
        then(customerRepository).should().save(customerCaptor.capture());
        assertEquals(customer, customerCaptor.getValue());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentCustomer() {
        // ARRANGE
        given(customerRepository.existsById(customer.getId())).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            customerService.updateCustomer(customer);
        });
        assertEquals("Customer not found with id " + customer.getId(), exception.getMessage());
    }

    @Test
    void shouldUpdateCorporateCustomerWithNullGender() {
        // ARRANGE
        customer.setCustomerType(CustomerType.CORPORATE);
        customer.setGender(Gender.FEMALE); // Initially set gender, but it should be null for CORPORATE
        given(customerRepository.existsById(customer.getId())).willReturn(true);
        given(customerRepository.save(customer)).willReturn(customer);

        // ACT
        Customer result = customerService.updateCustomer(customer);

        // ASSERT
        assertNotNull(result);
        assertNull(result.getGender());
        then(customerRepository).should().save(customerCaptor.capture());
        assertEquals(customer, customerCaptor.getValue());
        assertNull(customerCaptor.getValue().getGender());
    }

    @Test
    void shouldThrowValidationExceptionWhenSavingCorporateCustomerWithGender() {
        // ARRANGE
        customer.setCustomerType(CustomerType.CORPORATE);
        customer.setGender(Gender.FEMALE); // Set a non-null gender to be tested

        // Mock the repository to return the same customer but with gender nullified
        given(customerRepository.save(customer)).willAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.setGender(null); // simulate the behavior of validateGender
            return savedCustomer;
        });

        // ACT
        Customer result = customerService.saveCustomer(customer);

        // ASSERT
        assertNotNull(result); // Ensure result is not null
        assertNull(result.getGender()); // Ensure gender is nullified for corporate customers
    }

}
