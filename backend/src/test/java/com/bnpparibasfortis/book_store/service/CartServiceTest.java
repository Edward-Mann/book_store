package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Cart;
import com.bnpparibasfortis.book_store.model.CartItem;
import com.bnpparibasfortis.book_store.model.Customer;
import com.bnpparibasfortis.book_store.repository.BookRepository;
import com.bnpparibasfortis.book_store.repository.CartItemRepository;
import com.bnpparibasfortis.book_store.repository.CartRepository;
import com.bnpparibasfortis.book_store.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Tests")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CartService cartService;

    private Customer testCustomer;
    private Cart testCart;
    private Book testBook;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUsername("testuser");
        testCustomer.setName("Test User");
        testCustomer.setEmail("test@example.com");

        // Setup test cart
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setCustomer(testCustomer);
        testCart.setCreatedAt(LocalDateTime.now());
        testCart.setItems(new ArrayList<>());

        // Setup test book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("978-0123456789");
        testBook.setDescription("A test book description");
        testBook.setPrice(new BigDecimal("29.99"));
        testBook.setPublishedDate(LocalDate.of(2023, 1, 1));
        testBook.setStockQuantity(10);

        // Setup test cart item
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setCart(testCart);
        testCartItem.setBook(testBook);
        testCartItem.setQuantity(2);
    }

    @Test
    @DisplayName("Should get existing cart for user successfully")
    void shouldGetExistingCartForUserSuccessfully() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));

        // Act
        Cart result = cartService.getCartForUser("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCustomer()).isEqualTo(testCustomer);
        verify(customerRepository).findByUsername("testuser");
        verify(cartRepository).findByCustomer(testCustomer);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should create new cart when user has no existing cart")
    void shouldCreateNewCartWhenUserHasNoExistingCart() {
        // Arrange
        Cart newCart = new Cart();
        newCart.setId(2L);
        newCart.setCustomer(testCustomer);
        newCart.setCreatedAt(LocalDateTime.now());
        newCart.setItems(new ArrayList<>());

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        // Act
        Cart result = cartService.getCartForUser("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getCustomer()).isEqualTo(testCustomer);
        verify(customerRepository).findByUsername("testuser");
        verify(cartRepository).findByCustomer(testCustomer);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(customerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.getCartForUser("nonexistent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found: nonexistent");

        verify(customerRepository).findByUsername("nonexistent");
        verify(cartRepository, never()).findByCustomer(any());
    }

    @Test
    @DisplayName("Should add new item to cart successfully")
    void shouldAddNewItemToCartSuccessfully() {
        // Arrange
        CartItem savedItem = new CartItem();
        savedItem.setId(2L);
        savedItem.setCart(testCart);
        savedItem.setBook(testBook);
        savedItem.setQuantity(3);

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        Cart result = cartService.addItem("testuser", 1L, 3);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        verify(customerRepository).findByUsername("testuser");
        verify(bookRepository).findById(1L);
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository, org.mockito.Mockito.times(2)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should update existing item quantity when adding same book")
    void shouldUpdateExistingItemQuantityWhenAddingSameBook() {
        // Arrange
        testCart.getItems().add(testCartItem);

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        Cart result = cartService.addItem("testuser", 1L, 3);

        // Assert
        assertThat(result).isNotNull();
        assertThat(testCartItem.getQuantity()).isEqualTo(5); // 2 + 3
        verify(customerRepository).findByUsername("testuser");
        verify(bookRepository).findById(1L);
        verify(cartItemRepository).save(testCartItem);
        verify(cartRepository).save(testCart);
    }

    @Test
    @DisplayName("Should throw exception when adding item with invalid quantity")
    void shouldThrowExceptionWhenAddingItemWithInvalidQuantity() {
        // Act & Assert
        assertThatThrownBy(() -> cartService.addItem("testuser", 1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");

        assertThatThrownBy(() -> cartService.addItem("testuser", 1L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");

        verify(customerRepository, never()).findByUsername(anyString());
        verify(bookRepository, never()).findById(anyLong());
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when adding non-existent book")
    void shouldThrowExceptionWhenAddingNonExistentBook() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.addItem("testuser", 999L, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found with ID: 999");

        verify(bookRepository).findById(999L);
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should remove item from cart successfully")
    void shouldRemoveItemFromCartSuccessfully() {
        // Arrange
        testCart.getItems().add(testCartItem);

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));

        // Act
        Cart result = cartService.removeItem("testuser", 1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        verify(customerRepository).findByUsername("testuser");
        verify(cartItemRepository).findById(1L);
        verify(cartItemRepository).delete(testCartItem);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when removing non-existent item")
    void shouldThrowExceptionWhenRemovingNonExistentItem() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.removeItem("testuser", 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cart item not found with ID: 999");

        verify(cartItemRepository).findById(999L);
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw security exception when removing item from different user's cart")
    void shouldThrowSecurityExceptionWhenRemovingItemFromDifferentUsersCart() {
        // Arrange
        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);
        otherCustomer.setUsername("otheruser");

        Cart otherCart = new Cart();
        otherCart.setId(2L);
        otherCart.setCustomer(otherCustomer);

        CartItem otherCartItem = new CartItem();
        otherCartItem.setId(2L);
        otherCartItem.setCart(otherCart);
        otherCartItem.setBook(testBook);
        otherCartItem.setQuantity(1);

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(2L)).thenReturn(Optional.of(otherCartItem));

        // Act & Assert
        assertThatThrownBy(() -> cartService.removeItem("testuser", 2L))
                .isInstanceOf(SecurityException.class)
                .hasMessage("User does not have permission to remove this item");

        verify(cartItemRepository).findById(2L);
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should handle empty cart items list")
    void shouldHandleEmptyCartItemsList() {
        // Arrange
        testCart.setItems(new ArrayList<>());

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));

        // Act
        Cart result = cartService.getCartForUser("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        verify(customerRepository).findByUsername("testuser");
        verify(cartRepository).findByCustomer(testCustomer);
    }

    @Test
    @DisplayName("Should handle cart with multiple items")
    void shouldHandleCartWithMultipleItems() {
        // Arrange
        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Second Book");
        secondBook.setPrice(new BigDecimal("19.99"));

        CartItem secondCartItem = new CartItem();
        secondCartItem.setId(2L);
        secondCartItem.setCart(testCart);
        secondCartItem.setBook(secondBook);
        secondCartItem.setQuantity(1);

        List<CartItem> items = new ArrayList<>();
        items.add(testCartItem);
        items.add(secondCartItem);
        testCart.setItems(items);

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(cartRepository.findByCustomer(testCustomer)).thenReturn(Optional.of(testCart));

        // Act
        Cart result = cartService.getCartForUser("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems()).contains(testCartItem, secondCartItem);
    }

    @Test
    @DisplayName("Should handle null username gracefully")
    void shouldHandleNullUsernameGracefully() {
        // Act & Assert
        assertThatThrownBy(() -> cartService.getCartForUser(null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(customerRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Should handle empty username gracefully")
    void shouldHandleEmptyUsernameGracefully() {
        // Arrange
        when(customerRepository.findByUsername("")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.getCartForUser(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer not found: ");

        verify(customerRepository).findByUsername("");
    }
}
