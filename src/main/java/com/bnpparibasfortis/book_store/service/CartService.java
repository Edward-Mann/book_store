package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Cart;
import com.bnpparibasfortis.book_store.model.CartItem;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.repository.BookRepository;
import com.bnpparibasfortis.book_store.repository.CartItemRepository;
import com.bnpparibasfortis.book_store.repository.CartRepository;
import com.bnpparibasfortis.book_store.repository.CustomerRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class for managing shopping cart operations.
 * Handles cart retrieval, item addition, and item removal for authenticated users.
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       BookRepository bookRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Retrieves or creates a shopping cart for the specified user.
     *
     * @param username the username of the customer
     * @return the customer's cart
     * @throws IllegalArgumentException if customer is not found
     */
    @Transactional
    public Cart getCartForUser(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + username));

        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> createNewCart(customer));
    }

    /**
     * Adds an item to the user's shopping cart.
     * If the book is already in the cart, increases the quantity.
     *
     * @param username the username of the customer
     * @param bookId the ID of the book to add
     * @param quantity the quantity to add
     * @return the updated cart
     * @throws IllegalArgumentException if customer or book is not found, or if validation fails
     */
    @Transactional
    public Cart addItem(String username, 
                       @NotNull(message = "Book ID must be provided") Long bookId, 
                       @Min(value = 1, message = "Quantity must be at least 1") int quantity) {

        validateQuantity(quantity);

        Cart cart = getCartForUser(username);
        Book book = findBookById(bookId);

        Optional<CartItem> existingItem = findExistingCartItem(cart, bookId);

        if (existingItem.isPresent()) {
            updateExistingItem(existingItem.get(), quantity);
        } else {
            addNewItemToCart(cart, book, quantity);
        }

        return cartRepository.save(cart);
    }

    /**
     * Removes an item from the user's shopping cart.
     * Validates that the item belongs to the user's cart for security.
     *
     * @param username the username of the customer
     * @param itemId the ID of the cart item to remove
     * @return the updated cart
     * @throws IllegalArgumentException if customer or cart item is not found
     * @throws SecurityException if the item doesn't belong to the user's cart
     */
    @Transactional
    public Cart removeItem(String username, Long itemId) {
        Cart cart = getCartForUser(username);
        CartItem itemToRemove = findCartItemById(itemId);

        validateItemOwnership(cart, itemToRemove);

        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        return cart;
    }

    /**
     * Creates a new cart for the specified customer.
     *
     * @param customer the customer for whom to create the cart
     * @return the newly created cart
     */
    private Cart createNewCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setCreatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    /**
     * Finds a book by its ID.
     *
     * @param bookId the book ID
     * @return the book
     * @throws IllegalArgumentException if book is not found
     */
    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));
    }

    /**
     * Finds a cart item by its ID.
     *
     * @param itemId the cart item ID
     * @return the cart item
     * @throws IllegalArgumentException if cart item is not found
     */
    private CartItem findCartItemById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with ID: " + itemId));
    }

    /**
     * Finds an existing cart item for the specified book in the cart.
     *
     * @param cart the cart to search in
     * @param bookId the book ID to search for
     * @return optional containing the cart item if found
     */
    private Optional<CartItem> findExistingCartItem(Cart cart, Long bookId) {
        return cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();
    }

    /**
     * Updates the quantity of an existing cart item.
     *
     * @param existingItem the cart item to update
     * @param additionalQuantity the quantity to add
     */
    private void updateExistingItem(CartItem existingItem, int additionalQuantity) {
        existingItem.setQuantity(existingItem.getQuantity() + additionalQuantity);
        cartItemRepository.save(existingItem);
    }

    /**
     * Adds a new item to the cart.
     *
     * @param cart the cart to add the item to
     * @param book the book to add
     * @param quantity the quantity to add
     */
    private void addNewItemToCart(Cart cart, Book book, int quantity) {
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setBook(book);
        newItem.setQuantity(quantity);
        CartItem savedItem = cartItemRepository.save(newItem);
        cart.getItems().add(savedItem);
        cartRepository.save(cart);
    }

    /**
     * Validates that the quantity is positive.
     *
     * @param quantity the quantity to validate
     * @throws IllegalArgumentException if quantity is not positive
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    /**
     * Validates that the cart item belongs to the user's cart.
     *
     * @param cart the user's cart
     * @param item the cart item to validate
     * @throws SecurityException if the item doesn't belong to the user's cart
     */
    private void validateItemOwnership(Cart cart, CartItem item) {
        if (!Objects.equals(item.getCart().getId(), cart.getId())) {
            throw new SecurityException("User does not have permission to remove this item");
        }
    }
}
