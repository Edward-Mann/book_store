package com.bnpparibasfortis.book_store.controller;

import com.bnpparibasfortis.book_store.dto.ApiResponse;
import com.bnpparibasfortis.book_store.dto.OrderDto;
import com.bnpparibasfortis.book_store.mapper.OrderMapper;
import com.bnpparibasfortis.book_store.model.Order;
import com.bnpparibasfortis.book_store.service.CustomerService;
import com.bnpparibasfortis.book_store.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for order management operations.
 * Provides endpoints for order placement, order history, and administrative order management.
 * Most endpoints require USER role authentication, with admin endpoints requiring ADMIN role.
 */
@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('USER')")
@Validated
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final OrderMapper orderMapper;

    /**
     * Constructs a new OrderController with the required dependencies.
     *
     * @param orderService the service for order operations
     * @param customerService the service for customer operations
     * @param orderMapper the mapper for converting between Order entities and DTOs
     */
    public OrderController(OrderService orderService, CustomerService customerService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.orderMapper = orderMapper;
    }

    /**
     * Place a new order for the authenticated user.
     * Creates an order from the user's current cart contents.
     *
     * @param auth the authentication context
     * @return ResponseEntity with the created order
     */
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(Authentication auth) {
        Order order = orderService.placeOrder(getCustomerIdFromAuth(auth));
        OrderDto orderDto = orderMapper.toDto(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", orderDto));
    }

    /**
     * Retrieve all orders for the authenticated user.
     * Returns the user's order history.
     *
     * @param auth the authentication context
     * @return ResponseEntity with list of user's orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> listMyOrders(Authentication auth) {
        List<Order> orders = orderService.getOrdersForCustomer(getCustomerIdFromAuth(auth));
        List<OrderDto> orderDtos = orders.stream().map(orderMapper::toDto).toList();
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orderDtos));
    }

    /**
     * Retrieve all orders in the system (Admin only).
     * Administrative endpoint for viewing all customer orders.
     *
     * @return ResponseEntity with list of all orders
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<OrderDto>>> listAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDto> orderDtos = orders.stream().map(orderMapper::toDto).toList();
        return ResponseEntity.ok(ApiResponse.success("All orders retrieved successfully", orderDtos));
    }

    /**
     * Helper method to extract customer ID from authentication context.
     *
     * @param auth the authentication context
     * @return the customer ID
     */
    private Long getCustomerIdFromAuth(Authentication auth) {
        return customerService.getCustomerByUsername(auth.getName()).getId();
    }
}
