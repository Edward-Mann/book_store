package com.bnpparibasfortis.book_store.controller;

import com.bnpparibasfortis.book_store.dto.CustomerDto;
import com.bnpparibasfortis.book_store.dto.RegisterRequest;
import com.bnpparibasfortis.book_store.mapper.CustomerMapper;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.CustomerStatus;
import com.bnpparibasfortis.book_store.service.CustomerService;
import com.bnpparibasfortis.book_store.util.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Controller Tests")
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerController customerController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Customer testCustomer;
    private CustomerDto testCustomerDto;
    private RegisterRequest testRegisterRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

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

        testCustomerDto = new CustomerDto();
        testCustomerDto.setId(1L);
        testCustomerDto.setName("John Doe");
        testCustomerDto.setEmail("john@example.com");
        testCustomerDto.setUsername("johndoe");

        testRegisterRequest = new RegisterRequest();
        testRegisterRequest.setName("John Doe");
        testRegisterRequest.setEmail("john@example.com");
        testRegisterRequest.setUsername("johndoe");
        testRegisterRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        
        when(customerMapper.toEntity(any(RegisterRequest.class))).thenReturn(testCustomer);
        when(customerService.registerUser(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(testCustomerDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));

        verify(customerMapper).toEntity(any(RegisterRequest.class));
        verify(customerService).registerUser(any(Customer.class));
        verify(customerMapper).toDto(any(Customer.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid registration data")
    void shouldReturnBadRequestForInvalidRegistrationData() throws Exception {
        
        RegisterRequest invalidRequest = new RegisterRequest();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should upgrade user to admin successfully")
    void shouldUpgradeUserToAdminSuccessfully() throws Exception {
        
        Customer upgradedCustomer = new Customer();
        upgradedCustomer.setId(1L);
        upgradedCustomer.setRole(Customer.Role.ADMIN);

        when(customerService.upgradeToAdmin(1L)).thenReturn(upgradedCustomer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(testCustomerDto);

        mockMvc.perform(post("/api/upgrade-to-admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User upgraded to admin successfully"));

        verify(customerService).upgradeToAdmin(1L);
        verify(customerMapper).toDto(any(Customer.class));
    }

    @Test
    @DisplayName("Should create admin successfully")
    void shouldCreateAdminSuccessfully() throws Exception {
        
        Customer adminCustomer = new Customer();
        adminCustomer.setRole(Customer.Role.ADMIN);

        when(customerMapper.toEntity(any(RegisterRequest.class))).thenReturn(testCustomer);
        when(customerService.createAdmin(any(Customer.class))).thenReturn(adminCustomer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(testCustomerDto);

        mockMvc.perform(post("/api/create-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Admin created successfully"));

        verify(customerMapper).toEntity(any(RegisterRequest.class));
        verify(customerService).createAdmin(any(Customer.class));
        verify(customerMapper).toDto(any(Customer.class));
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomersSuccessfully() throws Exception {
        
        List<Customer> customers = Arrays.asList(testCustomer);

        when(customerService.getAllCustomers()).thenReturn(customers);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(testCustomerDto);

        mockMvc.perform(get("/api/admin/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customers retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());

        verify(customerService).getAllCustomers();
    }

    @Test
    @DisplayName("Should get customer by ID successfully")
    void shouldGetCustomerByIdSuccessfully() throws Exception {
        
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(testCustomerDto);

        mockMvc.perform(get("/api/admin/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(customerService).getCustomerById(1L);
        verify(customerMapper).toDto(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() throws Exception {
        
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/admin/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));

        verify(customerService).deleteCustomer(1L);
    }
}
