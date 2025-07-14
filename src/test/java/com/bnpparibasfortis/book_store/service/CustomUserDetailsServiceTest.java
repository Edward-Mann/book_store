package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.CustomerStatus;
import com.bnpparibasfortis.book_store.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Custom User Details Service Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john@example.com");
        testCustomer.setUsername("johndoe");
        testCustomer.setPassword("encodedPassword123");
        testCustomer.setPhone("1234567890");
        testCustomer.setAddress("123 Main St");
        testCustomer.setRegisteredDate(LocalDate.now());
        testCustomer.setRole(Customer.Role.USER);
        testCustomer.setStatus(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should load user by username successfully for active user")
    void shouldLoadUserByUsernameSuccessfullyForActiveUser() {
        
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        UserDetails result = customUserDetailsService.loadUserByUsername("johndoe");

        
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");
        assertThat(result.getPassword()).isEqualTo("encodedPassword123");
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
        assertThat(result.isEnabled()).isTrue();

        verify(customerRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should load admin user by username successfully")
    void shouldLoadAdminUserByUsernameSuccessfully() {
        
        testCustomer.setRole(Customer.Role.ADMIN);
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        UserDetails result = customUserDetailsService.loadUserByUsername("johndoe");

        
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");

        verify(customerRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should load user with locked account for inactive status")
    void shouldLoadUserWithLockedAccountForInactiveStatus() {
        
        testCustomer.setStatus(CustomerStatus.INACTIVE);
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        UserDetails result = customUserDetailsService.loadUserByUsername("johndoe");

        
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");
        assertThat(result.isAccountNonLocked()).isFalse();
        assertThat(result.isEnabled()).isFalse();

        verify(customerRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should load user with locked account for suspended status")
    void shouldLoadUserWithLockedAccountForSuspendedStatus() {
        
        testCustomer.setStatus(CustomerStatus.SUSPENDED);
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        UserDetails result = customUserDetailsService.loadUserByUsername("johndoe");

        
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");
        assertThat(result.isAccountNonLocked()).isFalse();
        assertThat(result.isEnabled()).isFalse();

        verify(customerRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        
        when(customerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: nonexistent");

        verify(customerRepository).findByUsername("nonexistent");
    }

    @ParameterizedTest
    @EnumSource(CustomerStatus.class)
    @DisplayName("Should handle all customer statuses correctly")
    void shouldHandleAllCustomerStatusesCorrectly(CustomerStatus status) {
        
        testCustomer.setStatus(status);
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        UserDetails result = customUserDetailsService.loadUserByUsername("johndoe");

        
        assertThat(result).isNotNull();
        boolean expectedAccountNonLocked = (status == CustomerStatus.ACTIVE);
        boolean expectedEnabled = (status == CustomerStatus.ACTIVE);

        assertThat(result.isAccountNonLocked()).isEqualTo(expectedAccountNonLocked);
        assertThat(result.isEnabled()).isEqualTo(expectedEnabled);

        verify(customerRepository).findByUsername("johndoe");
    }

    @ParameterizedTest
    @EnumSource(Customer.Role.class)
    @DisplayName("Should handle all customer roles correctly")
    void shouldHandleAllCustomerRolesCorrectly(Customer.Role role) {
        
        testCustomer.setRole(role);
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        UserDetails result = customUserDetailsService.loadUserByUsername("johndoe");

        
        assertThat(result).isNotNull();
        assertThat(result.getAuthorities()).hasSize(1);

        String expectedRole = "ROLE_" + role.name();
        assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo(expectedRole);

        verify(customerRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should set account properties correctly for active user")
    void shouldSetAccountPropertiesCorrectlyForActiveUser() {
        
        when(customerRepository.findByUsername("johndoe")).thenReturn(Optional.of(testCustomer));

        
        UserDetails result = customUserDetailsService.loadUserByUsername("johndoe");

        
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        assertThat(result.isEnabled()).isTrue();

        verify(customerRepository).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Should return correct username and password")
    void shouldReturnCorrectUsernameAndPassword() {
        
        testCustomer.setUsername("testuser");
        testCustomer.setPassword("testpassword");
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));

        
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("testpassword");

        verify(customerRepository).findByUsername("testuser");
    }
}
