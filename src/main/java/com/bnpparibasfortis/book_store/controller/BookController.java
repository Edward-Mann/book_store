package com.bnpparibasfortis.book_store.controller;

import com.bnpparibasfortis.book_store.dto.ApiResponse;
import com.bnpparibasfortis.book_store.dto.BookDto;
import com.bnpparibasfortis.book_store.mapper.BookMapper;
import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for book management operations.
 * Provides endpoints for book catalog browsing and administrative book management.
 */
@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    /**
     * Retrieve all books in the catalog.
     * Public endpoint accessible to all users for browsing the book catalog.
     *
     * @return ResponseEntity with list of all books
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookDto>>> listBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDto> bookDtos = books.stream().map(bookMapper::toDto).toList();
        return ResponseEntity.ok(ApiResponse
                .success(bookDtos.isEmpty()?
                        "No Books available in this category": "Books retrieved successfully", bookDtos));
    }

    /**
     * Retrieve a specific book by its ID.
     * Public endpoint accessible to all users for viewing book details.
     *
     * @param id the book ID
     * @return ResponseEntity with the book details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> getBook(@PathVariable @Positive(message = "Book ID must be positive") Long id) {
        Book book = bookService.getBookById(id);
        BookDto bookDto = bookMapper.toDto(book);
        return ResponseEntity.ok(ApiResponse.success("Book retrieved successfully", bookDto));
    }

    /**
     * Create a new book (Admin only).
     * Administrative endpoint for adding new books to the catalog.
     *
     * @param bookDto the book data to create
     * @return ResponseEntity with the created book
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<BookDto>> createBook(@Valid @RequestBody BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        Book created = bookService.createBook(book, bookDto.getPublisherId(), bookDto.getAuthorIds());
        BookDto createdDto = bookMapper.toDto(created);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book created successfully", createdDto));
    }

    /**
     * Update an existing book (Admin only).
     * Administrative endpoint for modifying book information.
     *
     * @param id the book ID to update
     * @param bookDto the updated book data
     * @return ResponseEntity with the updated book
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(
            @PathVariable @Positive(message = "Book ID must be positive") Long id, 
            @Valid @RequestBody BookDto bookDto) {
        Book bookData = bookMapper.toEntity(bookDto);
        Book updatedBook = bookService.updateBook(id, bookData);
        BookDto updatedDto = bookMapper.toDto(updatedBook);
        return ResponseEntity.ok(ApiResponse.success("Book updated successfully", updatedDto));
    }

    /**
     * Delete a book by ID (Admin only).
     * Administrative endpoint for removing books from the catalog.
     *
     * @param id the book ID to delete
     * @return ResponseEntity with deletion confirmation
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDto>> deleteBook(@PathVariable @Positive(message = "Book ID must be positive") Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }
}
