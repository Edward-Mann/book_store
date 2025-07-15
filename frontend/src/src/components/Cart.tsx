import React, { useState } from 'react';

interface Book {
  id: number;
  title: string;
  description: string;
  price: number;
  stockQuantity: number;
}

interface CartItem {
  book: Book;
  quantity: number;
}

interface CartProps {
  cartItems: CartItem[];
  onUpdateQuantity: (bookId: number, quantity: number) => void;
  onRemoveItem: (bookId: number) => void;
  onCheckout: () => void;
  onClose: () => void;
}

const Cart: React.FC<CartProps> = ({ 
  cartItems, 
  onUpdateQuantity, 
  onRemoveItem, 
  onCheckout, 
  onClose 
}) => {
  const [isProcessing, setIsProcessing] = useState<boolean>(false);

  const calculateTotal = (): number => {
    return cartItems.reduce((total, item) => total + (item.book.price * item.quantity), 0);
  };

  const handleQuantityChange = (bookId: number, newQuantity: number): void => {
    if (newQuantity > 0) {
      onUpdateQuantity(bookId, newQuantity);
    }
  };

  const handleCheckout = async (): Promise<void> => {
    setIsProcessing(true);
    try {
      // Simulate checkout process
      await new Promise(resolve => setTimeout(resolve, 2000));
      onCheckout();
      alert('Order placed successfully!');
      onClose();
    } catch (error) {
      alert('Checkout failed. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  };

  if (cartItems.length === 0) {
    return (
      <div className="cart-overlay">
        <div className="cart-modal">
          <div className="cart-header">
            <h2>Shopping Cart</h2>
            <button className="close-button" onClick={onClose}>×</button>
          </div>
          <div className="cart-content">
            <p>Your cart is empty</p>
            <button onClick={onClose} className="continue-shopping-button">
              Continue Shopping
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-overlay">
      <div className="cart-modal">
        <div className="cart-header">
          <h2>Shopping Cart ({cartItems.length} items)</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
        
        <div className="cart-content">
          <div className="cart-items">
            {cartItems.map((item) => (
              <div key={item.book.id} className="cart-item">
                <div className="item-info">
                  <h4>{item.book.title}</h4>
                  <p className="item-price">€{item.book.price.toFixed(2)}</p>
                </div>
                
                <div className="quantity-controls">
                  <button 
                    onClick={() => handleQuantityChange(item.book.id, item.quantity - 1)}
                    disabled={isProcessing}
                  >
                    -
                  </button>
                  <span className="quantity">{item.quantity}</span>
                  <button 
                    onClick={() => handleQuantityChange(item.book.id, item.quantity + 1)}
                    disabled={isProcessing || item.quantity >= item.book.stockQuantity}
                  >
                    +
                  </button>
                </div>
                
                <div className="item-total">
                  €{(item.book.price * item.quantity).toFixed(2)}
                </div>
                
                <button 
                  className="remove-button"
                  onClick={() => onRemoveItem(item.book.id)}
                  disabled={isProcessing}
                >
                  Remove
                </button>
              </div>
            ))}
          </div>
          
          <div className="cart-summary">
            <div className="total">
              <strong>Total: €{calculateTotal().toFixed(2)}</strong>
            </div>
            
            <div className="cart-actions">
              <button 
                onClick={onClose} 
                className="continue-shopping-button"
                disabled={isProcessing}
              >
                Continue Shopping
              </button>
              <button 
                onClick={handleCheckout} 
                className="checkout-button"
                disabled={isProcessing}
              >
                {isProcessing ? 'Processing...' : 'Checkout'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;