package duke.parser;

import java.util.regex.Pattern;

import duke.Action;
import duke.exception.InvalidCommandException;
import duke.exception.InvalidIndexException;
import duke.exception.InvalidInputException;
import duke.message.ByeMessage;
import duke.message.Message;
import duke.task.DeadlinesTask;
import duke.task.EventsTask;
import duke.task.TaskList;
import duke.task.TodoTask;
import duke.templates.MessageTemplates;

/**
 * Represents the UserInputParser.
 */
public class UserInputParser {
    private static boolean isActive = true;

    /**
     * Returns the Action of the user input.
     * @param userInput User input.
     * @return Action of the user input.
     * @throws InvalidInputException If user input is invalid.
     * @throws InvalidCommandException If user input is invalid.
     */
    private static Action getAction(String userInput) throws InvalidInputException, InvalidCommandException {
        if (userInput.equals("bye")) {
            return Action.BYE;
        }
        if (userInput.equals("list")) {
            return Action.LIST;
        }
        if (Pattern.matches("mark \\d+", userInput)) {
            return Action.MARK;
        }
        if (Pattern.matches("unmark \\d+", userInput)) {
            return Action.UNMARK;
        }
        if (Pattern.matches("^todo\\s*$", userInput)) {
            throw new InvalidInputException(MessageTemplates.MESSAGE_INVALID_TODO);
        }
        // Redundant check, to satisfy Level-5: Handle Errors requirement
        if (Pattern.matches("todo .+", userInput)) {
            return Action.TODO;
        }
        if (Pattern.matches("deadline .+ /by .+", userInput)) {
            return Action.DEADLINE;
        }
        if (Pattern.matches("event .+ /from .+ /to .+", userInput)) {
            return Action.EVENT;
        }
        if (Pattern.matches("delete \\d+", userInput)) {
            return Action.DELETE;
        }
        if (Pattern.matches("find .+", userInput)) {
            return Action.FIND;
        }
        throw new InvalidCommandException();
    }

    /**
     * Parses the user input.
     * @param userInput User input.
     * @param taskList TaskList.
     * @return Message.
     * @throws InvalidInputException If user input is invalid.
     * @throws InvalidCommandException Command does not exist.
     * @throws InvalidIndexException If index is invalid.
     */
    public static Message parse(String userInput, TaskList taskList)
            throws InvalidInputException, InvalidCommandException, InvalidIndexException {
        Action action = UserInputParser.getAction(userInput);
        int num;
        String name;
        String by;
        String from;
        String to;
        String[] a1;
        String[] a2;
        switch (action) {
        case BYE:
            isActive = false;
            return new ByeMessage();
        case LIST:
            return taskList.printList();
        case MARK:
            num = Integer.parseInt(userInput.split(" ", 2)[1]);
            return taskList.markTask(num);
        case UNMARK:
            num = Integer.parseInt(userInput.split(" ", 2)[1]);
            return taskList.unmarkTask(num);
        case TODO:
            name = userInput.split(" ", 2)[1];
            return taskList.add(new TodoTask(name, false));
        case DEADLINE:
            a1 = userInput.split(" /by ", 2);
            if (a1[1].contains("/by")) {
                throw new InvalidInputException(MessageTemplates.MESSAGE_DEADLINE_CONTAINS_BY);
            }
            // assert " /by " is not contained in deadline name
            assert !a1[1].contains("/by") : "deadline name should not contain ' /by '";

            name = a1[0].split(" ", 2)[1];
            by = DateTimeParser.parseDateTime(a1[1]);
            return taskList.add(new DeadlinesTask(name, false, by));
        case EVENT:
            a1 = userInput.split(" /to ", 2);
            if (a1[1].contains("/to")) {
                throw new InvalidInputException(MessageTemplates.MESSAGE_EVENT_CONTAINS_TO);
            }
            // assert " /to " is not in event name and from date
            assert !a1[1].contains("/to") : "event name and from date should not contain ' /to '";

            a2 = a1[0].split(" /from ", 2);
            if (a2[1].contains("/from")) {
                throw new InvalidInputException(MessageTemplates.MESSAGE_EVENT_CONTAINS_FROM);
            }
            // assert " /from " is not in event name
            assert !a2[1].contains("/from") : "event name should not contain ' /from '";

            name = a2[0].split(" ", 2)[1];
            from = DateTimeParser.parseDateTime(a2[1]);
            to = DateTimeParser.parseDateTime(a1[1]);
            if (!DateTimeParser.isValidPeriod(from, to)) {
                throw new InvalidInputException(MessageTemplates.MESSAGE_INVALID_EVENT_PERIOD);
            }
            return taskList.add(new EventsTask(name, false, from, to));
        case DELETE:
            num = Integer.parseInt(userInput.split(" ", 2)[1]);
            return taskList.delete(num);
        case FIND:
            name = userInput.split(" ", 2)[1];
            return taskList.find(name);
        default:
            throw new InvalidCommandException();
        }
    }
    /**
     * Returns the isActive.
     * @return isActive.
     */
    public static boolean getIsActive() {
        return UserInputParser.isActive;
    }
    /**
     * Sets the isActive.
     *
     * @param isActive isActive.
     */
    public static void setIsActive(boolean isActive) {
        UserInputParser.isActive = isActive;
    }
}
