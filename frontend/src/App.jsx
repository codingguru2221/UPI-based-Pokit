import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import './App.css'

// Import components
import Navbar from './components/Navbar'
import Home from './components/Home'
import Login from './components/Login'
import Register from './components/Register'
import ParentDashboard from './components/ParentDashboard'
import ChildDashboard from './components/ChildDashboard'
import TransactionApproval from './components/TransactionApproval'
import AccountConversion from './components/AccountConversion'
import DebugInfo from './components/DebugInfo'

function App() {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(null)

  return (
    <Router>
      <div className="App">
        <Navbar user={user} setUser={setUser} setToken={setToken} />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login setUser={setUser} setToken={setToken} />} />
          <Route path="/register" element={<Register />} />
          <Route path="/parent-dashboard" element={<ParentDashboard user={user} token={token} />} />
          <Route path="/child-dashboard" element={<ChildDashboard user={user} token={token} />} />
          <Route path="/approvals" element={<TransactionApproval user={user} token={token} />} />
          <Route path="/convert-account" element={<AccountConversion user={user} token={token} />} />
          <Route path="/debug" element={<DebugInfo />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App