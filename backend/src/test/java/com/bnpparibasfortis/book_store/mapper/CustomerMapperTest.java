package com.bnpparibasfortis.book_store.mapper;

import com.bnpparibasfortis.book_store.dto.CustomerDto;
import com.bnpparibasfortis.book_store.dto.RegisterRequest;
import com.bnpparibasfortis.book_store.model.Cart;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.CustomerStatus;
import com.bnpparibasfortis.book_store.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Customer Mapper Tests")
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    private Customer testCustomer;
    private CustomerDto testCustomerDto;
    private RegisterRequest testRegisterRequest;

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
        testCustomer.setRegisteredDate(LocalDate.of(2023, 1, 15));
        testCustomer.setRole(Customer.Role.USER);
        testCustomer.setStatus(CustomerStatus.ACTIVE);

        Cart testCart = new Cart();
        testCart.setId(10L);
        testCustomer.setCart(testCart);

        Order order1 = new Order();
        order1.setId(100L);
        Order order2 = new Order();
        order2.setId(200L);
        testCustomer.setOrders(Arrays.asList(order1, order2));

        testCustomerDto = new CustomerDto();
        testCustomerDto.setId(1L);
        testCustomerDto.setName("John Doe");
        testCustomerDto.setEmail("john@example.com");
        testCustomerDto.setUsername("johndoe");
        testCustomerDto.setPhone("1234567890");
        testCustomerDto.setAddress("123 Main St");
        testCustomerDto.setRegisteredDate(LocalDate.of(2023, 1, 15));
        testCustomerDto.setRole(Customer.Role.USER);
        testCustomerDto.setStatus("ACTIVE");

        testRegisterRequest = new RegisterRequest();
        testRegisterRequest.setName("Jane Smith");
        testRegisterRequest.setEmail("jane@example.com");
        testRegisterRequest.setUsername("janesmith");
        testRegisterRequest.setPassword("password456");
        testRegisterRequest.setPhone("0987654321");
        testRegisterRequest.setAddress("456 Oak Ave");
    }

    @Test
    @DisplayName("Should convert Customer to CustomerDto successfully")
    void shouldConvertCustomerToCustomerDtoSuccessfully() {

        CustomerDto result = customerMapper.toDto(testCustomer);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testCustomer.getId());
        assertThat(result.getName()).isEqualTo(testCustomer.getName());
        assertThat(result.getEmail()).isEqualTo(testCustomer.getEmail());
        assertThat(result.getUsername()).isEqualTo(testCustomer.getUsername());
        assertThat(result.getPhone()).isEqualTo(testCustomer.getPhone());
        assertThat(result.getAddress()).isEqualTo(testCustomer.getAddress());
        assertThat(result.getRegisteredDate()).isEqualTo(testCustomer.getRegisteredDate());
        assertThat(result.getRole()).isEqualTo(testCustomer.getRole());
        assertThat(result.getStatus()).isEqualTo(testCustomer.getStatus().name());
        assertThat(result.getCartId()).isEqualTo(10L);
        assertThat(result.getOrderIds()).containsExactly(100L, 200L);
    }

    @Test
    @DisplayName("Should convert Customer to CustomerDto with null cart")
    void shouldConvertCustomerToCustomerDtoWithNullCart() {

        testCustomer.setCart(null);


        CustomerDto result = customerMapper.toDto(testCustomer);


        assertThat(result).isNotNull();
        assertThat(result.getCartId()).isNull();
        assertThat(result.getOrderIds()).containsExactly(100L, 200L);
    }

    @Test
    @DisplayName("Should convert Customer to CustomerDto with null orders")
    void shouldConvertCustomerToCustomerDtoWithNullOrders() {

        testCustomer.setOrders(null);


        CustomerDto result = customerMapper.toDto(testCustomer);


        assertThat(result).isNotNull();
        assertThat(result.getCartId()).isEqualTo(10L);
        assertThat(result.getOrderIds()).isEmpty();
    }

    @Test
    @DisplayName("Should convert Customer to CustomerDto with empty orders")
    void shouldConvertCustomerToCustomerDtoWithEmptyOrders() {

        testCustomer.setOrders(new ArrayList<>());


        CustomerDto result = customerMapper.toDto(testCustomer);


        assertThat(result).isNotNull();
        assertThat(result.getOrderIds()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null Customer in toDto")
    void shouldHandleNullCustomerInToDto() {

        CustomerDto result = customerMapper.toDto(null);


        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert CustomerDto to Customer successfully")
    void shouldConvertCustomerDtoToCustomerSuccessfully() {

        Customer result = customerMapper.toEntity(testCustomerDto);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testCustomerDto.getId());
        assertThat(result.getName()).isEqualTo(testCustomerDto.getName());
        assertThat(result.getEmail()).isEqualTo(testCustomerDto.getEmail());
        assertThat(result.getUsername()).isEqualTo(testCustomerDto.getUsername());
        assertThat(result.getPhone()).isEqualTo(testCustomerDto.getPhone());
        assertThat(result.getAddress()).isEqualTo(testCustomerDto.getAddress());
        assertThat(result.getRegisteredDate()).isEqualTo(testCustomerDto.getRegisteredDate());
        assertThat(result.getRole()).isEqualTo(testCustomerDto.getRole());
        assertThat(result.getStatus()).isEqualTo(CustomerStatus.valueOf(testCustomerDto.getStatus()));
        assertThat(result.getOrders()).isNotNull().isEmpty();
        assertThat(result.getCart()).isNull();
    }

    @Test
    @DisplayName("Should handle null CustomerDto in toEntity")
    void shouldHandleNullCustomerDtoInToEntity() {

        Customer result = customerMapper.toEntity((CustomerDto) null);


        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert CustomerDto to Customer for update successfully")
    void shouldConvertCustomerDtoToCustomerForUpdateSuccessfully() {

        Customer result = customerMapper.toEntityForUpdate(testCustomerDto);


        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testCustomerDto.getName());
        assertThat(result.getEmail()).isEqualTo(testCustomerDto.getEmail());
        assertThat(result.getPhone()).isEqualTo(testCustomerDto.getPhone());
        assertThat(result.getAddress()).isEqualTo(testCustomerDto.getAddress());

        assertThat(result.getId()).isNull();
        assertThat(result.getUsername()).isNull();
        assertThat(result.getPassword()).isNull();
        assertThat(result.getRegisteredDate()).isNull();
        assertThat(result.getRole()).isEqualTo(Customer.Role.USER);
        assertThat(result.getStatus()).isNull();
        assertThat(result.getOrders()).isNotNull().isEmpty();
        assertThat(result.getCart()).isNull();
    }

    @Test
    @DisplayName("Should handle null CustomerDto in toEntityForUpdate")
    void shouldHandleNullCustomerDtoInToEntityForUpdate() {

        Customer result = customerMapper.toEntityForUpdate(null);


        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert RegisterRequest to Customer successfully")
    void shouldConvertRegisterRequestToCustomerSuccessfully() {

        Customer result = customerMapper.toEntity(testRegisterRequest);


        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testRegisterRequest.getName());
        assertThat(result.getEmail()).isEqualTo(testRegisterRequest.getEmail());
        assertThat(result.getUsername()).isEqualTo(testRegisterRequest.getUsername());
        assertThat(result.getPassword()).isEqualTo(testRegisterRequest.getPassword());
        assertThat(result.getPhone()).isEqualTo(testRegisterRequest.getPhone());
        assertThat(result.getAddress()).isEqualTo(testRegisterRequest.getAddress());

        assertThat(result.getId()).isNull();
        assertThat(result.getRegisteredDate()).isNull();
        assertThat(result.getRole()).isEqualTo(Customer.Role.USER);
        assertThat(result.getOrders()).isNotNull().isEmpty();
        assertThat(result.getCart()).isNull();
    }

    @Test
    @DisplayName("Should handle null RegisterRequest in toEntity")
    void shouldHandleNullRegisterRequestInToEntity() {

        Customer result = customerMapper.toEntity((RegisterRequest) null);


        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert orders to IDs correctly")
    void shouldConvertOrdersToIdsCorrectly() {

        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        Order order3 = new Order();
        order3.setId(3L);
        List<Order> orders = Arrays.asList(order1, order2, order3);


        List<Long> result = customerMapper.ordersToIds(orders);


        assertThat(result).isNotNull().containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("Should handle null orders in ordersToIds")
    void shouldHandleNullOrdersInOrdersToIds() {

        List<Long> result = customerMapper.ordersToIds(null);


        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should handle empty orders in ordersToIds")
    void shouldHandleEmptyOrdersInOrdersToIds() {

        List<Order> emptyOrders = new ArrayList<>();


        List<Long> result = customerMapper.ordersToIds(emptyOrders);


        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should handle orders with null IDs in ordersToIds")
    void shouldHandleOrdersWithNullIdsInOrdersToIds() {

        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(null);
        Order order3 = new Order();
        order3.setId(3L);
        List<Order> orders = Arrays.asList(order1, order2, order3);


        List<Long> result = customerMapper.ordersToIds(orders);


        assertThat(result).isNotNull().containsExactly(1L, null, 3L);
    }

    @Test
    @DisplayName("Should preserve all customer roles in mapping")
    void shouldPreserveAllCustomerRolesInMapping() {
        testCustomer.setRole(Customer.Role.USER);
        CustomerDto userResult = customerMapper.toDto(testCustomer);
        assertThat(userResult.getRole()).isEqualTo(Customer.Role.USER);

        testCustomer.setRole(Customer.Role.ADMIN);
        CustomerDto adminResult = customerMapper.toDto(testCustomer);
        assertThat(adminResult.getRole()).isEqualTo(Customer.Role.ADMIN);
    }

    @Test
    @DisplayName("Should preserve all customer statuses in mapping")
    void shouldPreserveAllCustomerStatusesInMapping() {
        testCustomer.setStatus(CustomerStatus.ACTIVE);
        CustomerDto activeResult = customerMapper.toDto(testCustomer);
        assertThat(activeResult.getStatus()).isEqualTo("ACTIVE");

        testCustomer.setStatus(CustomerStatus.INACTIVE);
        CustomerDto inactiveResult = customerMapper.toDto(testCustomer);
        assertThat(inactiveResult.getStatus()).isEqualTo("INACTIVE");

        testCustomer.setStatus(CustomerStatus.SUSPENDED);
        CustomerDto suspendedResult = customerMapper.toDto(testCustomer);
        assertThat(suspendedResult.getStatus()).isEqualTo("SUSPENDED");
    }
}
