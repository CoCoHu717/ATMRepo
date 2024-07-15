# ATM Interface Project

This project is a simple ATM interface built using Java and MySQL.
It allows users to view their transfer history, withdraw money, transfer money to other accounts, and quit the application.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [How to Run](#how-to-run)
- [Notes](#notes)


## Features
1. **Login**: Log in using your bank account and password. (You have 3 attempts to enter the correct password. After 3 incorrect attempts, the application will close and an error message is shown)
2. **View Transfers**: See a list of all transfers you have done.
3. **Withdraw**: Withdraw money from your account (balance updates accordingly, and if the balance is insufficient, an error message is shown).
4. **Transfer**: Transfer money from your account to another specified account (balance updates accordingly).
5. **Quit**: Close the application.

## Prerequisites
- Java Development Kit (JDK) 21
- MySQL 8.0 
- MySQL Workbench (optional, for database management)
- Maven (for managing dependencies and building the project)
- IntelliJ IDEA or any other preferred IDE

## Setup

### Database Setup
1. **Start MySQL Server**: Make sure your MySQL server is running.
2. **Create Database**: Create a new database called `db_atm` in your MySQL server.
3. **Create Tables**: Use the following SQL script to create the required tables and insert initial data.

```sql
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id CHAR(36),
    bank_account VARCHAR(20),
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modify_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id, bank_account)
);

DROP TABLE IF EXISTS transfers;
CREATE TABLE transfers (
    id CHAR(36) PRIMARY KEY,
    from_user_id CHAR(36),
    from_bank_account VARCHAR(20),
    to_user_id CHAR(36),
    to_bank_account VARCHAR(20),
    amount DECIMAL(10, 2),
    transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modify_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id, from_bank_account) REFERENCES users(id, bank_account),
    FOREIGN KEY (to_user_id, to_bank_account) REFERENCES users(id, bank_account)
);

INSERT INTO users (id, bank_account, username, password, balance) VALUES (UUID(), '1234567890', 'CoCo', 'password1', 1000.00);
INSERT INTO users (id, bank_account, username, password, balance) VALUES (UUID(), '2345678901', 'Filipe', 'password2', 500.00);
INSERT INTO users (id, bank_account, username, password, balance) VALUES (UUID(), '3456789012', 'Charlie', 'password3', 750.00);
```

### Project Setup
1. **Clone the Repository**: Clone this repository to your local machine.
2. **Ensure your MySQL server is running and accessible.**
3. **Configure Database Connection:**
   
- Create a db.properties file in src/main/resources/
- Add the following properties to the db.properties file:
  
   ```
       url=jdbc:mysql://localhost:3306/db_atm
       user=your_mysql_username
       password=your_mysql_password
  ```
3. **Build the Project:**: Use Maven to build the project. Run the following command in the project's root directory: ```mvn clean install```

## How to run
Under package org.execute,the Main.java file contains the entry point of the application, initializing the ATM interface and displaying the menu.
<img width="1433" alt="image" src="https://github.com/CoCoHu717/ATMRepo/assets/174446249/e152a74a-562d-4ce2-99f8-b3d7141ecd93">
<img width="479" alt="image" src="https://github.com/CoCoHu717/ATMRepo/assets/174446249/17b6ebd3-ad3a-485c-8f26-5f08f17581af">

## Notes
- Ensure your MySQL server is running and accessible.
- Adjust the database URL, username, and password in the db.properties file according to your MySQL configuration.
- The project uses UUIDs for user and transfer IDs, ensuring unique identifiers for all records.
- The project assumes a basic understanding of Java, JDBC, and SQL.
