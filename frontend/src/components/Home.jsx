import React from 'react';

const Home = () => {
  return (
    <div className="home">
      <div className="hero">
        <h1>UPI Pocket Money</h1>
        <p>Empowering parents to teach financial responsibility to their children through digital payments</p>
      </div>
      
      <div className="features">
        <div className="feature-card">
          <h3>Parental Control</h3>
          <p>Set spending limits and monitor your child's expenses in real-time</p>
        </div>
        
        <div className="feature-card">
          <h3>Flexible Categories</h3>
          <p>Allocate money across different categories like food, travel, and shopping</p>
        </div>
        
        <div className="feature-card">
          <h3>Real-time Approval</h3>
          <p>Get notified instantly when your child needs approval for a transaction</p>
        </div>
        
        <div className="feature-card">
          <h3>Seamless Conversion</h3>
          <p>When your child turns 18, their account converts to a full UPI account</p>
        </div>
      </div>
      
      <div className="cta-section">
        <h2>Get Started Today</h2>
        <p>Join thousands of parents teaching their children financial responsibility</p>
        <div className="cta-buttons">
          <a href="/register" className="btn primary">Sign Up as Parent</a>
          <a href="/login" className="btn secondary">Login</a>
        </div>
      </div>
    </div>
  );
};

export default Home;