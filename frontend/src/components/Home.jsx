export default function Home() {
  return (
    <div className="container">
      <div className="card" style={{ marginTop: '2rem', textAlign: 'center' }}>
        <h1>UPI-Powered Smart Pocket Money Management System</h1>
        <p>Welcome to the smart way to manage your child's pocket money!</p>
      </div>
      
      <div className="dashboard-content">
        <div className="card">
          <h2>For Parents</h2>
          <p>Register to create controlled sub-accounts for your children with category-based spending rules.</p>
          <div style={{ marginTop: '1rem' }}>
            <a href="/register" className="btn btn-primary">Register as Parent</a>
            <a href="/login" className="btn btn-secondary">Login as Parent</a>
          </div>
        </div>
        
        <div className="card">
          <h2>For Children</h2>
          <p>Login to your assigned account to make purchases within the rules set by your parents.</p>
          <div style={{ marginTop: '1rem' }}>
            <a href="/login" className="btn btn-primary">Login as Child</a>
          </div>
        </div>
      </div>
    </div>
  )
}