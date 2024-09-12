import java.time.LocalDateTime;

import Talky.Parser;
import Talky.SaveData;
import Talky.TalkyException;
import Talky.TaskList;
import Talky.Ui;

/**
 * Talky Chatbot that acts as a Task Manager.
 */
public class Talky {
    private static final String SAVE_PATH = "./data/talky.txt";
    private Ui ui;
    private TaskList userTasks;
    private SaveData saveData;

    public Talky() {
        ui = new Ui();
        saveData = new SaveData(SAVE_PATH);
        try {
            userTasks = new TaskList(saveData.loadData());
        } catch (TalkyException err) {
            ui.printSeperator(err.getMessage());
            userTasks = new TaskList();
        }
    }

    /**
     * Runs Talky Chatbot.
     */
    public void run() {
        ui.printSeperator("Hello I'm Talky\n" + "How may I help you?");
        boolean isRunning = true;
        while (isRunning) {
            try {
                String newCommand = ui.getCommand();
                String commandType = Parser.commandType(newCommand);
                String[] commandArgs = Parser.commandArgs(newCommand, commandType);
                switch (commandType) {
                case "bye":
                    ui.printSeperator("Bye!!! Do let me know if there's anything else!");
                    isRunning = false;
                    break;
                case "list":
                    ui.printSeperator(userTasks.toListFormat());
                    break;
                case "mark":
                    mark(commandArgs);
                    break;
                case "unmark":
                    unmark(commandArgs);
                    break;
                case "todo":
                    addTodo(commandArgs);
                    break;
                case "deadline":
                    addDeadline(commandArgs);
                    break;
                case "event":
                    addEvent(commandArgs);
                    break;
                case "delete":
                    delete(commandArgs);
                    break;
                case "find":
                    find(commandArgs);
                    break;
                default:
                    throw new TalkyException("Invalid Command");
                }
                saveData.saveData(userTasks);
            } catch (TalkyException err) {
                ui.printSeperator(err.getMessage());
            }
        }
    }

    public static void main(String[] args) throws TalkyException {
        new Talky().run();
    }

    public String returnResponse(String input) {
        String command = input;
        String response = "";
        try {
            String commandType = Parser.commandType(command);
            String[] commandArgs = Parser.commandArgs(command, commandType);
            switch (commandType) {
            case "bye":
                response = "Bye!!! Do let me know if there's anything else!";
                break;
            case "list":
                response = userTasks.toListFormat();
                break;
            case "mark":
                response = mark(commandArgs);
                break;
            case "unmark":
                response = unmark(commandArgs);
                break;
            case "todo":
                response = addTodo(commandArgs);
                break;
            case "deadline":
                response = addDeadline(commandArgs);
                break;
            case "event":
                response = addEvent(commandArgs);
                break;
            case "delete":
                response = delete(commandArgs);
                break;
            case "find":
                response = find(commandArgs);
                break;
            default:
                throw new TalkyException("Invalid Command");
            }
            saveData.saveData(userTasks);
        } catch (TalkyException err) {
            response = err.getMessage();
        }
        return response;
    }

    private String mark(String[] commandArgs) throws TalkyException {
        try {
            int indexToChange = Integer.parseInt(commandArgs[0]) - 1;
            String changedTask = userTasks.markTask(indexToChange, true);
            return "Task Marked Done: " + changedTask;
        } catch (IndexOutOfBoundsException err) {
            throw new TalkyException("Given index out of bounds");
        } catch (NumberFormatException err) {
            throw new TalkyException("Mark must be given a number");
        }
    }

    private String unmark(String[] commandArgs) throws TalkyException {
        try {
            int indexToChange = Integer.parseInt(commandArgs[0]) - 1;
            String changedTask = userTasks.markTask(indexToChange, false);
            return "Task Marked Done: " + changedTask;
        } catch (IndexOutOfBoundsException err) {
            throw new TalkyException("Given index out of bounds");
        } catch (NumberFormatException err) {
            throw new TalkyException("Unmark must be given a number");
        }
    }

    private String addTodo(String[] commandArgs) throws TalkyException {
        String name = commandArgs[0];
        userTasks.addToDo(name);
        return "Added ToDo: " + name;
    }

    private String addDeadline(String[] commandArgs) throws TalkyException {
        String name = commandArgs[0];
        String by = commandArgs[1];
        LocalDateTime formattedBy = Parser.parseDate(by).get(0);
        userTasks.addDeadline(name, formattedBy);
        return "Added Deadline: " + name;
    }

    private String addEvent(String[] commandArgs) throws TalkyException {
        String name = commandArgs[0];
        String from = commandArgs[1];
        String to = commandArgs[2];
        LocalDateTime formattedFrom = Parser.parseDate(from).get(0);
        LocalDateTime formattedTo = Parser.parseDate(to).get(0);
        userTasks.addEvent(name, formattedFrom, formattedTo);
        return "Added Event: " + name;
    }

    private String delete(String[] commandArgs) throws TalkyException {
        try {
            int indexToChange = Integer.parseInt(commandArgs[0]) - 1;
            String changedTask = userTasks.deleteTask(indexToChange);
            return "Deleted Task: " + changedTask;
        } catch (IndexOutOfBoundsException err) {
            throw new TalkyException("Given index out of bounds");
        } catch (NumberFormatException err) {
            throw new TalkyException("Delete must be given a number");
        }
    }

    private String find(String[] commandArgs) throws TalkyException {
        String keyword = commandArgs[0];
        TaskList foundTasks = userTasks.find(keyword);
        String header = String.format("Tasks that contains the keyword (%s):\n", keyword);
        return header + foundTasks.toListFormat();
    }
}

