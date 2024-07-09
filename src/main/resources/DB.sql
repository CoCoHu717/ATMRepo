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
