import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import Login from './components/Login';
import Cart from './components/Cart';

interface Author {
  id: number;
  name: string;
}

interface Publisher {
  id: number;
  name: string;
}

interface Book {
  id: number;
  title: string;
  description: string;
  price: number;
  stockQuantity: number;
  authors?: Author[];
  publisher?: Publisher;
}

interface ApiResponse {
  data: Book[];
}

interface User {
  id: number;
  username: string;
  email: string;
}

interface CartItem {
  book: Book;
  quantity: number;
}

function App(): JSX.Element {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [showLogin, setShowLogin] = useState<boolean>(false);
  const [showCart, setShowCart] = useState<boolean>(false);
  const [cartItems, setCartItems] = useState<CartItem[]>([]);

  useEffect(() => {
    fetchBooks();
  }, []);

  useEffect(() => {
    const checkSession = async () => {
      try {
        const response = await axios.get<{data: User}>('/api/customers/profile', {
          withCredentials: true
        });
        if (response.data && response.data.data) {
          setUser(response.data.data);
        }
      } catch (e) {
        setUser(null);
      }
    };
    checkSession();
  }, []);

  const fetchBooks = async (): Promise<void> => {
    try {
      setLoading(true);
      const response = await axios.get<ApiResponse>('/api/books');
      if (response.data && response.data.data) {
        setBooks(response.data.data);
      }
      setError(null);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Unknown error occurred';
      setError('Failed to fetch books: ' + errorMessage);
      console.error('Error fetching books:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = (loggedInUser: User): void => {
    setUser(loggedInUser);
    setShowLogin(false);
  };

  const handleLogout = (): void => {
    setUser(null);
    setCartItems([]);
    setShowCart(false);
  };

  const addToCart = (book: Book): void => {
    setCartItems(prevItems => {
      const existingItem = prevItems.find(item => item.book.id === book.id);
      if (existingItem) {
        return prevItems.map(item =>
          item.book.id === book.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      } else {
        return [...prevItems, { book, quantity: 1 }];
      }
    });
  };

  const updateCartQuantity = (bookId: number, quantity: number): void => {
    setCartItems(prevItems =>
      prevItems.map(item =>
        item.book.id === bookId ? { ...item, quantity } : item
      )
    );
  };

  const removeFromCart = (bookId: number): void => {
    setCartItems(prevItems => prevItems.filter(item => item.book.id !== bookId));
  };

  const handleCheckout = (): void => {
    setCartItems([]);
  };

  const getCartItemCount = (): number => {
    return cartItems.reduce((total, item) => total + item.quantity, 0);
  };

  return (
    <div className="App">
      <header className="App-header">
        <div className="header-content">
          <div className="header-left">
            <h1>Book Store</h1>
            <p>Welcome to our online book store!</p>
          </div>
          <div className="header-right">
            {user ? (
              <div className="user-section">
                <span>Welcome, {user.username}!</span>
                <button 
                  className="cart-button" 
                  onClick={() => setShowCart(true)}
                >
                  Cart ({getCartItemCount()})
                </button>
                <button className="logout-button" onClick={handleLogout}>
                  Logout
                </button>
              </div>
            ) : (
              <button className="login-button" onClick={() => setShowLogin(true)}>
                Login
              </button>
            )}
          </div>
        </div>
      </header>

      <main className="App-main">
        <section className="books-section">
          <h2>Available Books</h2>

          {loading && <p>Loading books...</p>}

          {error && (
            <div className="error-message">
              <p>{error}</p>
              <button onClick={fetchBooks}>Retry</button>
            </div>
          )}

          {!loading && !error && books.length === 0 && (
            <p>No books available at the moment.</p>
          )}

          {!loading && !error && books.length > 0 && (
            <div className="books-grid">
              {books.map((book: Book) => (
                <div key={book.id} className="book-card">
                  <h3>{book.title}</h3>
                  <p className="book-description">
                    {user 
                      ? book.description 
                      : `${book.description.substring(0, 100)}${book.description.length > 100 ? '...' : ''}`
                    }
                  </p>
                  {!user && (
                    <div className="sample-section">
                      <p className="sample-text">
                        <strong>Sample:</strong> "This is a sample excerpt from the book. 
                        Login to read the full content and access all features..."
                      </p>
                    </div>
                  )}
                  <p className="book-price">Price: €{book.price}</p>
                  {user && <p className="book-stock">Stock: {book.stockQuantity}</p>}
                  {book.authors && book.authors.length > 0 && (
                    <p className="book-authors">
                      Authors: {book.authors.map((author: Author) => author.name).join(', ')}
                    </p>
                  )}
                  {user && book.publisher && (
                    <p className="book-publisher">Publisher: {book.publisher.name}</p>
                  )}
                  {user ? (
                    <div className="book-actions">
                      <button 
                        className="add-to-cart-button"
                        onClick={() => addToCart(book)}
                        disabled={book.stockQuantity === 0}
                      >
                        {book.stockQuantity === 0 ? 'Out of Stock' : 'Add to Cart'}
                      </button>
                    </div>
                  ) : (
                    <div className="login-prompt">
                      <p><small>Login to purchase this book and access full details</small></p>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </section>
      </main>

      <footer className="App-footer">
        <p>Book Store Application - Frontend communicating with Spring Boot Backend</p>
      </footer>

      {showLogin && (
        <Login 
          onLogin={handleLogin} 
          onClose={() => setShowLogin(false)} 
        />
      )}

      {showCart && user && (
        <Cart
          cartItems={cartItems}
          onUpdateQuantity={updateCartQuantity}
          onRemoveItem={removeFromCart}
          onCheckout={handleCheckout}
          onClose={() => setShowCart(false)}
        />
      )}
    </div>
  );
}

export default App;