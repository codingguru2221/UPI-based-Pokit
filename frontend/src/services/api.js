const API_BASE_URL = 'http://localhost:8080';

class ApiService {
  constructor() {
    this.baseUrl = API_BASE_URL;
  }

  // Authentication APIs
  async login(credentials) {
    const response = await fetch(`${this.baseUrl}/api/auth/signin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
    });
    
    return response.json();
  }

  async registerParent(userData) {
    const response = await fetch(`${this.baseUrl}/api/auth/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ...userData,
        role: 'PARENT'
      }),
    });
    
    return response.json();
  }

  async createChild(userData, token) {
    const response = await fetch(`${this.baseUrl}/api/auth/child`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(userData),
    });
    
    return response.json();
  }

  // Account APIs
  async getAccountSummary(userId, token) {
    const response = await fetch(`${this.baseUrl}/api/accounts/summary/${userId}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    
    return response.json();
  }

  async createPocketAccount(childId, parentId, monthlyLimit, token) {
    const response = await fetch(`${this.baseUrl}/api/accounts/pocket/${childId}?parentId=${parentId}&monthlyLimit=${monthlyLimit}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    
    return response.json();
  }

  async processTransaction(childId, transactionData, token) {
    const { merchantName, merchantUpiId, amount, categoryName, description } = transactionData;
    const response = await fetch(`${this.baseUrl}/api/accounts/transaction/${childId}?merchantName=${encodeURIComponent(merchantName)}&merchantUpiId=${encodeURIComponent(merchantUpiId)}&amount=${amount}&categoryName=${categoryName}&description=${encodeURIComponent(description)}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    
    return response.json();
  }

  async approveTransaction(transactionId, parentId, token) {
    const response = await fetch(`${this.baseUrl}/api/accounts/transaction/${transactionId}/approve?parentId=${parentId}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    
    return response.json();
  }

  async rejectTransaction(transactionId, parentId, token) {
    const response = await fetch(`${this.baseUrl}/api/accounts/transaction/${transactionId}/reject?parentId=${parentId}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    
    return response.json();
  }

  async reallocateLimits(pocketAccountId, fromCategoryId, toCategoryId, amount, token) {
    const response = await fetch(`${this.baseUrl}/api/accounts/reallocate?pocketAccountId=${pocketAccountId}&fromCategoryId=${fromCategoryId}&toCategoryId=${toCategoryId}&amount=${amount}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    
    return response.json();
  }
}

export default new ApiService();