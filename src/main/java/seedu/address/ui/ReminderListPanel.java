package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Employee;

/**
 * Panel containing a list of reminders for either upcoming birthdays or work anniversaries.
 */
public class ReminderListPanel extends UiPart<Region> {
    private static final String FXML = "ReminderListPanel.fxml";
    private boolean isBirthday;

    @FXML
    private ListView<Employee> reminderListView;

    /**
     * Creates a {@code ReminderListPanel} with the given {@code ObservableList} of persons
     * and a flag indicating whether the reminders are for birthdays.
     *
     * @param reminderList The list of persons to display in the reminder panel.
     * @param isBirthday   True if this panel displays birthday reminders, false for work anniversaries.
     */
    public ReminderListPanel(ObservableList<Employee> reminderList, boolean isBirthday) {
        super(FXML);
        this.isBirthday = isBirthday;
        reminderListView.setItems(reminderList);
        reminderListView.setCellFactory(listView -> new ReminderListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays a {@code Employee} using a {@code ReminderCard}.
     */
    class ReminderListViewCell extends ListCell<Employee> {
        @Override
        protected void updateItem(Employee employee, boolean empty) {
            super.updateItem(employee, empty);
            if (empty || employee == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new ReminderCard(employee, isBirthday).getRoot());
            }
        }
    }
}
