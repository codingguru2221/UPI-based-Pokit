import React, { useState, useEffect } from 'react';

const TransactionApprovals = ({ user, token }) => {
  const [pendingTransactions, setPendingTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user && token) {
      fetchPendingTransactions();
    }
  }, [user, token]);

  const fetchPendingTransactions = async () => {
    try {
      // In a real implementation, this would fetch actual pending transactions from the backend
      // For now, we'll use mock data
      setPendingTransactions([
        {
          id: 1,
          childName: 'Child 1',
          merchantName: 'Pizza Hut',
          amount: 150.00,
          category: 'FOOD',
          description: 'Lunch with friends',
          timestamp: '2023-06-15T12:30:00Z'
        },
        {
          id: 2,
          childName: 'Child 2',
          merchantName: 'Book Store',
          amount: 250.00,
          category: 'SHOPPING',
          description: 'School books',
          timestamp: '2023-06-15T14:45:00Z'
        }
      ]);
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch pending transactions');
      setLoading(false);
    }
  };

  const handleApprove = async (transactionId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/accounts/transaction/${transactionId}/approve?parentId=${user.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        alert('Transaction approved successfully!');
        // Remove the approved transaction from the list
        setPendingTransactions(pendingTransactions.filter(t => t.id !== transactionId));
      } else {
        const errorData = await response.json();
        alert(errorData.message || 'Failed to approve transaction');
      }
    } catch (err) {
      alert('An error occurred while approving the transaction');
    }
  };

  const handleReject = async (transactionId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/accounts/transaction/${transactionId}/reject?parentId=${user.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        alert('Transaction rejected successfully!');
        // Remove the rejected transaction from the list
        setPendingTransactions(pendingTransactions.filter(t => t.id !== transactionId));
      } else {
        const errorData = await response.json();
        alert(errorData.message || 'Failed to reject transaction');
      }
    } catch (err) {
      alert('An error occurred while rejecting the transaction');
    }
  };

  if (loading) {
    return <div className="dashboard">Loading...</div>;
  }

  return (
    <div className="dashboard approval-dashboard">
      <h1>Transaction Approvals</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="dashboard-content">
        <div className="pending-transactions">
          <h2>Pending Approvals</h2>
          {pendingTransactions.length > 0 ? (
            <div className="transactions-list">
              {pendingTransactions.map(transaction => (
                <div key={transaction.id} className="transaction-card">
                  <div className="transaction-header">
                    <h3>{transaction.childName}</h3>
                    <span className="timestamp">
                      {new Date(transaction.timestamp).toLocaleString()}
                    </span>
                  </div>
                  
                  <div className="transaction-details">
                    <p><strong>Merchant:</strong> {transaction.merchantName}</p>
                    <p><strong>Amount:</strong> ₹{transaction.amount}</p>
                    <p><strong>Category:</strong> {transaction.category}</p>
                    <p><strong>Description:</strong> {transaction.description}</p>
                  </div>
                  
                  <div className="transaction-actions">
                    <button 
                      onClick={() => handleApprove(transaction.id)} 
                      className="btn approve"
                    >
                      Approve
                    </button>
                    <button 
                      onClick={() => handleReject(transaction.id)} 
                      className="btn reject"
                    >
                      Reject
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p>No pending transactions for approval</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default TransactionApprovals;