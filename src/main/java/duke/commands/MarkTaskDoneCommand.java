package duke.commands;

import duke.exceptions.DukeException;
import duke.tasks.Task;
import duke.tasks.TaskManager;

public class MarkTaskDoneCommand extends Command {

    private static final String MARK_TASK_DONE_MESSAGE = "Good job! You have finished the following:";
    private static final String MISSING_NUMBER_ERROR =
            "OH NO! You need to specify the task number...\n"
                    + "Enter \"list\" to check the task number!";
    private static final String NUMBER_FORMAT_ERROR = "OH NO! That wasn't a number...";
    private static final String NUMBER_NOT_FOUND_ERROR =
            "OH NO! The task number is invalid, I can't find any tasks matching that number...\n"
                    + "Enter \"list\" to check the task number!";
    private int taskNumber;

    public MarkTaskDoneCommand(String argument) {
        super(argument);
    }

    private int retrieveNumberParameter(String argument) throws DukeException {

        int taskNumber;

        if (isEmptyArgument(argument)) {
            throw new DukeException(MISSING_NUMBER_ERROR);
        }
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new DukeException(NUMBER_FORMAT_ERROR);
        }
        if (!TaskManager.isValidTaskNumber(taskNumber)) {
            throw new DukeException(NUMBER_NOT_FOUND_ERROR);
        }

        return taskNumber;
    }

    @Override
    public CommandResult executeCommand() throws DukeException {
        taskNumber = retrieveNumberParameter(argument);
        Task doneTask = TaskManager.markTaskDone(taskNumber);
        CommandResult result = new CommandResult(MARK_TASK_DONE_MESSAGE + "\n" + doneTask.toString());
        return result;
    }
}