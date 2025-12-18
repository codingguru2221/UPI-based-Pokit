import { useState, useEffect } from 'react'

export default function ChildDashboard({ user, token }) {
  const [child, setChild] = useState(null)
  const [categories, setCategories] = useState([])
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (user) {
      fetchChildDetails()
      fetchCategories()
      fetchTransactions()
    }
  }, [user])

  const fetchChildDetails = async () => {
    try {
      const res = await fetch(`/api/children/${user.childId}`)
      if (res.ok) {
        const data = await res.json()
        setChild(data)
      } else {
        setError('Failed to fetch child details')
      }
    } catch (err) {
      setError('Error fetching child details: ' + err.message)
    }
  }

  const fetchCategories = async () => {
    try {
      const res = await fetch(`/api/categories/child/${user.childId}`)
      if (res.ok) {
        const data = await res.json()
        setCategories(data)
      } else {
        setError('Failed to fetch categories')
      }
    } catch (err) {
      setError('Error fetching categories: ' + err.message)
    }
  }

  const fetchTransactions = async () => {
    try {
      const res = await fetch(`/api/transactions/child/${user.childId}`)
      if (res.ok) {
        const data = await res.json()
        setTransactions(data)
      } else {
        setError('Failed to fetch transactions')
      }
    } catch (err) {
      setError('Error fetching transactions: ' + err.message)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div className="container"><div className="card"><p>Loading...</p></div></div>
  if (error) return <div className="container"><div className="card"><div className="alert alert-error">Error: {error}</div></div></div>
  if (!child) return <div className="container"><div className="card"><p>No child data found</p></div></div>

  return (
    <div className="container dashboard">
      <div className="dashboard-header">
        <h1>Child Dashboard</h1>
      </div>
      
      <div className="card">
        <div className="card-header">
          <h2>Welcome, {child.name}</h2>
        </div>
        <div className="summary-cards">
          <div className="summary-card">
            <h3>Age</h3>
            <p className="amount">{child.age}</p>
          </div>
          <div className="summary-card">
            <h3>Current Balance</h3>
            <p className="amount">₹{child.currentBalance}</p>
          </div>
          <div className="summary-card">
            <h3>Monthly Limit</h3>
            <p className="amount">₹{child.monthlyLimit}</p>
          </div>
        </div>
      </div>
      
      <div className="card">
        <div className="card-header">
          <h2>Spending Categories</h2>
        </div>
        {categories.length === 0 ? (
          <p>No categories assigned</p>
        ) : (
          <div className="children-grid">
            {categories.map(category => (
              <div className="child-card" key={category.categoryId}>
                <h3>{category.categoryName}</h3>
                <div className="progress-container">
                  <div className="progress-label">
                    <span>Used: ₹{category.allocatedLimit - category.remainingLimit}</span>
                    <span>Remaining: ₹{category.remainingLimit}</span>
                  </div>
                  <div className="progress-bar">
                    <div 
                      className="progress-fill" 
                      style={
                        { width: `${((category.allocatedLimit - category.remainingLimit) / category.allocatedLimit) * 100}%` }
                      }
                    ></div>
                  </div>
                  <p>Limit: ₹{category.allocatedLimit}</p>
                </div>
                {category.locked && (
                  <span className="status-badge status-pending">LOCKED</span>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
      
      <div className="card">
        <div className="card-header">
          <h2>Recent Transactions</h2>
        </div>
        {transactions.length === 0 ? (
          <p>No transactions yet</p>
        ) : (
          <div className="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>Merchant</th>
                  <th>Amount</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {transactions.slice(0, 5).map(transaction => (
                  <tr key={transaction.transactionId}>
                    <td>{transaction.merchantName}</td>
                    <td>₹{transaction.amount}</td>
                    <td>
                      <span className={`status-badge ${
                        transaction.status === 'SUCCESS' ? 'status-success' : 
                        transaction.status === 'PENDING' ? 'status-pending' : 'status-failed'
                      }`}>
                        {transaction.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}