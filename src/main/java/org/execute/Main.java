package org.execute;

import model.atm.ATM;

import java.util.Scanner;

public class Main {
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
        atm.showMenu();
    }
}