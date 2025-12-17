import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

const ChildDashboard = ({ user, token }) => {
  const [accountSummary, setAccountSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showTransactionForm, setShowTransactionForm] = useState(false);
  const [transactionData, setTransactionData] = useState({
    merchantName: '',
    merchantUpiId: '',
    amount: '',
    categoryName: 'FOOD',
    description: ''
  });

  useEffect(() => {
    if (user && token) {
      fetchAccountSummary();
    }
  }, [user, token]);

  const fetchAccountSummary = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/accounts/summary/${user.id}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setAccountSummary(data);
      } else {
        setError('Failed to fetch account summary');
      }
    } catch (err) {
      setError('An error occurred while fetching account summary');
    } finally {
      setLoading(false);
    }
  };

  const handleTransactionChange = (e) => {
    setTransactionData({
      ...transactionData,
      [e.target.name]: e.target.value
    });
  };

  const handleTransactionSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const response = await fetch(`http://localhost:8080/api/accounts/transaction/${user.id}?merchantName=${encodeURIComponent(transactionData.merchantName)}&merchantUpiId=${encodeURIComponent(transactionData.merchantUpiId)}&amount=${transactionData.amount}&categoryName=${transactionData.categoryName}&description=${encodeURIComponent(transactionData.description)}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        alert('Transaction processed successfully!');
        setShowTransactionForm(false);
        setTransactionData({
          merchantName: '',
          merchantUpiId: '',
          amount: '',
          categoryName: 'FOOD',
          description: ''
        });
        fetchAccountSummary(); // Refresh the dashboard
      } else {
        const errorData = await response.json();
        alert(errorData.message || 'Transaction failed');
      }
    } catch (err) {
      alert('An error occurred while processing the transaction');
    }
  };

  if (loading) {
    return <div className="dashboard">Loading...</div>;
  }

  return (
    <div className="dashboard child-dashboard">
      <h1>Child Dashboard</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="dashboard-content">
        <div className="account-summary">
          <h2>Your Pocket Money</h2>
          {accountSummary && (
            <div className="summary-cards">
              <div className="summary-card">
                <h3>Available Balance</h3>
                <p>₹{accountSummary.pocketBalance || '0.00'}</p>
              </div>
              
              <div className="summary-card">
                <h3>Monthly Limit</h3>
                <p>₹{accountSummary.pocketAccount?.monthlyLimit || '0.00'}</p>
              </div>
            </div>
          )}
        </div>
        
        <div className="category-limits">
          <h2>Category Limits</h2>
          {accountSummary && accountSummary.categoryLimits && accountSummary.categoryLimits.length > 0 ? (
            <div className="limits-grid">
              {accountSummary.categoryLimits.map(limit => (
                <div key={limit.id} className="limit-card">
                  <h3>{limit.category.name}</h3>
                  <div className="progress-bar">
                    <div 
                      className="progress-fill" 
                      style={{ width: `${(limit.currentSpent / limit.limitAmount) * 100}%` }}
                    ></div>
                  </div>
                  <p>₹{limit.currentSpent} / ₹{limit.limitAmount}</p>
                </div>
              ))}
            </div>
          ) : (
            <p>No category limits set</p>
          )}
        </div>
        
        <div className="transaction-section">
          <h2>Make a Transaction</h2>
          {!showTransactionForm ? (
            <button onClick={() => setShowTransactionForm(true)} className="btn primary">
              New Transaction
            </button>
          ) : (
            <form onSubmit={handleTransactionSubmit} className="transaction-form">
              <div className="form-group">
                <label htmlFor="merchantName">Merchant Name</label>
                <input
                  type="text"
                  id="merchantName"
                  name="merchantName"
                  value={transactionData.merchantName}
                  onChange={handleTransactionChange}
                  required
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="merchantUpiId">Merchant UPI ID</label>
                <input
                  type="text"
                  id="merchantUpiId"
                  name="merchantUpiId"
                  value={transactionData.merchantUpiId}
                  onChange={handleTransactionChange}
                  required
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="amount">Amount (₹)</label>
                <input
                  type="number"
                  id="amount"
                  name="amount"
                  value={transactionData.amount}
                  onChange={handleTransactionChange}
                  min="0.01"
                  step="0.01"
                  required
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="categoryName">Category</label>
                <select
                  id="categoryName"
                  name="categoryName"
                  value={transactionData.categoryName}
                  onChange={handleTransactionChange}
                  required
                >
                  <option value="FOOD">Food</option>
                  <option value="TRAVEL">Travel</option>
                  <option value="SHOPPING">Shopping</option>
                  <option value="ENTERTAINMENT">Entertainment</option>
                </select>
              </div>
              
              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  name="description"
                  value={transactionData.description}
                  onChange={handleTransactionChange}
                  rows="3"
                ></textarea>
              </div>
              
              <div className="form-actions">
                <button type="submit" className="btn primary">Process Transaction</button>
                <button type="button" onClick={() => setShowTransactionForm(false)} className="btn secondary">
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>
        
        <div className="account-conversion-section">
          <h2>Account Conversion</h2>
          <p>When you turn 18, you can convert your pocket money account to a normal UPI account.</p>
          <Link to="/convert-account" className="btn primary">
            Check Conversion Eligibility
          </Link>
        </div>
        
        <div className="recent-transactions">
          <h2>Recent Transactions</h2>
          {accountSummary && accountSummary.recentTransactions && accountSummary.recentTransactions.length > 0 ? (
            <table className="transactions-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Merchant</th>
                  <th>Category</th>
                  <th>Amount</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {accountSummary.recentTransactions.map(transaction => (
                  <tr key={transaction.id}>
                    <td>{new Date(transaction.transactionTime).toLocaleDateString()}</td>
                    <td>{transaction.merchantName}</td>
                    <td>{transaction.category?.name || 'N/A'}</td>
                    <td>₹{transaction.amount}</td>
                    <td>{transaction.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p>No recent transactions</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default ChildDashboard;