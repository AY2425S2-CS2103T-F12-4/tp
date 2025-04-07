package seedu.address.model.person;

import lombok.EqualsAndHashCode;

/**
 * Represents a query for an employee ID.
 * Has an additional boolean field to indicate if the query is an exact match. If it is, then the query will only return an exact match.
 * Otherwise, the query will return all employee IDs that have the same prefix.
 */
@EqualsAndHashCode(callSuper = true)
public class EmployeeIdQuery extends EmployeeId {
    boolean isExactMatch;

    public EmployeeIdQuery(String employeeId, boolean isExactMatch) {
        super(employeeId);
        this.isExactMatch = isExactMatch;
    }

    /**
     * Parses a string to create an EmployeeIdQuery.
     * If the string ends with $ then it is an exact match.
     * Otherwise, it is a prefix match.
     * 
     * @param employeeId The string to parse.
     * @return An EmployeeIdQuery object.
     */
    public static EmployeeIdQuery fromString(String employeeId) {
        if (employeeId.endsWith("$")) {
            return new EmployeeIdQuery(employeeId.substring(0, employeeId.length() - 1), true);
        }
        return new EmployeeIdQuery(employeeId, false);
    }

    /**
     * Checks if the given EmployeeId is a prefix of another EmployeeId.
     * If the given EmployeeId ends with $ then it returns true if the other EmployeeId exactly matches the given EmployeeId without the $ sign.
     *
     * @param other The EmployeeId to be checked against.
     * @return true if this EmployeeId is a prefix of the specified EmployeeId, otherwise false.
     */
    public boolean isPrefixOf(EmployeeId other) {
        if (isExactMatch) {
            return other.value.equals(this.value);
        }
        return other.value.startsWith(this.value);
    }

    /**
     * Checks if the given EmployeeId is a valid EmployeeIdQuery.
     * 
     * @param employeeId The EmployeeId to be checked.
     * @return true if the EmployeeId is a valid EmployeeIdQuery, otherwise false.
     */
    public static boolean isValidEmployeeIdQuery(String employeeIdQuery) {
        if (employeeIdQuery.isEmpty()) {
            return false;
        }
        if (employeeIdQuery.endsWith("$")) {
            return isValidEmployeeId(employeeIdQuery.substring(0, employeeIdQuery.length() - 1));
        }
        return isValidEmployeeId(employeeIdQuery);
    }
    
    @Override
    public String toString() {
        return super.toString() + (isExactMatch ? "$" : "");
    }
}
