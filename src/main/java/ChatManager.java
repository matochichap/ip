import java.util.regex.Pattern;

public class ChatManager {
    private boolean isActive;
    private final TaskList taskList;
    public ChatManager() {
        this.isActive = true;
        this.taskList = new TaskList();
        new WelcomeMessage().send();
    }
    private Action getAction(String userInput) throws DukeException {
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
            throw new DukeException("☹ OOPS!!! The description of a todo cannot be empty.");
        }
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
        return Action.UNDEFINED;
    }
    public void handleInput(String userInput) throws DukeException {
        Action action = getAction(userInput);
        int num;
        String name, deadline, from, to;
        String[] a1, a2;
        switch (action) {
            case BYE:
                this.isActive = false;
                new ByeMessage().send();
                break;
            case LIST:
                taskList.printList();
                break;
            case MARK:
                num = Integer.parseInt(userInput.split(" ", 2)[1]);
                taskList.markTask(num);
                break;
            case UNMARK:
                num = Integer.parseInt(userInput.split(" ", 2)[1]);
                taskList.unmarkTask(num);
                break;
            case TODO:
                name = userInput.split(" ", 2)[1];
                taskList.add(new TodoTask(name));
                break;
            case DEADLINE:
                // assumes " /by " is not contained in deadline name
                a1 = userInput.split(" /by ", 2);
                name = a1[0].split(" ", 2)[1];
                deadline = a1[1];
                taskList.add(new DeadlinesTask(name, deadline));
                break;
            case EVENT:
                // assumes " /to " is not in event name and from date
                a1 = userInput.split(" /to ", 2);
                // assumes " /from " is not in event name
                a2 = a1[0].split(" /from ", 2);
                name = a2[0].split(" ", 2)[1];
                from = a2[1];
                to = a1[1];
                taskList.add(new EventsTask(name, from, to));
                break;
            case DELETE:
                num = Integer.parseInt(userInput.split(" ", 2)[1]);
                taskList.delete(num);
                break;
            default:
                new MenuMessage().send();
        }
    }
    public boolean getIsActive() {
        return this.isActive;
    }
}
