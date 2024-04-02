package seedu.address.logic;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.account.account.Account;
import seedu.address.account.account.AccountList;
import seedu.address.account.function.AccountParser;
import seedu.address.account.function.AccountStorage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;

/**
 * Manages the current session, including the logged-in account and the associated logic manager.
 */
public class AccountManager {
    private AccountList accountList;
    private AccountParser accountParser = new AccountParser();
    private Account currentAccount;
    private Logic logic;

    private boolean isUserLogin = false;

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    /**
     * Constructs an AccountManager.
     * It loads the accounts from a file and stores them in an AccountList.
     * If the file cannot be read, an empty AccountList is created.
     */
    public AccountManager(Logic logic) {
        AccountStorage accountStorage = new AccountStorage("data/accounts.txt");
        try {
            List<String> accountListInString = accountStorage.load();
            List<Account> accountListInAccount = accountParser.parseToAccount(accountListInString);
            this.accountList = new AccountList();
            for (Account account : accountListInAccount) {
                this.accountList.addAccount(account);
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.accountList = new AccountList();
        }
        currentAccount = null;
        this.logic = logic;
    }

    /**
     * Logs in the specified account and loads the associated address book.
     *
     * @param account The account to log in.
     */
    public void login(Account account) {
        this.currentAccount = account;
        isUserLogin = true;
    }

    /**
     * Logs out the current account and clears the address book.
     */
    public void logout() {
        this.currentAccount = null;
        this.isUserLogin = false;
        //this.addressBook = null;
    }

    /**
     * Loads the address book associated with the specified account.
     *
     * @param account The account whose address book is to be loaded.
     * @return The loaded address book.
     */
    private AddressBook loadAddressBook(Account account) {
        account.getUsername();
        return null;
    }

    /**
     * Returns the current account.
     *
     * @return The current account.
     */
    public Account getCurrentAccount() {
        return currentAccount;
    }

    public AccountList getAccountList() {
        return accountList;
    }

    private void updateModelManagerForUser(String username) {
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(Paths.get("data", username + ".json"));
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(Paths.get("data", username + ".json"));
        Storage storage = new StorageManager(addressBookStorage, userPrefsStorage);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);

        logger.info("Using data file : " + storage.getAddressBookFilePath());
        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (!addressBookOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getAddressBookFilePath()
                        + " populated with a sample AddressBook.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getAddressBookFilePath() + " could not be loaded."
                    + " Will be starting with an empty AddressBook.");
            initialData = new AddressBook();
        }

        logic.setModel(new ModelManager(initialData, userPrefs));
    }

    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }
        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }
}
