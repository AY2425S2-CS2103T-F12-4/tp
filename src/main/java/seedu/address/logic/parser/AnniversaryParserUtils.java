package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ANNIVERSARY_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ANNIVERSARY_DESC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ANNIVERSARY_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ANNIVERSARY_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ANNIVERSARY_TYPE_DESC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BIRTHDAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WORK_ANNIVERSARY;
import static seedu.address.logic.parser.ParserUtil.validateSafeContent;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.anniversary.Anniversary;
import seedu.address.model.anniversary.AnniversaryType;
import seedu.address.model.anniversary.Birthday;
import seedu.address.model.anniversary.WorkAnniversary;
import seedu.address.model.person.Name;

/**
 * Utility class for parsing different kinds of anniversary information.
 */
public class AnniversaryParserUtils {
    /** Error message if required anniversary fields are missing. */
    public static final String INVALID_ANNIVERSARY = "Anniversary must include the following"
            + " prefixes: an/NAME d/DATE at/type ad/description atdesc/typeDescription"
            + " or bd/ for BIRTHDAY or wa/ for WORK_ANNIVERSARY";
    /** Error message if the date format is invalid. */
    public static final String MESSAGE_DATE_CONSTRAINTS = "Anniversary date must be in YYYY-MM-DD format.";

    /**
     * Parses anniversary-related fields from the given `ArgumentMultimap`.
     * Throws `ParseException` if the fields are invalid.
     *
     * @param argMultimap Contains user arguments.
     * @return A valid `Anniversary`.
     * @throws ParseException If arguments are not properly formatted.
     */
    public static Anniversary resolveAnniversaryInput(ArgumentMultimap argMultimap) throws ParseException {
        boolean hasAnnivType = arePresent(argMultimap, PREFIX_ANNIVERSARY_NAME, PREFIX_ANNIVERSARY_TYPE);
        boolean hasBirthday = arePresent(argMultimap, PREFIX_BIRTHDAY, PREFIX_NAME);
        boolean hasWork = argMultimap.getValue(PREFIX_WORK_ANNIVERSARY).isPresent();

        if (hasAnnivType && (hasBirthday || hasWork)) {
            throw new ParseException(errorMsg());
        }
        if (hasAnnivType) {
            return parseStandardAnniversary(argMultimap);
        }
        if (hasBirthday) {
            return parseBirthday(argMultimap);
        }
        if (hasWork) {
            return parseWorkAnniversary(argMultimap);
        }
        throw new ParseException(errorMsg());
    }

    /**
     * Parses multiple anniversaries from the given `ArgumentMultimap`.
     *
     * @param argMultimap Contains user arguments.
     * @return A list of valid `Anniversary` objects.
     * @throws ParseException If arguments are invalid.
     */
    public static List<Anniversary> multiAddAnniversary(ArgumentMultimap argMultimap) throws ParseException {
        List<Anniversary> anniversaries = new ArrayList<>();
        if (arePresent(argMultimap, PREFIX_BIRTHDAY, PREFIX_NAME)) {
            anniversaries.add(parseBirthday(argMultimap));
        }
        if (argMultimap.getValue(PREFIX_WORK_ANNIVERSARY).isPresent()) {
            anniversaries.add(parseWorkAnniversary(argMultimap));
        }
        return anniversaries;
    }

    /**
     * Parses a standard anniversary using well-known prefixes.
     *
     * @param argMultimap Contains user arguments.
     * @return A `Anniversary` with the required fields.
     * @throws ParseException If required fields are missing or invalid.
     */
    private static Anniversary parseStandardAnniversary(ArgumentMultimap argMultimap) throws ParseException {
        String name = argMultimap.getValue(PREFIX_ANNIVERSARY_NAME).get();
        String desc = argMultimap.getValue(PREFIX_ANNIVERSARY_DESC).orElse("");
        String date = argMultimap.getValue(PREFIX_ANNIVERSARY_DATE).orElseThrow(() -> new ParseException(dateMsg()));
        String type = argMultimap.getValue(PREFIX_ANNIVERSARY_TYPE).get();
        String typeDesc = argMultimap.getValue(PREFIX_ANNIVERSARY_TYPE_DESC).orElse("");
        return parseAnniversary(name, desc, date, type, typeDesc);
    }

    /**
     * Parses a birthday anniversary.
     *
     * @param argMultimap Contains user arguments.
     * @return A `Anniversary` representing a birthday.
     * @throws ParseException If required fields are invalid.
     */
    private static Anniversary parseBirthday(ArgumentMultimap argMultimap) throws ParseException {
        Name personName = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        String date = argMultimap.getValue(PREFIX_BIRTHDAY).get();
        return parseAnniversaryWithName(personName, date, PREFIX_BIRTHDAY);
    }

    /**
     * Parses a work anniversary.
     *
     * @param argMultimap Contains user arguments.
     * @return A `Anniversary` representing a work anniversary.
     * @throws ParseException If required fields are invalid.
     */
    private static Anniversary parseWorkAnniversary(ArgumentMultimap argMultimap) throws ParseException {
        Name personName = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        String date = argMultimap.getValue(PREFIX_WORK_ANNIVERSARY).get();
        return parseAnniversaryWithName(personName, date, PREFIX_WORK_ANNIVERSARY);
    }

    /**
     * Checks if all specified prefixes exist in the `ArgumentMultimap`.
     *
     * @param map The argument multimap to check.
     * @param prefixes Prefixes to verify.
     * @return True if all prefixes are present.
     */
    private static boolean arePresent(ArgumentMultimap map, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> map.getValue(prefix).isPresent());
    }

    /**
     * Creates a standard error message related to anniversary fields.
     * @return A formatted error message.
     */
    private static String errorMsg() {
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT, INVALID_ANNIVERSARY);
    }

    /**
     * Creates a standard error message related to invalid date input.
     *
     * @return A formatted date error message.
     */
    private static String dateMsg() {
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_DATE_CONSTRAINTS);
    }
    /**
     * Parses a {@code String name}, {@code String dateStr}, and a {@code String type} into an {@code Anniversary}.
     *
     * @param name the name of the anniversary for custom anniversaries
     * @param description the description of the anniversary
     * @param dateStr the date of the anniversary
     * @param type the type of the anniversary
     * @throws ParseException if the given {@code dateStr} is invalid.
     */
    public static Anniversary parseAnniversary(String name, String description, String dateStr, String type,
                                               String typeDescription) throws ParseException {
        requireNonNull(dateStr);
        String trimmedAnniversaryDate = dateStr.trim();
        LocalDate date;
        try {
            validateSafeContent(name, "anniversary name", true);
            validateSafeContent(type, "anniversary type", false);
            validateSafeContent(description, "anniversary description", false);
            date = LocalDate.parse(trimmedAnniversaryDate);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_DATE_CONSTRAINTS);
        }
        if (name == null || name.isEmpty()) {
            return new Anniversary(date, new AnniversaryType(type, typeDescription),
                    description, type);
        } else {
            return new Anniversary(date, new AnniversaryType(type, typeDescription),
                    description, name);
        }
    }
    /**
     * Parses a {@code String name}, {@code String dateStr}, and a {@code String type} into an {@code Anniversary}.
     *
     * @param name the name of the person attributed to prebuilt-anniversaries
     * @param dateStr the date of the anniversary
     * @param type the prefix of the anniversary
     * @throws ParseException if the given {@code dateStr} is invalid.
     */
    public static Anniversary parseAnniversaryWithName(Name name, String dateStr,
                                                       Prefix type) throws ParseException {
        String trimmedAnniversaryDate = dateStr.trim();
        LocalDate date;
        try {
            date = LocalDate.parse(trimmedAnniversaryDate);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_DATE_CONSTRAINTS);
        }
        if (type.equals(PREFIX_BIRTHDAY)) {
            String birthdayAppend = "Birthday";
            return new Anniversary(date, new Birthday(), name + "'s " + birthdayAppend, birthdayAppend);
        }
        if (type.equals(PREFIX_WORK_ANNIVERSARY)) {
            String workAnniversaryAppend = "work anniversary";
            return new Anniversary(date, new WorkAnniversary(), name + "'s "
                    + workAnniversaryAppend, workAnniversaryAppend);
        }
        throw new ParseException("Invalid anniversary type");
    }
}
