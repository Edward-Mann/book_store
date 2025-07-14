package com.bnpparibasfortis.book_store.service;

import com.bnpparibasfortis.book_store.model.Author;
import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Publisher;
import com.bnpparibasfortis.book_store.repository.AuthorRepository;
import com.bnpparibasfortis.book_store.repository.BookRepository;
import com.bnpparibasfortis.book_store.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private Publisher testPublisher;
    private Author testAuthor1;
    private Author testAuthor2;

    @BeforeEach
    void setUp() {
        // Setup test book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("978-0123456789");
        testBook.setDescription("A test book description");
        testBook.setPrice(new BigDecimal("29.99"));
        testBook.setPublishedDate(LocalDate.of(2023, 1, 1));
        testBook.setStockQuantity(10);

        // Setup test publisher
        testPublisher = new Publisher();
        testPublisher.setId(1L);
        testPublisher.setName("Test Publisher");

        // Setup test authors
        testAuthor1 = new Author();
        testAuthor1.setId(1L);
        testAuthor1.setFirstName("Test");
        testAuthor1.setLastName("Author 1");

        testAuthor2 = new Author();
        testAuthor2.setId(2L);
        testAuthor2.setFirstName("Test");
        testAuthor2.setLastName("Author 2");

        Set<Author> authors = new HashSet<>();
        authors.add(testAuthor1);
        authors.add(testAuthor2);
        testBook.setAuthors(authors);
        testBook.setPublisher(testPublisher);
    }

    @Test
    @DisplayName("Should get all books successfully")
    void shouldGetAllBooksSuccessfully() {
        
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Another Book");
        List<Book> books = Arrays.asList(testBook, book2);

        when(bookRepository.findAll()).thenReturn(books);

        
        List<Book> result = bookService.getAllBooks();

        
        assertThat(result).hasSize(2).contains(testBook, book2);
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Should get book by ID successfully")
    void shouldGetBookByIdSuccessfully() {
        
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        
        Book result = bookService.getBookById(1L);

        
        assertThat(result).isEqualTo(testBook);
        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when book not found by ID")
    void shouldThrowExceptionWhenBookNotFoundById() {
        
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> bookService.getBookById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book not found with ID: 1");

        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Should create book successfully with publisher and authors")
    void shouldCreateBookSuccessfullyWithPublisherAndAuthors() {
        
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setPrice(new BigDecimal("19.99"));
        newBook.setStockQuantity(5);

        List<Long> authorIds = Arrays.asList(1L, 2L);

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(testPublisher));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor1));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(testAuthor2));
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        
        Book result = bookService.createBook(newBook, 1L, authorIds);

        
        assertThat(result).isNotNull();
        assertThat(newBook.getPublisher()).isEqualTo(testPublisher);
        assertThat(newBook.getAuthors()).hasSize(2).contains(testAuthor1, testAuthor2);
        verify(publisherRepository).findById(1L);
        verify(authorRepository).findById(1L);
        verify(authorRepository).findById(2L);
        verify(bookRepository).save(newBook);
    }

    @Test
    @DisplayName("Should create book successfully without publisher and authors")
    void shouldCreateBookSuccessfullyWithoutPublisherAndAuthors() {
        
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setPrice(new BigDecimal("19.99"));
        newBook.setStockQuantity(5);

        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        
        Book result = bookService.createBook(newBook, null, null);

        
        assertThat(result).isNotNull();
        verify(bookRepository).save(newBook);
        verify(publisherRepository, never()).findById(anyLong());
        verify(authorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when creating book with invalid publisher")
    void shouldThrowExceptionWhenCreatingBookWithInvalidPublisher() {
        
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setPrice(new BigDecimal("19.99"));
        newBook.setStockQuantity(5);

        when(publisherRepository.findById(1L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> bookService.createBook(newBook, 1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Publisher with ID: 1 not found");

        verify(publisherRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when creating book with invalid author")
    void shouldThrowExceptionWhenCreatingBookWithInvalidAuthor() {
        
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setPrice(new BigDecimal("19.99"));
        newBook.setStockQuantity(5);

        List<Long> authorIds = Arrays.asList(1L, 999L);

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(testPublisher));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor1));
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> bookService.createBook(newBook, 1L, authorIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Author with ID: 999 not found");

        verify(publisherRepository).findById(1L);
        verify(authorRepository).findById(1L);
        verify(authorRepository).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when creating book with null title")
    void shouldThrowExceptionWhenCreatingBookWithNullTitle() {
        
        Book newBook = new Book();
        newBook.setTitle(null);
        newBook.setPrice(new BigDecimal("19.99"));
        newBook.setStockQuantity(5);

        
        assertThatThrownBy(() -> bookService.createBook(newBook, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book title is required");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when creating book with empty title")
    void shouldThrowExceptionWhenCreatingBookWithEmptyTitle() {
        Book newBook = new Book();
        newBook.setTitle("   ");
        newBook.setPrice(new BigDecimal("19.99"));
        newBook.setStockQuantity(5);

        
        assertThatThrownBy(() -> bookService.createBook(newBook, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book title is required");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when creating book with null price")
    void shouldThrowExceptionWhenCreatingBookWithNullPrice() {
        
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setPrice(null);
        newBook.setStockQuantity(5);

        
        assertThatThrownBy(() -> bookService.createBook(newBook, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book price must be positive");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when creating book with negative price")
    void shouldThrowExceptionWhenCreatingBookWithNegativePrice() {
        
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setPrice(new BigDecimal("-10.00"));
        newBook.setStockQuantity(5);

        
        assertThatThrownBy(() -> bookService.createBook(newBook, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book price must be positive");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when creating book with negative stock quantity")
    void shouldThrowExceptionWhenCreatingBookWithNegativeStockQuantity() {
        
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setPrice(new BigDecimal("19.99"));
        newBook.setStockQuantity(-1);

        
        assertThatThrownBy(() -> bookService.createBook(newBook, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock quantity cannot be negative");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should update book successfully")
    void shouldUpdateBookSuccessfully() {
        
        Book updatedBookDetails = new Book();
        updatedBookDetails.setTitle("Updated Title");
        updatedBookDetails.setDescription("Updated Description");
        updatedBookDetails.setPrice(new BigDecimal("39.99"));
        updatedBookDetails.setPublishedDate(LocalDate.of(2024, 1, 1));
        updatedBookDetails.setStockQuantity(20);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        
        Book result = bookService.updateBook(1L, updatedBookDetails);

        
        assertThat(result).isNotNull();
        assertThat(testBook.getTitle()).isEqualTo("Updated Title");
        assertThat(testBook.getDescription()).isEqualTo("Updated Description");
        assertThat(testBook.getPrice()).isEqualTo(new BigDecimal("39.99"));
        assertThat(testBook.getPublishedDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(testBook.getStockQuantity()).isEqualTo(20);
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should update book with new publisher successfully")
    void shouldUpdateBookWithNewPublisherSuccessfully() {
        
        Publisher newPublisher = new Publisher();
        newPublisher.setId(2L);
        newPublisher.setName("New Publisher");

        Book updatedBookDetails = new Book();
        updatedBookDetails.setPublisher(newPublisher);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(newPublisher));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        
        Book result = bookService.updateBook(1L, updatedBookDetails);

        
        assertThat(result).isNotNull();
        assertThat(testBook.getPublisher()).isEqualTo(newPublisher);
        verify(bookRepository).findById(1L);
        verify(publisherRepository).findById(2L);
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should update book with new authors successfully")
    void shouldUpdateBookWithNewAuthorsSuccessfully() {
        
        Author newAuthor = new Author();
        newAuthor.setId(3L);
        newAuthor.setFirstName("New");
        newAuthor.setLastName("Author");

        Set<Author> newAuthors = new HashSet<>();
        newAuthors.add(newAuthor);

        Book updatedBookDetails = new Book();
        updatedBookDetails.setAuthors(newAuthors);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.findById(3L)).thenReturn(Optional.of(newAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        
        Book result = bookService.updateBook(1L, updatedBookDetails);

        
        assertThat(result).isNotNull();
        assertThat(testBook.getAuthors()).hasSize(1).contains(newAuthor);
        verify(bookRepository).findById(1L);
        verify(authorRepository).findById(3L);
        verify(bookRepository).save(testBook);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent book")
    void shouldThrowExceptionWhenUpdatingNonExistentBook() {
        
        Book updatedBookDetails = new Book();
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> bookService.updateBook(1L, updatedBookDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book not found with ID: 1");

        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when updating book with invalid publisher")
    void shouldThrowExceptionWhenUpdatingBookWithInvalidPublisher() {
        
        Publisher invalidPublisher = new Publisher();
        invalidPublisher.setId(999L);

        Book updatedBookDetails = new Book();
        updatedBookDetails.setPublisher(invalidPublisher);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(publisherRepository.findById(999L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> bookService.updateBook(1L, updatedBookDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Publisher not found with ID: 999");

        verify(bookRepository).findById(1L);
        verify(publisherRepository).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when updating book with invalid author")
    void shouldThrowExceptionWhenUpdatingBookWithInvalidAuthor() {
        
        Author invalidAuthor = new Author();
        invalidAuthor.setId(999L);

        Set<Author> invalidAuthors = new HashSet<>();
        invalidAuthors.add(invalidAuthor);

        Book updatedBookDetails = new Book();
        updatedBookDetails.setAuthors(invalidAuthors);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        
        assertThatThrownBy(() -> bookService.updateBook(1L, updatedBookDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Author not found with ID: 999");

        verify(bookRepository).findById(1L);
        verify(authorRepository).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should delete book successfully")
    void shouldDeleteBookSuccessfully() {
        
        when(bookRepository.existsById(1L)).thenReturn(true);

        
        bookService.deleteBook(1L);

        
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent book")
    void shouldThrowExceptionWhenDeletingNonExistentBook() {
        
        when(bookRepository.existsById(1L)).thenReturn(false);

        
        assertThatThrownBy(() -> bookService.deleteBook(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book not found with ID: 1");

        verify(bookRepository).existsById(1L);
        verify(bookRepository, never()).deleteById(anyLong());
    }
}
