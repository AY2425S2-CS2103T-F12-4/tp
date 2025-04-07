package seedu.address.model;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Employee;
import seedu.address.model.person.EmployeeId;
import seedu.address.model.person.EmployeeIdQuery;
import seedu.address.model.reminder.Reminder;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Employee> PREDICATE_SHOW_ALL_EMPLOYEES = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a employee with the same identity as {@code employee} exists in the address book.
     */
    boolean hasEmployee(Employee employee);

    /**
     * Returns true if more than one employee with the same identity as {@code employee} exists in the address book.
     */
    boolean hasDuplicateEmployeeDetails(Employee employee);
    /**
     * Deletes the given employee.
     * The employee must exist in the address book.
     */
    void deleteEmployee(Employee target);

    /**
     * Adds the given employee.
     * {@code employee} must not already exist in the address book.
     */
    void addEmployee(Employee employee);

    /**
     * Replaces the given employee {@code target} with {@code editedEmployee}.
     * {@code target} must exist in the address book.
     * The employee identity of {@code editedEmployee} must not be the
     * same as another existing employee in the address book.
     */
    void setEmployee(Employee target, Employee editedEmployee);

    /** Returns an unmodifiable view of the filtered employee list */
    ObservableList<Employee> getFilteredEmployeeList();

    /**
     * Returns an unmodifiable view of the filtered employee list that contains only employees with id starting with
     * the provided one, from the current VIEW
     */
    ObservableList<Employee> getFilteredByEmployeeIdPrefixListFromObservable(EmployeeIdQuery employeeIdQuery);

    /**
     * Returns a List of the filtered employee list that contains only employees with id starting with
     * the provided EmployeeId, from the full data
     */
    List<Employee> getFullFilteredByEmployeeIdPrefixListFromData(EmployeeIdQuery employeeIdQuery);

    /**
     * Updates the filter of the filtered employee list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredEmployeeList(Predicate<Employee> predicate);

    void commitChanges();

    ObservableList<Reminder> getReminderList();

    void updateReminderList();
}
