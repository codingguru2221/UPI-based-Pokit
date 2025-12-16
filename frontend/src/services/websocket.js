import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

let stompClient = null;
let connected = false;

/**
 * Connect to the WebSocket server
 * @param {Function} onNotificationReceived callback function to handle received notifications
 */
export const connect = (onNotificationReceived) => {
  if (connected) return;
  
  const socket = new SockJS('http://localhost:8080/ws');
  stompClient = Stomp.over(socket);
  
  stompClient.connect({}, frame => {
    console.log('Connected: ' + frame);
    connected = true;
    
    // Subscribe to notifications for the current user
    if (localStorage.getItem('userId')) {
      stompClient.subscribe(`/topic/notifications/${localStorage.getItem('userId')}`, notification => {
        if (onNotificationReceived) {
          onNotificationReceived(JSON.parse(notification.body));
        }
      });
    }
  });
};

/**
 * Disconnect from the WebSocket server
 */
export const disconnect = () => {
  if (stompClient !== null) {
    stompClient.disconnect();
    connected = false;
  }
  console.log("Disconnected");
};

/**
 * Subscribe to notifications for a specific user
 * @param {number} userId the ID of the user to subscribe to
 * @param {Function} onNotificationReceived callback function to handle received notifications
 */
export const subscribeToUserNotifications = (userId, onNotificationReceived) => {
  if (stompClient !== null && connected) {
    stompClient.subscribe(`/topic/notifications/${userId}`, notification => {
      if (onNotificationReceived) {
        onNotificationReceived(JSON.parse(notification.body));
      }
    });
  }
};