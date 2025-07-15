package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.CustomerStatus;
import com.bnpparibasfortis.book_store.repository.CustomerRepository;
import com.bnpparibasfortis.book_store.util.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john@example.com");
        testCustomer.setUsername("johndoe");
        testCustomer.setPassword("password123");
        testCustomer.setPhone("1234567890");
        testCustomer.setAddress("123 Main St");
        testCustomer.setRegisteredDate(LocalDate.now());
        testCustomer.setRole(Customer.Role.USER);
        testCustomer.setStatus(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        
        String encodedPassword = "encodedPassword123";
        when(customerRepository.findByUsername(testCustomer.getUsername())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(testCustomer.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testCustomer.getPassword())).thenReturn(encodedPassword);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        
        Customer result = customerService.registerUser(testCustomer);

        
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Customer.Role.USER);
        assertThat(result.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(result.getRegisteredDate()).isNotNull();

        verify(customerRepository).findByUsername(testCustomer.getUsername());
        verify(customerRepository).findByEmail(testCustomer.getEmail());
        verify(passwordEncoder).encode("password123");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists during registration")
    void shouldThrowExceptionWhenUsernameAlreadyExistsDuringRegistration() {
        
        when(customerRepository.findByUsername(testCustomer.getUsername())).thenReturn(Optional.of(testCustomer));

        assertThatThrownBy(() -> customerService.registerUser(testCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");

        verify(customerRepository).findByUsername(testCustomer.getUsername());
        verify(customerRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists during registration")
    void shouldThrowExceptionWhenEmailAlreadyExistsDuringRegistration() {
        
        when(customerRepository.findByUsername(testCustomer.getUsername())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(testCustomer.getEmail())).thenReturn(Optional.of(testCustomer));

        assertThatThrownBy(() -> customerService.registerUser(testCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");

        verify(customerRepository).findByUsername(testCustomer.getUsername());
        verify(customerRepository).findByEmail(testCustomer.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should upgrade user to admin successfully")
    void shouldUpgradeUserToAdminSuccessfully() {
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        
        Customer result = customerService.upgradeToAdmin(1L);

        
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Customer.Role.ADMIN);

        verify(customerRepository).findById(1L);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @DisplayName("Should throw exception when upgrading non-existent user")
    void shouldThrowExceptionWhenUpgradingNonExistentUser() {
        
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.upgradeToAdmin(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AppConstants.CUSTOMER_NOT_FOUND);

        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should create admin successfully")
    void shouldCreateAdminSuccessfully() {
        
        String encodedPassword = "encodedPassword123";
        when(customerRepository.findByUsername(testCustomer.getUsername())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(testCustomer.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testCustomer.getPassword())).thenReturn(encodedPassword);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        
        Customer result = customerService.createAdmin(testCustomer);

        
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Customer.Role.ADMIN);
        assertThat(result.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(result.getRegisteredDate()).isNotNull();

        verify(customerRepository).findByUsername(testCustomer.getUsername());
        verify(customerRepository).findByEmail(testCustomer.getEmail());
        verify(passwordEncoder).encode("password123");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should get customer by ID successfully")
    void shouldGetCustomerByIdSuccessfully() {
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        
        Customer result = customerService.getCustomerById(1L);

        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");

        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when customer not found by ID")
    void shouldThrowExceptionWhenCustomerNotFoundById() {
        
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AppConstants.CUSTOMER_NOT_FOUND);

        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get customer by username successfully")
    void shouldGetCustomerByUsernameSuccessfully() {
        
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        Customer result = customerService.getCustomerByUsername("johndoe");

        
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");

        verify(customerRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should throw exception when customer not found by username")
    void shouldThrowExceptionWhenCustomerNotFoundByUsername() {
        
        when(customerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerByUsername("nonexistent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CUSTOMER_NOT_FOUND for username: nonexistent");

        verify(customerRepository).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should get customer with orders by username successfully")
    void shouldGetCustomerWithOrdersByUsernameSuccessfully() {
        
        when(customerRepository.findByUsernameWithOrders("johndoe")).thenReturn(Optional.of(testCustomer));

        
        Customer result = customerService.getCustomerWithOrdersByUsername("johndoe");

        
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");

        verify(customerRepository).findByUsernameWithOrders("johndoe");
    }

    @Test
    @DisplayName("Should find customer by username")
    void shouldFindCustomerByUsername() {
        
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        Optional<Customer> result = customerService.findByUsername("johndoe");

        
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("johndoe");

        verify(customerRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should find customer by email")
    void shouldFindCustomerByEmail() {
        
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testCustomer));

        
        Optional<Customer> result = customerService.findByEmail("john@example.com");

        
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");

        verify(customerRepository).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomersSuccessfully() {
        
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Jane Doe");
        List<Customer> customers = Arrays.asList(testCustomer, customer2);

        when(customerRepository.findAll()).thenReturn(customers);

        
        List<Customer> result = customerService.getAllCustomers();

        
        assertThat(result).hasSize(2).contains(testCustomer, customer2);

        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        
        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("John Updated");
        updatedCustomer.setEmail("john.updated@example.com");
        updatedCustomer.setPhone("9876543210");
        updatedCustomer.setAddress("456 New St");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        
        Customer result = customerService.updateCustomer(1L, updatedCustomer);

        
        assertThat(result).isNotNull();
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent customer")
    void shouldThrowExceptionWhenUpdatingNonExistentCustomer() {
        
        Customer updatedCustomer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(1L, updatedCustomer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AppConstants.CUSTOMER_NOT_FOUND);

        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(customerRepository.findCustomerById(1L)).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        
        customerService.deleteCustomer(1L);

        
        verify(customerRepository).existsById(1L);
        verify(customerRepository).findCustomerById(1L);
        verify(customerRepository).save(testCustomer);
        assertThat(testCustomer.getStatus()).isEqualTo(CustomerStatus.DELETED);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent customer")
    void shouldThrowExceptionWhenDeletingNonExistentCustomer() {
        
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomer(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(AppConstants.CUSTOMER_NOT_FOUND);

        verify(customerRepository).existsById(1L);
        verify(customerRepository, never()).deleteById(1L);
    }


    @ParameterizedTest
    @EnumSource(CustomerStatus.class)
    @DisplayName("Should handle all customer statuses")
    void shouldHandleAllCustomerStatuses(CustomerStatus status) {
        
        testCustomer.setStatus(status);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        
        Customer result = customerService.getCustomerById(1L);

        
        assertThat(result.getStatus()).isEqualTo(status);
        verify(customerRepository).findById(1L);
    }
}
