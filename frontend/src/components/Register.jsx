import { useState } from 'react'

export default function Register() {
  const [fullName, setFullName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [password, setPassword] = useState('')
  const [upiId, setUpiId] = useState('')
  const [message, setMessage] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    try {
      const res = await fetch('/api/parents/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName, email, phone, password, upiId })
      })
      if (res.status === 201) {
        setMessage('Registered successfully! You can now log in.')
      } else {
        const text = await res.text()
        setMessage('Error: ' + text)
      }
    } catch (error) {
      setMessage('Network error: ' + error.message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="form-container">
      <div className="form-card">
        <h2>Parent Register</h2>
        <form onSubmit={submit}>
          <div className="form-group">
            <label htmlFor="fullName">Full Name</label>
            <input 
              id="fullName"
              className="form-control" 
              placeholder="Full Name" 
              value={fullName} 
              onChange={e => setFullName(e.target.value)} 
              required 
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input 
              id="email"
              className="form-control" 
              placeholder="Email" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              required 
            />
          </div>
          <div className="form-group">
            <label htmlFor="phone">Phone</label>
            <input 
              id="phone"
              className="form-control" 
              placeholder="Phone" 
              value={phone} 
              onChange={e => setPhone(e.target.value)} 
              required 
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input 
              id="password"
              className="form-control" 
              placeholder="Password" 
              type="password" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              required 
            />
          </div>
          <div className="form-group">
            <label htmlFor="upiId">UPI ID (optional)</label>
            <input 
              id="upiId"
              className="form-control" 
              placeholder="UPI ID (optional)" 
              value={upiId} 
              onChange={e => setUpiId(e.target.value)} 
            />
          </div>
          <button type="submit" className="btn btn-primary btn-block" disabled={isLoading}>
            {isLoading ? 'Registering...' : 'Register'}
          </button>
        </form>
        {message && (
          <div className={`alert ${message.includes('Error') ? 'alert-error' : 'alert-success'}`}>
            {message}
          </div>
        )}
        <div className="text-center mt-1">
          <p>Already have an account? <a href="/login">Login here</a></p>
        </div>
      </div>
    </div>
  )
}
