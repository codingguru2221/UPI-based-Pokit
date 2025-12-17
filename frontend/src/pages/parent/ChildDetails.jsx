import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const ChildDetails = ({ user, token }) => {
  const { childId } = useParams();
  const navigate = useNavigate();
  const [child, setChild] = useState(null);
  const [accountSummary, setAccountSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showSetLimitForm, setShowSetLimitForm] = useState(false);
  const [limitData, setLimitData] = useState({
    monthlyLimit: '',
    categoryId: ''
  });

  useEffect(() => {
    if (user && token) {
      fetchChildDetails();
      fetchAccountSummary();
    }
  }, [user, token, childId]);

  const fetchChildDetails = async () => {
    // In a real implementation, this would fetch actual child details from the backend
    // For now, we'll use mock data
    setChild({
      id: childId,
      name: `Child ${childId}`,
      age: 12,
      username: `child${childId}`
    });
    setLoading(false);
  };

  const fetchAccountSummary = async () => {
    // In a real implementation, this would fetch actual account summary from the backend
    // For now, we'll use mock data
    setAccountSummary({
      pocketBalance: 500.00,
      pocketAccount: {
        monthlyLimit: 1000.00
      },
      categoryLimits: [
        { id: 1, category: { name: 'FOOD' }, limitAmount: 300.00, currentSpent: 150.00 },
        { id: 2, category: { name: 'TRAVEL' }, limitAmount: 200.00, currentSpent: 50.00 },
        { id: 3, category: { name: 'SHOPPING' }, limitAmount: 300.00, currentSpent: 200.00 },
        { id: 4, category: { name: 'ENTERTAINMENT' }, limitAmount: 200.00, currentSpent: 100.00 }
      ]
    });
    setLoading(false);
  };

  const handleSetLimit = async (e) => {
    e.preventDefault();
    
    try {
      // In a real implementation, this would call the backend API to set limits
      alert(`Limit set successfully for category ${limitData.categoryId} with amount ${limitData.monthlyLimit}`);
      setShowSetLimitForm(false);
      setLimitData({ monthlyLimit: '', categoryId: '' });
    } catch (err) {
      setError('An error occurred while setting the limit');
    }
  };

  const handleCreatePocketAccount = async () => {
    try {
      // In a real implementation, this would call the backend API to create a pocket account
      alert('Pocket account created successfully!');
    } catch (err) {
      setError('An error occurred while creating the pocket account');
    }
  };

  if (loading) {
    return <div className="dashboard">Loading...</div>;
  }

  return (
    <div className="dashboard child-details">
      <div className="dashboard-header">
        <button onClick={() => navigate(-1)} className="btn secondary">
          ← Back
        </button>
        <h1>Child Details: {child?.name}</h1>
      </div>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="dashboard-content">
        <div className="child-info">
          <h2>Child Information</h2>
          <div className="info-grid">
            <div className="info-item">
              <label>Name:</label>
              <span>{child?.name}</span>
            </div>
            <div className="info-item">
              <label>Username:</label>
              <span>{child?.username}</span>
            </div>
            <div className="info-item">
              <label>Age:</label>
              <span>{child?.age}</span>
            </div>
          </div>
          
          <button onClick={handleCreatePocketAccount} className="btn primary">
            Create Pocket Account
          </button>
        </div>
        
        <div className="account-summary">
          <h2>Pocket Account Summary</h2>
          {accountSummary ? (
            <div className="summary-cards">
              <div className="summary-card">
                <h3>Monthly Limit</h3>
                <p>₹{accountSummary.pocketAccount?.monthlyLimit || '0.00'}</p>
              </div>
              
              <div className="summary-card">
                <h3>Available Balance</h3>
                <p>₹{accountSummary.pocketBalance || '0.00'}</p>
              </div>
            </div>
          ) : (
            <p>No pocket account found</p>
          )}
        </div>
        
        <div className="category-limits">
          <div className="section-header">
            <h2>Category Limits</h2>
            <button onClick={() => setShowSetLimitForm(!showSetLimitForm)} className="btn secondary">
              {showSetLimitForm ? 'Cancel' : 'Set Limit'}
            </button>
          </div>
          
          {showSetLimitForm && (
            <form onSubmit={handleSetLimit} className="limit-form">
              <div className="form-group">
                <label htmlFor="categoryId">Category</label>
                <select
                  id="categoryId"
                  value={limitData.categoryId}
                  onChange={(e) => setLimitData({...limitData, categoryId: e.target.value})}
                  required
                >
                  <option value="">Select Category</option>
                  <option value="1">FOOD</option>
                  <option value="2">TRAVEL</option>
                  <option value="3">SHOPPING</option>
                  <option value="4">ENTERTAINMENT</option>
                </select>
              </div>
              
              <div className="form-group">
                <label htmlFor="monthlyLimit">Monthly Limit (₹)</label>
                <input
                  type="number"
                  id="monthlyLimit"
                  value={limitData.monthlyLimit}
                  onChange={(e) => setLimitData({...limitData, monthlyLimit: e.target.value})}
                  min="0"
                  step="0.01"
                  required
                />
              </div>
              
              <button type="submit" className="btn primary">Set Limit</button>
            </form>
          )}
          
          {accountSummary && accountSummary.categoryLimits && accountSummary.categoryLimits.length > 0 ? (
            <div className="limits-grid">
              {accountSummary.categoryLimits.map(limit => (
                <div key={limit.id} className="limit-card">
                  <h3>{limit.category.name}</h3>
                  <div className="progress-bar">
                    <div 
                      className="progress-fill" 
                      style={{ width: `${(limit.currentSpent / limit.limitAmount) * 100}%` }}
                    ></div>
                  </div>
                  <p>₹{limit.currentSpent} / ₹{limit.limitAmount}</p>
                </div>
              ))}
            </div>
          ) : (
            <p>No category limits set</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default ChildDetails;