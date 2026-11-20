import java.io.*;
class InvalidWithdrawalException extends Exception {
    InvalidWithdrawalException(String message) { super(message); }
}
class InsufficientFundsException extends Exception {
    InsufficientFundsException(String message) { super(message); }
}
public class ATM {
    static double balance = 5000;
    static void withdraw(String account, double amount)
            throws InvalidWithdrawalException, InsufficientFundsException {
        if (!account.matches("\\d{6,}"))
            throw new IllegalArgumentException("Invalid account number");
        if (amount <= 0) throw new InvalidWithdrawalException("Amount must be positive");
        if (amount > balance) throw new InsufficientFundsException("Insufficient balance");
        balance -= amount;
        System.out.printf("Cash dispensed. Balance: %.2f%n", balance);
    }
    public static void main(String[] args) {
        System.out.println("=== BANK ATM ===");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter account number: ");
            String account = br.readLine();
            System.out.print("Enter withdrawal amount: ");
            double amount = Double.parseDouble(br.readLine());
            try {
                // Create card-reader.txt in this folder to simulate a present reader.
                try (BufferedReader card = new BufferedReader(new FileReader("card-reader.txt"))) {
                    card.readLine();
                }
                withdraw(account, amount);
            } catch (InvalidWithdrawalException | InsufficientFundsException e) {
                System.out.println("Withdrawal rejected: " + e.getMessage());
            } catch (FileNotFoundException e) {
                System.out.println("Card reader error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Input/device error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } finally {
                System.out.println("ATM transaction session ended.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: amount must be numeric.");
            System.out.println("ATM transaction session ended.");
        } catch (IOException e) {
            System.out.println("Input error: " + e.getMessage());
        }
    }
}