package com.bnpparibasfortis.book_store.controller;

import com.bnpparibasfortis.book_store.dto.ApiResponse;
import com.bnpparibasfortis.book_store.dto.CartDto;
import com.bnpparibasfortis.book_store.dto.CartItemDto;
import com.bnpparibasfortis.book_store.mapper.CartMapper;
import com.bnpparibasfortis.book_store.model.Cart;
import com.bnpparibasfortis.book_store.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for shopping cart management.
 * Provides endpoints for cart operations including viewing, adding items, and removing items.
 * All endpoints require USER role authentication.
 */
@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('USER')")
@Validated
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    /**
     * Constructs a new CartController with the required dependencies.
     *
     * @param cartService the service for cart operations
     * @param cartMapper the mapper for converting between Cart entities and DTOs
     */
    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    /**
     * Retrieve the current user's shopping cart.
     * Returns the cart with all items for the authenticated user.
     *
     * @param auth the authentication context
     * @return ResponseEntity with the user's cart
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(Authentication auth) {
        Cart cart = cartService.getCartForUser(auth.getName());
        CartDto cartDto = cartMapper.toDto(cart);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cartDto));
    }

    /**
     * Add an item to the current user's shopping cart.
     * Creates a new cart item or updates quantity if the book is already in the cart.
     *
     * @param itemDto the cart item data containing book ID and quantity
     * @param auth the authentication context
     * @return ResponseEntity with the updated cart
     */
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(@Valid @RequestBody CartItemDto itemDto, Authentication auth) {
        Cart cart = cartService.addItem(auth.getName(), itemDto.getBookId(), itemDto.getQuantity());
        CartDto cartDto = cartMapper.toDto(cart);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item added to cart successfully", cartDto));
    }

    /**
     * Remove an item from the current user's shopping cart.
     * Completely removes the specified item from the cart.
     *
     * @param itemId the cart item ID to remove
     * @param auth the authentication context
     * @return ResponseEntity with the updated cart
     */
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> removeFromCart(
            @PathVariable("itemId") @Positive(message = "Item ID must be positive") Long itemId,
            Authentication auth) {
        Cart cart = cartService.removeItem(auth.getName(), itemId);
        CartDto cartDto = cartMapper.toDto(cart);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", cartDto));
    }
}
