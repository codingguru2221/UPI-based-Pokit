import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// Polyfill for global object in Vite
if (typeof window !== 'undefined') {
  window.global = window;
}

// Debug logging
console.log('Starting UPI Pocket Money App');
const rootElement = document.getElementById('root');
console.log('Root element found:', rootElement);

// Ensure root element has proper styles
if (rootElement) {
  rootElement.style.width = '100%';
  rootElement.style.minHeight = '100vh';
  
  createRoot(rootElement).render(
    <StrictMode>
      <App />
    </StrictMode>,
  );
  console.log('App rendered successfully');
} else {
  console.error('Root element not found!');
  document.body.innerHTML = '<div style="padding: 20px; color: red;"><h1>Error: Root element not found!</h1><p>Please check the index.html file.</p></div>';
}
