import React, { useState, useEffect } from 'react';

const AccountConversion = ({ user, token }) => {
  const [isEligible, setIsEligible] = useState(null);
  const [loading, setLoading] = useState(true);
  const [converting, setConverting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (user && token) {
      checkEligibility();
    }
  }, [user, token]);

  const checkEligibility = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/conversion/eligible/${user.id}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const eligible = await response.json();
        setIsEligible(eligible);
      } else {
        setError('Failed to check eligibility for account conversion');
      }
    } catch (err) {
      setError('An error occurred while checking eligibility');
    } finally {
      setLoading(false);
    }
  };

  const handleConvertAccount = async () => {
    if (!window.confirm('Are you sure you want to convert this account to a normal UPI account? This action cannot be undone.')) {
      return;
    }

    setConverting(true);
    setError('');
    setSuccess('');

    try {
      const response = await fetch(`http://localhost:8080/api/conversion/convert/${user.id}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const userData = await response.json();
        setSuccess('Account successfully converted to a normal UPI account!');
        setIsEligible(false); // No longer eligible after conversion
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Failed to convert account');
      }
    } catch (err) {
      setError('An error occurred while converting the account');
    } finally {
      setConverting(false);
    }
  };

  if (loading) {
    return <div className="dashboard">Checking eligibility...</div>;
  }

  return (
    <div className="dashboard conversion-dashboard">
      <h1>Account Conversion</h1>
      
      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}
      
      <div className="dashboard-content">
        <div className="conversion-info">
          <h2>Convert to Normal UPI Account</h2>
          
          {isEligible === true ? (
            <div className="eligible-content">
              <p>Congratulations! You are eligible to convert your pocket money account to a normal UPI account.</p>
              <p>Once converted, you will have full control over your account with no parental restrictions.</p>
              
              <button 
                onClick={handleConvertAccount} 
                disabled={converting}
                className="btn primary"
              >
                {converting ? 'Converting...' : 'Convert Account Now'}
              </button>
            </div>
          ) : isEligible === false ? (
            <div className="not-eligible-content">
              <p>You are not yet eligible to convert your account to a normal UPI account.</p>
              <p>Account conversion is available when you turn 18 years old.</p>
            </div>
          ) : (
            <div className="error-content">
              <p>Unable to determine eligibility for account conversion.</p>
            </div>
          )}
        </div>
        
        <div className="conversion-details">
          <h3>What happens during conversion?</h3>
          <ul>
            <li>Your pocket money account will be converted to a normal UPI account</li>
            <li>All parental controls and restrictions will be removed</li>
            <li>You will have full control over your account balance</li>
            <li>Your transaction history will be preserved</li>
            <li>You can continue using the same app with enhanced features</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default AccountConversion;