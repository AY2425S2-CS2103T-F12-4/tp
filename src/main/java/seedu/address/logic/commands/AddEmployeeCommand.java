package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_WARNING_ANNI_AFTER_TODAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BIRTHDAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMPLOYEEID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_JOBPOSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WORK_ANNIVERSARY;

import java.time.LocalDate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Employee;

/**
 * Adds a employee to the address book.
 */
public class AddEmployeeCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a employee to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_JOBPOSITION + "JOB_POSITION "
            + "[" + PREFIX_TAG + "TAG]..."
            + PREFIX_BIRTHDAY + "BIRTHDAY"
            + PREFIX_WORK_ANNIVERSARY + "WORK_ANNIVERSARY "
            + PREFIX_EMPLOYEEID + "[EMPLOYEEID]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_JOBPOSITION + "Data Engineer "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney "
            + PREFIX_BIRTHDAY + "2000-01-01 "
            + PREFIX_WORK_ANNIVERSARY + "2020-07-08 "
            + PREFIX_EMPLOYEEID + "3b9417cc-cf4e-4231-bc4d-4fd167c2abc6";

    public static final String MESSAGE_SUCCESS = "New employee added: %1$s";
    public static final String MESSAGE_DUPLICATE_EMPLOYEE = "This employee already exists in the address book";
    public static final String MESSAGE_EMPLOYEE_ID_CONFLICT = "This employee ID is either a prefix of another "
            + "existing employee ID or another existing employee ID is a prefix of this one";

    private final Employee toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Employee}
     */
    public AddEmployeeCommand(Employee employee) {
        requireNonNull(employee);
        toAdd = employee;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasEmployee(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_EMPLOYEE);
        }

        if (model.hasEmployeeIdPrefixConflict(toAdd.getEmployeeId())) {
            throw new CommandException(MESSAGE_EMPLOYEE_ID_CONFLICT);
        }

        //Save the state before any potential changes
        model.commitChanges();
        boolean isAnyAnniAfterToday = toAdd.getAnniversaries().stream()
                .anyMatch(anniversary -> anniversary.getDate().isAfter(LocalDate.now()));

        model.addEmployee(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd))
                + (isAnyAnniAfterToday ? "\n" + MESSAGE_WARNING_ANNI_AFTER_TODAY : ""));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddEmployeeCommand)) {
            return false;
        }

        AddEmployeeCommand otherAddEmployeeCommand = (AddEmployeeCommand) other;
        return toAdd.equals(otherAddEmployeeCommand.toAdd);
    }
    /** Custom equals method to compare AddCommand objects while ignoring employeeId. */
    public boolean hasSameDetails(AddEmployeeCommand other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return this.toAdd.hasSameDetails(other.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
