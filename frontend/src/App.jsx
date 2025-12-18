import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

// Import components
import Home from './components/Home'
import Login from './components/Login'
import Register from './components/Register'

// Import parent pages
import ParentDashboard from './pages/parent/Dashboard'
import CreateChild from './pages/parent/CreateChild'
import ChildDetails from './pages/parent/ChildDetails'
import Approvals from './pages/parent/Approvals'

// Import child pages
import ChildDashboard from './pages/child/Dashboard'

function App() {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(null)

  return (
    <Router>
      <div className="app">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login setUser={setUser} setToken={setToken} />} />
          <Route path="/register" element={<Register />} />
          <Route path="/parent-dashboard" element={<ParentDashboard user={user} token={token} />} />
          <Route path="/parent/create-child/:parentId" element={<CreateChild />} />
          <Route path="/parent/child-details/:childId" element={<ChildDetails />} />
          <Route path="/parent/approvals/:parentId" element={<Approvals />} />
          <Route path="/child-dashboard" element={<ChildDashboard user={user} token={token} />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App