import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { connect, disconnect } from '../services/websocket';
import Notifications from './Notifications';

const Navbar = ({ user, setUser, setToken }) => {
  const [notifications, setNotifications] = useState([]);

  // Debug logging
  console.log('Navbar rendering with user:', user);

  useEffect(() => {
    if (user) {
      console.log('Connecting WebSocket for user:', user.id);
      // Connect to WebSocket when user logs in
      connect(handleNotificationReceived);
      
      // Store user ID in localStorage for WebSocket subscription
      localStorage.setItem('userId', user.id);
    } else {
      console.log('Disconnecting WebSocket');
      // Disconnect from WebSocket when user logs out
      disconnect();
      localStorage.removeItem('userId');
    }
    
    // Cleanup function to disconnect when component unmounts
    return () => {
      disconnect();
    };
  }, [user]);

  const handleNotificationReceived = (notification) => {
    console.log('Notification received:', notification);
    // Add the new notification to the list
    setNotifications(prev => [notification, ...prev]);
    
    // Show browser notification if supported
    if (Notification.permission === 'granted') {
      new Notification('New Notification', {
        body: notification.message,
        icon: '/notification-icon.png'
      });
    }
  };

  const handleLogout = () => {
    console.log('Logging out');
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
  };

  const requestNotificationPermission = () => {
    if ('Notification' in window) {
      Notification.requestPermission();
    }
  };

  const clearNotifications = () => {
    setNotifications([]);
  };

  useEffect(() => {
    requestNotificationPermission();
  }, []);

  return (
    <nav className="navbar" style={{ border: '2px solid yellow' }}>
      <div className="nav-brand">
        <Link to="/">UPI Pocket Money</Link>
      </div>
      
      <ul className="nav-links">
        {!user ? (
          <>
            <li><Link to="/login">Login</Link></li>
            <li><Link to="/register">Register</Link></li>
          </>
        ) : (
          <>
            {user.role === 'PARENT' ? (
              <li><Link to="/parent-dashboard">Parent Dashboard</Link></li>
            ) : (
              <li><Link to="/child-dashboard">Child Dashboard</Link></li>
            )}
            <li><Link to="/approvals">Approvals</Link></li>
            <li>
              <Notifications 
                notifications={notifications} 
                onClearNotifications={clearNotifications} 
              />
            </li>
            <li><button onClick={handleLogout}>Logout</button></li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;