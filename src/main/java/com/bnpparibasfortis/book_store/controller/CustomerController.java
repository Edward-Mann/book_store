package com.bnpparibasfortis.book_store.controller;

import com.bnpparibasfortis.book_store.dto.ApiResponse;
import com.bnpparibasfortis.book_store.dto.CustomerDto;
import com.bnpparibasfortis.book_store.dto.RegisterRequest;
import com.bnpparibasfortis.book_store.mapper.CustomerMapper;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for customer authentication and management.
 */
@RestController
@RequestMapping("/api")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    /**
     * Register a new user.
     *
     * @param registerRequest the registration request
     * @return ResponseEntity with the registration result
     */
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<CustomerDto>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Customer customer = customerMapper.toEntity(registerRequest);
        Customer registeredCustomer = customerService.registerUser(customer);
        CustomerDto responseDto = customerMapper.toDto(registeredCustomer);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", responseDto));
    }

    /**
     * Upgrade an existing user to an admin role (Admin only).
     *
     * @param userId the ID of the user to upgrade
     * @return ResponseEntity with an upgrade result
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upgrade-to-admin/{userId}")
    public ResponseEntity<ApiResponse<CustomerDto>> upgradeToAdmin(@PathVariable @Positive Long userId) {
        Customer upgraded = customerService.upgradeToAdmin(userId);
        CustomerDto responseDto = customerMapper.toDto(upgraded);
        return ResponseEntity.ok(ApiResponse.success("User upgraded to admin successfully", responseDto));
    }

    /**
     * Create a new admin user (Admin only).
     *
     * @param registerRequest the registration request for the new admin
     * @return ResponseEntity with a creation result
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-admin")
    public ResponseEntity<ApiResponse<CustomerDto>> createAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        Customer customer = customerMapper.toEntity(registerRequest);
        Customer created = customerService.createAdmin(customer);
        CustomerDto responseDto = customerMapper.toDto(created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin created successfully", responseDto));
    }

    /**
     * Get current user profile.
     *
     * @return ResponseEntity with user profile
     */
    @GetMapping("/customers/profile")
    public ResponseEntity<ApiResponse<CustomerDto>> getProfile() {
        Authentication authentication = requireAuthenticatedUser();
        String username = authentication.getName();
        Customer customer = customerService.getCustomerByUsername(username);
        CustomerDto responseDto = customerMapper.toDto(customer);

        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", responseDto));
    }

    /**
     * Update current user profile.
     *
     * @param customerDto the updated customer data
     * @return ResponseEntity with an update result
     */
    @PutMapping("/customers/profile")
    public ResponseEntity<ApiResponse<CustomerDto>> updateProfile(@Valid @RequestBody CustomerDto customerDto) {
        Authentication authentication = requireAuthenticatedUser();
        Customer currentCustomer = customerService.getCustomerByUsername(authentication.getName());
        Customer updatedCustomer = customerMapper.toEntityForUpdate(customerDto);

        Customer customer = customerService.updateCustomer(currentCustomer.getId(), updatedCustomer);
        CustomerDto responseCustomerDto = customerMapper.toDto(customer);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", responseCustomerDto));
    }

    /**
     * Get all customers (Admin only).
     *
     * @return ResponseEntity with a list of customers
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/customers")
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        List<CustomerDto> customerDtos = customers.stream()
                .map(customerMapper::toDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customerDtos));
    }

    /**
     * Get customer by ID (Admin only).
     *
     * @param id the customer ID
     * @return ResponseEntity with the customer data
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/customers/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable @Positive(message = "Customer ID must be positive") Long id) {
        Customer customer = customerService.getCustomerById(id);
        CustomerDto customerDto = customerMapper.toDto(customer);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customerDto));
    }

    /**
     * Delete customer by ID (Admin only).
     *
     * @param id the customer ID
     * @return ResponseEntity with a deletion result
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/customers/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> deleteCustomer(@PathVariable @Positive(message = "Customer ID must be positive") Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }

    private Authentication requireAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated") {};
        }
        return authentication;
    }
}
