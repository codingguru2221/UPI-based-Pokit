-- Database initialization script for upi_pokit
CREATE DATABASE IF NOT EXISTS upi_pokit;
USE upi_pokit;

CREATE TABLE IF NOT EXISTS parents (
    parent_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    upi_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS children (
    child_id INT AUTO_INCREMENT PRIMARY KEY,
    parent_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    age INT,
    monthly_limit DECIMAL(10,2),
    current_balance DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES parents(parent_id)
);

CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    category_name VARCHAR(50),
    allocated_limit DECIMAL(10,2),
    remaining_limit DECIMAL(10,2),
    locked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (child_id) REFERENCES children(child_id)
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    child_id INT NOT NULL,
    category_id INT,
    amount DECIMAL(10,2),
    merchant_name VARCHAR(100),
    status ENUM('SUCCESS','FAILED','PENDING'),
    approval_required BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (child_id) REFERENCES children(child_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE IF NOT EXISTS approvals (
    approval_id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    parent_id INT NOT NULL,
    status ENUM('APPROVED','REJECTED','PENDING'),
    decision_time TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id),
    FOREIGN KEY (parent_id) REFERENCES parents(parent_id)
);
