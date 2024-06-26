package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_TAG_NOT_IN_TAG_LIST;
//import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
//import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "+";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_ID + "ID "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_TAG + "TAG\n"
            + "Example: " + COMMAND_WORD + " "

            + PREFIX_NAME + "John Doe "
            + PREFIX_ID + "johndoe41 "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_TAG + "finance";

    //+ PREFIX_EMAIL + "EMAIL "
    //+ PREFIX_ADDRESS + "ADDRESS "
    //+ "[" + PREFIX_TAG + "TAG]...\n"

    //+ PREFIX_EMAIL + "johnd@example.com "
    //+ PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
    //+ PREFIX_TAG + "friends "
    //+ PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        for (Tag tag : toAdd.getTags()) {
            if (!model.hasTag(tag)) {
                throw new CommandException(String.format(MESSAGE_TAG_NOT_IN_TAG_LIST, tag));
            }
        }

        // Clear sample data upon first entry
        boolean isSample = model.getUserPrefs().getIsSample();
        if (isSample) {
            model.setAddressBook(new AddressBook());
            model.setUserPrefsIsSample(model.getUserPrefs(), false);
        }

        model.addPerson(toAdd);
        model.addExecutedCommand(this);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    /**
     * Retrieves the {@code Person} object that is to be added to the address book.
     * This method allows access to the person specified at the creation of this command.
     *
     * @return The {@code Person} object set to be added by this command.
     */
    public Person getPersonToAdd() {
        return toAdd;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }

}
