import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

export default function CreateChild() {
  const { parentId } = useParams()
  const navigate = useNavigate()
  
  const [name, setName] = useState('')
  const [age, setAge] = useState('')
  const [monthlyLimit, setMonthlyLimit] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    try {
      const res = await fetch(`/api/children/parent/${parentId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, age: parseInt(age), monthlyLimit: parseFloat(monthlyLimit) })
      })
      
      if (res.status === 201) {
        setMessage('Child added successfully')
        setTimeout(() => navigate('/parent-dashboard'), 2000)
      } else {
        const text = await res.text()
        setError('Error: ' + text)
      }
    } catch (err) {
      setError('Network error: ' + err.message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="container">
      <div className="form-card">
        <h2>Add Child</h2>
        <form onSubmit={submit}>
          <div className="form-group">
            <label htmlFor="name">Child's Name</label>
            <input 
              id="name"
              className="form-control"
              placeholder="Child's Name" 
              value={name} 
              onChange={e => setName(e.target.value)} 
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="age">Age</label>
            <input 
              id="age"
              className="form-control"
              placeholder="Age" 
              type="number" 
              value={age} 
              onChange={e => setAge(e.target.value)} 
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="monthlyLimit">Monthly Limit (₹)</label>
            <input 
              id="monthlyLimit"
              className="form-control"
              placeholder="Monthly Limit" 
              type="number" 
              step="0.01"
              value={monthlyLimit} 
              onChange={e => setMonthlyLimit(e.target.value)} 
              required
            />
          </div>
          <button type="submit" className="btn btn-primary btn-block" disabled={isLoading}>
            {isLoading ? 'Adding Child...' : 'Add Child'}
          </button>
        </form>
        
        {message && <div className="alert alert-success">{message}</div>}
        {error && <div className="alert alert-error">{error}</div>}
        
        <button className="btn btn-secondary btn-block" onClick={() => navigate('/parent-dashboard')}>
          Back to Dashboard
        </button>
      </div>
    </div>
  )
}