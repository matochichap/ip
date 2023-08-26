import duke.exception.InvalidCommandException;
import duke.exception.InvalidIndexException;
import duke.exception.InvalidInputException;
import duke.Storage;
import duke.parser.UserInputParser;
import duke.task.TaskList;
import duke.Ui;

import java.io.IOException;
import java.util.Scanner;

public class Duke {
    private TaskList tasks;
    private final Storage storage;
    private final Ui ui;
    public Duke(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        // load data file
        try {
            this.tasks = new TaskList(storage.loadFile());
        } catch (IOException e) {
            this.ui.showError(e.getMessage());
            this.ui.showLine();
            this.tasks = new TaskList();
        }
    }
    public static void main(String[] args) {
        new Duke("data/duke.txt").run();
    }
    private void run() {
        Scanner sc = new Scanner(System.in);
        while (UserInputParser.isActive) {
            String userInput = sc.nextLine();
            try {
                UserInputParser.parse(userInput, this.tasks);
                this.storage.writeToFile(this.tasks);
            } catch (InvalidInputException e) {
                this.ui.showError(e.getMessage());
            } catch (InvalidCommandException e) {
                this.ui.showMenu();
            } catch (InvalidIndexException e) {
                this.ui.showInvalidIndexError();
            } catch (IOException e) {
                this.ui.showSaveDataError();
            } finally {
                this.ui.showLine();
            }
        }
    }
}
