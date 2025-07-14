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

    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

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

    private Collection<? extends GrantedAuthority> getAuthorities(Customer customer) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + customer.getRole().name())
        );
    }
}
