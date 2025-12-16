import React, { useEffect, useState } from 'react';

const DebugInfo = () => {
  const [debugInfo, setDebugInfo] = useState({});

  useEffect(() => {
    // Gather debug information
    const info = {
      userAgent: navigator.userAgent,
      language: navigator.language,
      cookiesEnabled: navigator.cookieEnabled,
      online: navigator.onLine,
      screenResolution: `${screen.width}x${screen.height}`,
      windowSize: `${window.innerWidth}x${window.innerHeight}`,
      timestamp: new Date().toISOString(),
      location: window.location.href,
      hostname: window.location.hostname,
      port: window.location.port,
      pathname: window.location.pathname
    };
    
    setDebugInfo(info);
    
    // Log to console
    console.log('Debug Info:', info);
    
    // Check if required APIs are available
    console.log('React Router DOM available:', typeof window.ReactRouterDOM !== 'undefined');
    console.log('Local Storage available:', typeof Storage !== 'undefined');
    
    // Test localStorage
    try {
      localStorage.setItem('debug-test', 'test-value');
      const testValue = localStorage.getItem('debug-test');
      localStorage.removeItem('debug-test');
      console.log('LocalStorage working:', testValue === 'test-value');
    } catch (e) {
      console.error('LocalStorage error:', e);
    }
  }, []);

  return (
    <div style={{ padding: '20px', fontFamily: 'monospace', backgroundColor: '#f5f5f5' }}>
      <h2>Debug Information</h2>
      <p>If you can see this, the React app is rendering correctly.</p>
      
      <h3>Browser Information:</h3>
      <ul>
        <li><strong>User Agent:</strong> {debugInfo.userAgent}</li>
        <li><strong>Language:</strong> {debugInfo.language}</li>
        <li><strong>Cookies Enabled:</strong> {debugInfo.cookiesEnabled ? 'Yes' : 'No'}</li>
        <li><strong>Online:</strong> {debugInfo.online ? 'Yes' : 'No'}</li>
        <li><strong>Screen Resolution:</strong> {debugInfo.screenResolution}</li>
        <li><strong>Window Size:</strong> {debugInfo.windowSize}</li>
      </ul>
      
      <h3>Location Information:</h3>
      <ul>
        <li><strong>URL:</strong> {debugInfo.location}</li>
        <li><strong>Hostname:</strong> {debugInfo.hostname}</li>
        <li><strong>Port:</strong> {debugInfo.port}</li>
        <li><strong>Pathname:</strong> {debugInfo.pathname}</li>
      </ul>
      
      <h3>Timestamp:</h3>
      <p>{debugInfo.timestamp}</p>
    </div>
  );
};

export default DebugInfo;