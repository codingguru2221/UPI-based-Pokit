import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'

export default function Approvals() {
  const { parentId } = useParams()
  const navigate = useNavigate()
  
  const [approvals, setApprovals] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [processing, setProcessing] = useState({})

  useEffect(() => {
    if (parentId) {
      fetchPendingApprovals()
    }
  }, [parentId])

  const fetchPendingApprovals = async () => {
    try {
      const res = await fetch(`/api/approvals/parent/${parentId}/pending`)
      if (res.ok) {
        const data = await res.json()
        setApprovals(data)
      } else {
        setError('Failed to fetch pending approvals')
      }
    } catch (err) {
      setError('Error fetching pending approvals: ' + err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleApproval = async (approvalId, approved) => {
    // Set processing state for this approval
    setProcessing(prev => ({ ...prev, [approvalId]: true }))
    
    try {
      const res = await fetch('/api/approvals/process', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ approvalId, approved })
      })
      
      if (res.ok) {
        // Refresh the list
        fetchPendingApprovals()
      } else {
        setError('Failed to process approval')
      }
    } catch (err) {
      setError('Error processing approval: ' + err.message)
    } finally {
      // Clear processing state for this approval
      setProcessing(prev => {
        const newState = { ...prev }
        delete newState[approvalId]
        return newState
      })
    }
  }

  if (loading) return <div className="container"><div className="card"><p>Loading...</p></div></div>
  if (error) return <div className="container"><div className="card"><div className="alert alert-error">Error: {error}</div></div></div>

  return (
    <div className="container dashboard">
      <div className="dashboard-header">
        <h1>Pending Approvals</h1>
        <button className="btn btn-secondary" onClick={() => navigate('/parent-dashboard')}>
          ← Back to Dashboard
        </button>
      </div>
      
      <div className="card">
        <div className="card-header">
          <h2>Approval Requests</h2>
        </div>
        
        {approvals.length === 0 ? (
          <p>No pending approvals</p>
        ) : (
          <div className="table-responsive">
            <table>
              <thead>
                <tr>
                  <th>Transaction ID</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {approvals.map(approval => (
                  <tr key={approval.approvalId}>
                    <td>{approval.transactionId}</td>
                    <td><span className="status-badge status-pending">{approval.status}</span></td>
                    <td>
                      <button 
                        className="btn btn-success"
                        onClick={() => handleApproval(approval.approvalId, true)}
                        disabled={!!processing[approval.approvalId]}
                      >
                        {processing[approval.approvalId] ? 'Approving...' : 'Approve'}
                      </button>
                      <button 
                        className="btn btn-danger"
                        onClick={() => handleApproval(approval.approvalId, false)}
                        disabled={!!processing[approval.approvalId]}
                      >
                        {processing[approval.approvalId] ? 'Rejecting...' : 'Reject'}
                      </button>
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