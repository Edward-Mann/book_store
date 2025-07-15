package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Cart;
import com.bnpparibasfortis.book_store.model.CartItem;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.model.Order;
import com.bnpparibasfortis.book_store.model.OrderItem;
import com.bnpparibasfortis.book_store.repository.BookRepository;
import com.bnpparibasfortis.book_store.repository.CartRepository;
import com.bnpparibasfortis.book_store.repository.CustomerRepository;
import com.bnpparibasfortis.book_store.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing order operations.
 * Handles order placement, order retrieval, and order management functionality.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;

    /**
     * Constructs a new OrderService with the required repository dependencies.
     *
     * @param orderRepository the repository for order data access
     * @param bookRepository the repository for book data access
     * @param customerRepository the repository for customer data access
     * @param cartRepository the repository for cart data access
     */
    public OrderService(OrderRepository orderRepository, BookRepository bookRepository, CustomerRepository customerRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * Places an order for the specified customer.
     * Converts the customer's cart items into order items, updates stock quantities,
     * calculates the total price, and clears the cart.
     *
     * @param customerId the ID of the customer placing the order
     * @return the created order
     * @throws IllegalArgumentException if customer is not found
     * @throws IllegalStateException if the cart is empty or stock is insufficient
     */
    @Transactional
    public Order placeOrder(Long customerId) {
        Customer customer = findCustomerById(customerId);
        Cart cart = validateAndGetCart(customer);

        Order order = createNewOrder(customer);
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            validateStockAvailability(cartItem);

            OrderItem orderItem = createOrderItem(cartItem, order);
            orderItems.add(orderItem);

            updateBookStock(cartItem);
            totalPrice = totalPrice.add(calculateItemTotal(orderItem));
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        clearCart(cart);
        return savedOrder;
    }

    /**
     * Retrieves all orders for a specific customer.
     *
     * @param customerId the customer ID
     * @return list of orders for the customer
     * @throws IllegalArgumentException if customer is not found
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersForCustomer(Long customerId) {
        Customer customer = findCustomerById(customerId);
        return orderRepository.findAll().stream()
                .filter(order -> order.getCustomer().getId().equals(customerId))
                .toList();
    }

    /**
     * Retrieves all orders in the system.
     *
     * @return list of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Finds a customer by ID.
     *
     * @param customerId the customer ID
     * @return the customer
     * @throws IllegalArgumentException if customer is not found
     */
    private Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
    }

    /**
     * Validates and retrieves the customer's cart.
     *
     * @param customer the customer
     * @return the customer's cart
     * @throws IllegalStateException if cart is empty
     */
    private Cart validateAndGetCart(Customer customer) {
        Cart cart = customer.getCart();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order: cart is empty");
        }
        return cart;
    }

    /**
     * Creates a new order for the customer.
     *
     * @param customer the customer
     * @return the new order
     */
    private Order createNewOrder(Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("NEW");
        return order;
    }

    /**
     * Validates that sufficient stock is available for the cart item.
     *
     * @param cartItem the cart item to validate
     * @throws IllegalStateException if insufficient stock
     */
    private void validateStockAvailability(CartItem cartItem) {
        Book book = cartItem.getBook();
        if (book.getStockQuantity() < cartItem.getQuantity()) {
            throw new IllegalStateException("Insufficient stock for book: " + book.getTitle() + 
                    ". Available: " + book.getStockQuantity() + ", Requested: " + cartItem.getQuantity());
        }
    }

    /**
     * Creates an order item from a cart item.
     *
     * @param cartItem the cart item
     * @param order the order
     * @return the created order item
     */
    private OrderItem createOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice()); // Capture current price
        orderItem.setOrder(order);
        return orderItem;
    }

    /**
     * Updates the book stock quantity after order placement.
     *
     * @param cartItem the cart item containing book and quantity information
     */
    private void updateBookStock(CartItem cartItem) {
        Book book = cartItem.getBook();
        book.setStockQuantity(book.getStockQuantity() - cartItem.getQuantity());
        bookRepository.save(book);
    }

    /**
     * Calculates the total price for an order item.
     *
     * @param orderItem the order item
     * @return the total price for the item
     */
    private BigDecimal calculateItemTotal(OrderItem orderItem) {
        return orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }

    /**
     * Clears all items from the cart after successful order placement.
     *
     * @param cart the cart to clear
     */
    private void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
