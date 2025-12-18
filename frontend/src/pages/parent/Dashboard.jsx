import { useState, useEffect } from 'react'

export default function ParentDashboard({ user, token }) {
  const [children, setChildren] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (user) {
      fetchChildren()
    }
  }, [user])

  const fetchChildren = async () => {
    try {
      const res = await fetch(`/api/children/parent/${user.parentId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      if (res.ok) {
        const data = await res.json()
        setChildren(data)
      } else {
        setError('Failed to fetch children')
      }
    } catch (err) {
      setError('Error fetching children: ' + err.message)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div className="container"><div className="card"><p>Loading...</p></div></div>
  if (error) return <div className="container"><div className="card"><div className="alert alert-error">Error: {error}</div></div></div>

  return (
    <div className="container dashboard">
      <div className="dashboard-header">
        <h1>Parent Dashboard</h1>
        <h3>Welcome, {user.fullName}</h3>
      </div>
      
      <div className="summary-cards">
        <div className="summary-card">
          <h3>Total Children</h3>
          <p className="amount">{children.length}</p>
        </div>
      </div>
      
      <div className="card">
        <div className="card-header">
          <h2>Your Children</h2>
        </div>
        {children.length === 0 ? (
          <p>No children added yet</p>
        ) : (
          <div className="children-grid">
            {children.map(child => (
              <div className="child-card" key={child.childId}>
                <h3>{child.name}</h3>
                <p>Age: {child.age}</p>
                <p className="balance">Balance: ₹{child.currentBalance} / ₹{child.monthlyLimit}</p>
                <button 
                  className="btn btn-secondary"
                  onClick={() => window.location.href = `/parent/child-details/${child.childId}`}>
                  View Details
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
      
      <div className="card">
        <div className="card-header">
          <h2>Actions</h2>
        </div>
        <div className="d-flex justify-content-between">
          <button 
            className="btn btn-primary"
            onClick={() => window.location.href = `/parent/create-child/${user.parentId}`}>
            Add Child
          </button>
          <button 
            className="btn btn-secondary"
            onClick={() => window.location.href = `/parent/approvals/${user.parentId}`}>
            View Pending Approvals
          </button>
        </div>
      </div>
    </div>
  )
}