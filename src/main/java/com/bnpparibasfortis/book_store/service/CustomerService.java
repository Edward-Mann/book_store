package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.CustomerStatus;
import com.bnpparibasfortis.book_store.repository.CustomerRepository;
import com.bnpparibasfortis.book_store.util.AppConstants;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing customer operations.
 * Handles customer registration, authentication, profile management, and administrative functions.
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, 
                          PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a customer with their orders by username.
     *
     * @param username the username to search for
     * @return the customer with orders
     * @throws IllegalArgumentException if customer is not found
     */
    @Transactional
    public Customer getCustomerWithOrdersByUsername(String username) {
        return customerRepository.findByUsernameWithOrders(username)
                .orElseThrow(() -> new IllegalArgumentException("CUSTOMER_NOT_FOUND for username: " + username));
    }
    /**
     * Register a new user with encoded password.
     *
     * @param customer the customer to register
     * @return the registered customer
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public Customer registerUser(Customer customer) {

        validateUsernameAndEmail(customer);

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        customer.setRole(Customer.Role.USER);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setRegisteredDate(LocalDate.now());

        return customerRepository.save(customer);
    }

    /**
     * Upgrades an existing customer to admin role.
     *
     * @param userId the ID of the customer to upgrade
     * @return the upgraded customer
     * @throws IllegalArgumentException if customer is not found
     */
    @Transactional
    public Customer upgradeToAdmin(Long userId) {
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(AppConstants.CUSTOMER_NOT_FOUND));
        customer.setRole(Customer.Role.ADMIN);
        return customerRepository.save(customer);
    }

    /**
     * Creates a new admin customer with encoded password.
     *
     * @param customer the customer to create as admin
     * @return the created admin customer
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public Customer createAdmin(Customer customer) {
        validateUsernameAndEmail(customer);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole(Customer.Role.ADMIN);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setRegisteredDate(LocalDate.now());
        return customerRepository.save(customer);
    }

    private void validateUsernameAndEmail(Customer customer) {
        if (customerRepository.findByUsername(customer.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + customer.getUsername());
        }

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
        }
    }

    /**
     * Retrieves a customer by ID.
     *
     * @param id the customer ID
     * @return the customer
     * @throws IllegalArgumentException if customer is not found
     */
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(AppConstants.CUSTOMER_NOT_FOUND));
    }

    /**
     * Retrieves a customer by username.
     *
     * @param username the username to search for
     * @return the customer
     * @throws IllegalArgumentException if customer is not found
     */
    @Transactional(readOnly = true)
    public Customer getCustomerByUsername(String username) {
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("CUSTOMER_NOT_FOUND for username: " + username));
    }

    /**
     * Finds a customer by username (returns Optional).
     *
     * @param username the username to search for
     * @return Optional containing the customer if found
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    /**
     * Finds a customer by email (returns Optional).
     *
     * @param email the email to search for
     * @return Optional containing the customer if found
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Retrieves all customers.
     *
     * @return list of all customers
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Updates an existing customer's information.
     *
     * @param id the customer ID to update
     * @param updatedCustomer the customer data to update
     * @return the updated customer
     * @throws IllegalArgumentException if customer is not found or email already exists
     */
    @Transactional
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(AppConstants.CUSTOMER_NOT_FOUND));

        if (updatedCustomer.getName() != null) {
            existingCustomer.setName(updatedCustomer.getName());
        }

        if (updatedCustomer.getEmail() != null && !updatedCustomer.getEmail().equals(existingCustomer.getEmail())) {
            Optional<Customer> customerWithEmail = customerRepository.findByEmail(updatedCustomer.getEmail());
            if (customerWithEmail.isPresent() && !customerWithEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email already exists: " + updatedCustomer.getEmail());
            }
            existingCustomer.setEmail(updatedCustomer.getEmail());
        }

        if (updatedCustomer.getPhone() != null) {
            existingCustomer.setPhone(updatedCustomer.getPhone());
        }

        if (updatedCustomer.getAddress() != null) {
            existingCustomer.setAddress(updatedCustomer.getAddress());
        }

        return customerRepository.save(existingCustomer);
    }

    /**
     * Deletes a customer by ID.
     *
     * @param id the customer ID to delete
     * @throws IllegalArgumentException if a customer is not found
     */
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException(AppConstants.CUSTOMER_NOT_FOUND);
        }
        Customer customer = customerRepository.findCustomerById(id);
        customer.setStatus(CustomerStatus.DELETED);
        customerRepository.save(customer);
    }
}
