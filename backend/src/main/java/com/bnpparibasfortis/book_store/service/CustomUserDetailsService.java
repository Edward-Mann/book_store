package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.CustomerStatus;
import com.bnpparibasfortis.book_store.repository.CustomerRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * Loads user details from the Customer entity.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    /**
     * Constructs a new CustomUserDetailsService with the required repository dependency.
     *
     * @param customerRepository the repository for customer data access
     */
    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Loads user details by username for Spring Security authentication.
     * Converts Customer entity to UserDetails with appropriate authorities and account status.
     *
     * @param username the username to load
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword())
                .authorities(getAuthorities(customer))
                .accountExpired(false)
                .accountLocked(customer.getStatus() != CustomerStatus.ACTIVE)
                .credentialsExpired(false)
                .disabled(customer.getStatus() != CustomerStatus.ACTIVE)
                .build();
    }

    /**
     * Converts customer role to Spring Security authorities.
     *
     * @param customer the customer whose authorities to retrieve
     * @return collection of granted authorities based on customer role
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Customer customer) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + customer.getRole().name())
        );
    }
}
