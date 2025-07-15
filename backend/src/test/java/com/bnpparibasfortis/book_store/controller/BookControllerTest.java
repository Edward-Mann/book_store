package com.bnpparibasfortis.book_store.controller;

import com.bnpparibasfortis.book_store.dto.BookDto;
import com.bnpparibasfortis.book_store.exception.BookNotFoundException;
import com.bnpparibasfortis.book_store.mapper.BookMapper;
import com.bnpparibasfortis.book_store.model.Author;
import com.bnpparibasfortis.book_store.model.Book;
import com.bnpparibasfortis.book_store.model.Publisher;
import com.bnpparibasfortis.book_store.service.BookService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Controller Tests")
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookController bookController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Book testBook;
    private BookDto testBookDto;
    private Publisher testPublisher;
    private Author testAuthor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Setup test publisher
        testPublisher = new Publisher();
        testPublisher.setId(1L);
        testPublisher.setName("Test Publisher");

        // Setup test author
        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("Test");
        testAuthor.setLastName("Author");

        Set<Author> authors = new HashSet<>();
        authors.add(testAuthor);

        // Setup test book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("978-0123456789");
        testBook.setDescription("A test book description");
        testBook.setPrice(new BigDecimal("29.99"));
        testBook.setPublishedDate(LocalDate.of(2023, 1, 1));
        testBook.setStockQuantity(10);
        testBook.setPublisher(testPublisher);
        testBook.setAuthors(authors);

        // Setup test book DTO
        testBookDto = new BookDto();
        testBookDto.setId(1L);
        testBookDto.setTitle("Test Book");
        testBookDto.setIsbn("978-0123456789");
        testBookDto.setDescription("A test book description");
        testBookDto.setPrice(new BigDecimal("29.99"));
        testBookDto.setPublishedDate("2023-01-01");
        testBookDto.setStockQuantity(10);
        testBookDto.setPublisherName("Test Publisher");
        testBookDto.setPublisherId(1L);
        testBookDto.setAuthorNames(Arrays.asList("Test Author"));
        testBookDto.setAuthorIds(Arrays.asList(1L));
    }

    @Test
    @DisplayName("Should list all books successfully")
    void shouldListAllBooksSuccessfully() throws Exception {

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Another Book");

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle("Another Book");

        List<Book> books = Arrays.asList(testBook, book2);
        List<BookDto> bookDtos = Arrays.asList(testBookDto, bookDto2);

        when(bookService.getAllBooks()).thenReturn(books);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);


        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Books retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Test Book"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].title").value("Another Book"));

        verify(bookService).getAllBooks();
        verify(bookMapper).toDto(testBook);
        verify(bookMapper).toDto(book2);
    }

    @Test
    @DisplayName("Should return empty message when no books available")
    void shouldReturnEmptyMessageWhenNoBooksAvailable() throws Exception {

        List<Book> emptyBooks = Arrays.asList();

        when(bookService.getAllBooks()).thenReturn(emptyBooks);


        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("No Books available in this category"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(bookService).getAllBooks();
    }

    @Test
    @DisplayName("Should get book by ID successfully")
    void shouldGetBookByIdSuccessfully() throws Exception {

        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(bookMapper.toDto(testBook)).thenReturn(testBookDto);


        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Book"))
                .andExpect(jsonPath("$.data.isbn").value("978-0123456789"))
                .andExpect(jsonPath("$.data.price").value(29.99))
                .andExpect(jsonPath("$.data.stockQuantity").value(10));

        verify(bookService).getBookById(1L);
        verify(bookMapper).toDto(testBook);
    }

    @Test
    @DisplayName("Should create book successfully")
    void shouldCreateBookSuccessfully() throws Exception {

        BookDto newBookDto = new BookDto();
        newBookDto.setTitle("New Book");
        newBookDto.setIsbn("978-9876543210");
        newBookDto.setDescription("A new book description");
        newBookDto.setPrice(new BigDecimal("19.99"));
        newBookDto.setPublishedDate("2024-01-01");
        newBookDto.setStockQuantity(5);
        newBookDto.setPublisherId(1L);
        newBookDto.setAuthorIds(Arrays.asList(1L));

        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setIsbn("978-9876543210");
        newBook.setDescription("A new book description");
        newBook.setPrice(new BigDecimal("19.99"));
        newBook.setStockQuantity(5);

        Book createdBook = new Book();
        createdBook.setId(2L);
        createdBook.setTitle("New Book");
        createdBook.setIsbn("978-9876543210");
        createdBook.setDescription("A new book description");
        createdBook.setPrice(new BigDecimal("19.99"));
        createdBook.setStockQuantity(5);

        BookDto createdBookDto = new BookDto();
        createdBookDto.setId(2L);
        createdBookDto.setTitle("New Book");
        createdBookDto.setIsbn("978-9876543210");
        createdBookDto.setDescription("A new book description");
        createdBookDto.setPrice(new BigDecimal("19.99"));
        createdBookDto.setStockQuantity(5);

        when(bookMapper.toEntity(any(BookDto.class))).thenReturn(newBook);
        when(bookService.createBook(any(Book.class), anyLong(), anyList())).thenReturn(createdBook);
        when(bookMapper.toDto(any(Book.class))).thenReturn(createdBookDto);


        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book created successfully"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.title").value("New Book"))
                .andExpect(jsonPath("$.data.isbn").value("978-9876543210"))
                .andExpect(jsonPath("$.data.price").value(19.99));

        verify(bookMapper).toEntity(any(BookDto.class));
        verify(bookService).createBook(any(Book.class), anyLong(), anyList());
        verify(bookMapper).toDto(any(Book.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid book data")
    void shouldReturnBadRequestForInvalidBookData() throws Exception {

        BookDto invalidBookDto = new BookDto();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBookDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update book successfully")
    void shouldUpdateBookSuccessfully() throws Exception {

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setTitle("Updated Book");
        updatedBookDto.setDescription("Updated description");
        updatedBookDto.setPrice(new BigDecimal("39.99"));
        updatedBookDto.setStockQuantity(20);

        Book updatedBookData = new Book();
        updatedBookData.setTitle("Updated Book");
        updatedBookData.setDescription("Updated description");
        updatedBookData.setPrice(new BigDecimal("39.99"));
        updatedBookData.setStockQuantity(20);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Book");
        updatedBook.setDescription("Updated description");
        updatedBook.setPrice(new BigDecimal("39.99"));
        updatedBook.setStockQuantity(20);

        BookDto updatedBookResponseDto = new BookDto();
        updatedBookResponseDto.setId(1L);
        updatedBookResponseDto.setTitle("Updated Book");
        updatedBookResponseDto.setDescription("Updated description");
        updatedBookResponseDto.setPrice(new BigDecimal("39.99"));
        updatedBookResponseDto.setStockQuantity(20);

        when(bookMapper.toEntity(any(BookDto.class))).thenReturn(updatedBookData);
        when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(updatedBook);
        when(bookMapper.toDto(any(Book.class))).thenReturn(updatedBookResponseDto);


        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Updated Book"))
                .andExpect(jsonPath("$.data.description").value("Updated description"))
                .andExpect(jsonPath("$.data.price").value(39.99))
                .andExpect(jsonPath("$.data.stockQuantity").value(20));

        verify(bookMapper).toEntity(any(BookDto.class));
        verify(bookService).updateBook(1L, updatedBookData);
        verify(bookMapper).toDto(any(Book.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid book ID in update")
    void shouldReturnBadRequestForInvalidBookIdInUpdate() throws Exception {

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setTitle("Updated Book");


        mockMvc.perform(put("/api/books/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete book successfully")
    void shouldDeleteBookSuccessfully() throws Exception {

        doNothing().when(bookService).deleteBook(1L);


        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(bookService).deleteBook(1L);
    }

    @Test
    @DisplayName("Should handle book not found exception")
    void shouldHandleBookNotFoundExceptionInGetBook() throws Exception {

        when(bookService.getBookById(999L)).thenThrow(new IllegalArgumentException("Book not found with ID: 999"));


        mockMvc.perform(get("/api/books/999"))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Book not found with ID: 999"));

        verify(bookService).getBookById(999L);
    }

    @Test
    @DisplayName("Should handle book not found exception in update")
    void shouldHandleBookNotFoundExceptionInUpdate() throws Exception {

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setTitle("Updated Book");
        updatedBookDto.setPrice(new BigDecimal("29.99"));
        updatedBookDto.setStockQuantity(10);

        Book updatedBookData = new Book();
        updatedBookData.setTitle("Updated Book");

        when(bookMapper.toEntity(any(BookDto.class))).thenReturn(updatedBookData);
        when(bookService.updateBook(anyLong(), any(Book.class)))
                .thenThrow(new BookNotFoundException(999L));

        mockMvc.perform(put("/api/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Book not found with ID: 999"));

        verify(bookMapper).toEntity(any(BookDto.class));
        verify(bookService).updateBook(999L, updatedBookData);
    }

    @Test
    @DisplayName("Should handle book not found exception in delete")
    void shouldHandleBookNotFoundExceptionInDelete() throws Exception {

        doThrow(new IllegalArgumentException("Book not found with ID: 999")).when(bookService).deleteBook(999L);


        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Book not found with ID: 999"));

        verify(bookService).deleteBook(999L);
    }
}
