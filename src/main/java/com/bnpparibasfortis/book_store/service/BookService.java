package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Author;
import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Publisher;
import com.bnpparibasfortis.book_store.repository.AuthorRepository;
import com.bnpparibasfortis.book_store.repository.BookRepository;
import com.bnpparibasfortis.book_store.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing book operations.
 * Handles book catalog management, creation, updates, and deletion.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, PublisherRepository publisherRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
    }

    /**
     * Retrieves all books from the catalog.
     *
     * @return list of all books
     */
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param id the book ID
     * @return the book
     * @throws IllegalArgumentException if book is not found
     */
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));
    }

    /**
     * Creates a new book in the catalog.
     * Validates that the publisher and authors exist before saving.
     *
     * @param book the book to create
     * @param publisherId the publisher ID to assign to the book
     * @param authorIds the author IDs to assign to the book
     * @return the created book
     * @throws IllegalArgumentException if publisher or authors are not found
     */
    @Transactional
    public Book createBook(Book book, Long publisherId, List<Long> authorIds) {
        validateBookData(book);

        if (publisherId != null) {
            Publisher publisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Publisher with ID: %s not found. Please check the ID and try again.", publisherId)));
            book.setPublisher(publisher);
        }

        if (authorIds != null && !authorIds.isEmpty()) {
            Set<Author> validatedAuthors = authorIds.stream()
                    .map(authorId -> authorRepository.findById(authorId)
                            .orElseThrow(() -> new IllegalArgumentException(String.format("Author with ID: %s not found. Please check the ID and try again.", authorId))))
                    .collect(Collectors.toSet());
            book.setAuthors(validatedAuthors);
        }

        return bookRepository.save(book);
    }

    /**
     * Updates an existing book in the catalog.
     *
     * @param id the book ID to update
     * @param bookDetails the updated book data
     * @return the updated book
     * @throws IllegalArgumentException if book, publisher, or authors are not found
     */
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book existingBook = getBookById(id);

        if (bookDetails.getTitle() != null) {
            existingBook.setTitle(bookDetails.getTitle());
        }
        if (bookDetails.getDescription() != null) {
            existingBook.setDescription(bookDetails.getDescription());
        }
        if (bookDetails.getPrice() != null) {
            existingBook.setPrice(bookDetails.getPrice());
        }
        if (bookDetails.getPublishedDate() != null) {
            existingBook.setPublishedDate(bookDetails.getPublishedDate());
        }
        existingBook.setStockQuantity(bookDetails.getStockQuantity());

        if (bookDetails.getPublisher() != null && bookDetails.getPublisher().getId() != null) {
            Publisher publisher = publisherRepository.findById(bookDetails.getPublisher().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Publisher not found with ID: " + bookDetails.getPublisher().getId()));
            existingBook.setPublisher(publisher);
        }

        if (bookDetails.getAuthors() != null) {
            Set<Author> validatedAuthors = bookDetails.getAuthors().stream()
                    .map(author -> authorRepository.findById(author.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + author.getId())))
                    .collect(Collectors.toSet());
            existingBook.setAuthors(validatedAuthors);
        }

        return bookRepository.save(existingBook);
    }

    /**
     * Deletes a book from the catalog.
     *
     * @param id the book ID to delete
     * @throws IllegalArgumentException if book is not found
     */
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }

    /**
     * Validates basic book data.
     *
     * @param book the book to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateBookData(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        if (book.getPrice() == null || book.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Book price must be positive");
        }
        if (book.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }
}
