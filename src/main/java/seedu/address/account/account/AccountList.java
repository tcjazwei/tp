package seedu.address.account.account;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seedu.address.account.function.AccountParser;
import seedu.address.account.function.AccountStorage;

/**
 * Represents a list of Accounts.
 * Each AccountList is associated with a map that stores accounts with their usernames as keys.
 */
public class AccountList {
    private static final String filePath = "data/accounts.txt";
    private Map<Username, Account> accounts;
    private AccountParser accountParser = new AccountParser();
    private AccountStorage accountStorage = new AccountStorage(filePath);

    /**
     * Constructs an AccountList instance with an empty map of accounts.
     */
    public AccountList() {
        this.accounts = new HashMap<>();
    }

    /**
     * Adds a new account to the list. Returns true if the account was successfully added,
     * or false if an account with the same username already exists.
     */
    public boolean addAccount(Account account) {
        if (accounts.containsKey(account.getUsername())) {
            return false;
        }
        accounts.put(account.getUsername(), account);
        saveToFile();
        return true;
    }

    /**
     * Attempts to authenticate a user with the provided username and password.
     * Returns the Account object if authentication is successful, or null otherwise.
     */
    public Account authenticate(Username username, Password passwordHash) {
        Account account = accounts.get(username);
        if (account != null && account.getPasswordHash().equals(passwordHash)) {
            return account;
        }
        return null;
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password The password to be hashed.
     * @return The hashed password.
     * @throws RuntimeException if the SHA-256 algorithm is not found.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return transformBytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param hash The byte array to be converted.
     * @return The hexadecimal string.
     */
    private static String transformBytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Saves the current state of the accounts to the storage.
     * It first converts the account values to a string using the accountParser,
     * then saves this string representation to the accountStorage.
     * If an exception occurs during this process, it is caught and its stack trace is printed.
     */
    public void saveToFile() {
        try {
            ArrayList<Account> accountArrayList = new ArrayList<>(accounts.values());
            List<String> accountStringList = accountParser.parseToString(accountArrayList);
            accountStorage.saveToFile(accountStringList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the accounts from the storage file.
     * It first reads the account strings from the accountStorage,
     * then parses these strings to Account objects using the accountParser.
     * The Account objects are then stored in the accounts map.
     * If an exception occurs during this process, it is thrown.
     *
     * @throws IOException if an error occurs during reading from the file.
     */
    public void loadFromFile() throws IOException {
        List<String> accountStringList = accountStorage.loadFromFile();
        List<Account> accountList = accountParser.parseToAccount(accountStringList);
        for (Account account : accountList) {
            accounts.put(account.getUsername(), account);
        }
    }
}
