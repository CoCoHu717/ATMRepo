package model.atm;

import model.user.User;
import utils.DBConfig;
import utils.ReflectionMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;

public class ATM {
    private String userId;
    private String bankAccount;
    private final Connection connection;

    public ATM(String password, String bankAccount) throws Exception {
        this.connection = DBConfig.getConnection();
        if (password == null || password.isEmpty() || bankAccount == null || bankAccount.isEmpty()) {
            throw new Exception("Password or bank account cannot be empty.");
        }
        User user = getUserInfoByBankAccount(bankAccount);
        if (user == null) {
            throw new Exception("User does not exits, please check carefully");
        } else if (!password.equals(user.getPassword())) {
            throw new Exception("Password and user does not match, please check carefully and try again");
        }
        this.userId = user.getId();
        this.bankAccount = bankAccount;
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("ATM Menu:");
            System.out.println("1- See transfers");
            System.out.println("2- Withdraw");
            System.out.println("3- Transfer");
            System.out.println("4- Quit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1 -> getTransferList();
                case 2 -> withdraw(scanner);
                case 3 -> transfer(scanner);
                case 4 -> {
                    closeConnection();
                    System.out.println("Goodbye, see you next time!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void getTransferList() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM transfers WHERE from_user_id = ? OR to_user_id = ?")) {
            stmt.setString(1, userId);
            stmt.setString(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                boolean foundTransfer = false;
                while (rs.next()) {
                    foundTransfer = true;
                    System.out.printf("Transfer ID: %s, From User: %s, To User: %s, Amount: %.2f, Date: %s\n",
                            rs.getString("id"), rs.getString("from_user_id"), rs.getString("to_user_id"), rs.getBigDecimal("amount"),
                            rs.getTimestamp("transfer_date"));
                }
                if (!foundTransfer) {
                    System.out.println("No transfer records found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void withdraw(Scanner scanner) {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        if(amount <= 0){
            System.out.println("Enter wrong amount, try again please!");
            return;
        }
        try (PreparedStatement balanceStmt = connection.prepareStatement("SELECT balance FROM users WHERE id = ? AND bank_account = ?");
             PreparedStatement updateStmt = connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE id = ? AND bank_account = ?")) {
            balanceStmt.setString(1, userId);
            balanceStmt.setString(2, bankAccount);

            try (ResultSet rs = balanceStmt.executeQuery()) {
                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");
                    if (currentBalance >= amount) {
                        updateStmt.setDouble(1, amount);
                        updateStmt.setString(2, userId);
                        updateStmt.setString(3, bankAccount);
                        updateStmt.executeUpdate();
                    } else {
                        System.out.println("Insufficient funds.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void transfer(Scanner scanner) {
        System.out.print("Enter recipient bank account: ");
        String recipientBankAccount = scanner.next();
        if(recipientBankAccount.equals(this.bankAccount)){
            System.out.println("You can not transfer to your own bank account, try again please!");
            return;
        }

        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        if(amount <= 0){
            System.out.println("Enter wrong amount, try again please!");
            return;
        }

        try (PreparedStatement balanceStmt = connection.prepareStatement("SELECT balance FROM users WHERE id = ? AND bank_account = ?");
             PreparedStatement updateStmt = connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE id = ? AND bank_account = ?");
             PreparedStatement recipientStmt = connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE id = ? AND bank_account = ?");
             PreparedStatement transferStmt = connection.prepareStatement("INSERT INTO transfers (id, from_user_id, from_bank_account, to_user_id, to_bank_account, amount) VALUES (?, ?, ?, ?, ?, ?)")) {
            balanceStmt.setString(1, userId);
            balanceStmt.setString(2, bankAccount);

            try (ResultSet rs = balanceStmt.executeQuery()) {
                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");
                    if (currentBalance >= amount) {
                        connection.setAutoCommit(false);

                        updateStmt.setDouble(1, amount);
                        //update from user's balance
                        updateStmt.setString(2, userId);
                        updateStmt.setString(3, bankAccount);
                        updateStmt.executeUpdate();
                        //update recipients info
                        UUID recipientId = getUserIdByBankAccount(recipientBankAccount);
                        if(recipientId !=null){
                            recipientStmt.setDouble(1, amount);
                            recipientStmt.setString(2, recipientId.toString());
                            recipientStmt.setString(3, recipientBankAccount);
                            recipientStmt.executeUpdate();

                            UUID transferId = UUID.randomUUID();
                            transferStmt.setString(1, transferId.toString());
                            transferStmt.setString(2, userId);
                            transferStmt.setString(3, bankAccount);
                            transferStmt.setString(4, recipientId.toString());
                            transferStmt.setString(5, recipientBankAccount);
                            transferStmt.setDouble(6, amount);
                            transferStmt.executeUpdate();
                            connection.commit();
                        }

                        System.out.println("Transfer successful.");
                    } else {
                        System.out.println("Insufficient funds.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Transfer fail. because:"+e.getMessage());
        }
    }
    private UUID getUserIdByBankAccount(String bankAccount) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT id FROM users WHERE bank_account = ?")) {
            stmt.setString(1, bankAccount);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User getUserInfoById(String userId) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return ReflectionMapper.mapResultSetToObject(rs, User.class);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User getUserInfoByBankAccount(String bankAccount) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE bank_account = ?")) {
            stmt.setString(1, bankAccount);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return ReflectionMapper.mapResultSetToObject(rs, User.class);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ATM atm = null;
        int errorCount = 0;
        while (errorCount<=3 && atm == null) {
            try {
                System.out.print("Enter your bank account: ");
                String bankAccount = scanner.next();
                System.out.print("Enter your password: ");
                String password = scanner.next();
                atm = new ATM(password, bankAccount);
                //reset the error count
                errorCount =0;
            } catch (Exception e) {
                System.out.printf("Invalid input info, %s and try again later!\n", e.getMessage());
                errorCount++;
            }
        }
        System.out.println("error time is:"+errorCount);
        atm.showMenu();
    }
}


