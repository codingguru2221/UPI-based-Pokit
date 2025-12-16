import React, { useState, useEffect } from 'react';

const ParentDashboard = ({ user, token }) => {
  const [accountSummary, setAccountSummary] = useState(null);
  const [children, setChildren] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user && token) {
      fetchAccountSummary();
      fetchChildren();
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

  const fetchChildren = async () => {
    // In a real implementation, this would fetch actual children from the backend
    // For now, we'll use mock data
    setChildren([
      { id: 1, name: 'Child 1', age: 12 },
      { id: 2, name: 'Child 2', age: 15 }
    ]);
    setLoading(false);
  };

  if (loading) {
    return <div className="dashboard">Loading...</div>;
  }

  return (
    <div className="dashboard parent-dashboard">
      <h1>Parent Dashboard</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="dashboard-content">
        <div className="account-summary">
          <h2>Account Summary</h2>
          {accountSummary && (
            <div className="summary-cards">
              <div className="summary-card">
                <h3>Total Balance</h3>
                <p>₹{accountSummary.totalBalance || '0.00'}</p>
              </div>
              
              <div className="summary-card">
                <h3>Pocket Balance</h3>
                <p>₹{accountSummary.pocketBalance || '0.00'}</p>
              </div>
            </div>
          )}
        </div>
        
        <div className="children-section">
          <h2>Your Children</h2>
          <div className="children-grid">
            {children.map(child => (
              <div key={child.id} className="child-card">
                <h3>{child.name}</h3>
                <p>Age: {child.age}</p>
                <button onClick={() => {/* Navigate to child details */}}>
                  View Details
                </button>
              </div>
            ))}
          </div>
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

export default ParentDashboard;