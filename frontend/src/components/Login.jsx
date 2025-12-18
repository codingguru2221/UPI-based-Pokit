import { useState } from 'react'

export default function Login({ setUser, setToken }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    try {
      const res = await fetch('/api/parents/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })
      if (res.ok) {
        const data = await res.json()
        setUser(data)
        setToken('dummy-token') // In a real app, you'd get this from the response
        setMessage('Logged in successfully!')
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
        <h2>Parent Login</h2>
        <form onSubmit={submit}>
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
          <button type="submit" className="btn btn-primary btn-block" disabled={isLoading}>
            {isLoading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        {message && (
          <div className={`alert ${message.includes('Error') ? 'alert-error' : 'alert-success'}`}>
            {message}
          </div>
        )}
        <div className="text-center mt-1">
          <p>Don't have an account? <a href="/register">Register here</a></p>
        </div>
      </div>
    </div>
  )
}
