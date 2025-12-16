import React, { useState, useEffect } from 'react';

const Notifications = ({ notifications, onClearNotifications }) => {
  const [showNotifications, setShowNotifications] = useState(false);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (showNotifications && !event.target.closest('.notifications-dropdown')) {
        setShowNotifications(false);
      }
    };

    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, [showNotifications]);

  const toggleNotifications = () => {
    setShowNotifications(!showNotifications);
  };

  const clearNotifications = () => {
    onClearNotifications();
    setShowNotifications(false);
  };

  return (
    <div className="notifications-dropdown">
      <button className="notification-bell" onClick={toggleNotifications}>
        <span className="bell-icon">🔔</span>
        {notifications.length > 0 && (
          <span className="notification-count">{notifications.length}</span>
        )}
      </button>
      
      {showNotifications && (
        <div className="notifications-panel">
          <div className="notifications-header">
            <h3>Notifications</h3>
            {notifications.length > 0 && (
              <button onClick={clearNotifications} className="clear-btn">
                Clear All
              </button>
            )}
          </div>
          
          <div className="notifications-list">
            {notifications.length > 0 ? (
              notifications.map((notification, index) => (
                <div key={index} className="notification-item">
                  <div className="notification-content">
                    <p>{notification.message}</p>
                    <span className="notification-time">
                      {new Date(notification.createdAt).toLocaleString()}
                    </span>
                  </div>
                </div>
              ))
            ) : (
              <div className="no-notifications">
                <p>No new notifications</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Notifications;