package duke.storage;

import duke.exceptions.DukeException;
import duke.tasks.Deadline;
import duke.tasks.Event;
import duke.tasks.Task;
import duke.tasks.ToDo;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents the individual information of a {@code Task} retrieved from storage, before adding
 * them to the {@code Task} list.
 */
public class Data {

    private String[] parameters;
    private static final String CORRUPTED_DATA_ERROR =
            "OH NO! Your data is corrupted, starting a new file for you...";

    /**
     * <p>Checks if the date and time string is of a valid format.</p>
     * <p>Valid form: [yyyy-mm-dd]T[HH:MM]</p>
     *
     * @param date date string to check for validity
     * @return
     * <p>{@code true} - if the specified date is valid</p>
     * <p>{@code false} - otherwise</p>
     */
    private boolean isValidDateTime(String date) {
        try {
            LocalDateTime.parse(date);
        } catch (DateTimeParseException exception) {
            return false;
        }
        return true;
    }

    /**
     * <p>Checks if the data that is returned after parsing is corrupted.</p>
     * <p>For example, the array [D, 0, description, date] will return {@code false} while the array
     * [D, 0, description] will return {@code true} since {@code Deadline} is expected to have a
     * date.</p>
     * <p>Dates stored in the storage need to have a valid format, otherwise it is treated as corrupted.</p>
     *
     * @param parameters array that is returned after the information of a {@code Task} in storage is parsed
     * @return
     * <p>{@code true} - if data is corrupted</p>
     * <p>{@code false} - otherwise</p>
     */
    private boolean hasCorruptedData(String[] parameters) {
        switch (parameters[0]) {
        case "T":
            return (parameters.length < 3);
        case "D":
            // Fallthrough
        case "E":
            if (parameters.length < 4) {
                return true;
            }
            return !isValidDateTime(parameters[3]);
        default:
            return true;
        }
    }

    /**
     * Constructs a {@code Data} object from the information read from the storage.
     *
     * @param parameters <p>Variable argument that contains the information of a {@code Task}</p>
     *                   <p>For {@code ToDo} - Entries of the array represent the task type,
     *                   done status, and description.</p>
     *                   <p>For {@code Deadline, Event} - Entries of the array represent the task
     *                   type, done status, description, and the date and time.</p>
     * @throws DukeException If information read from the storage is corrupted
     */
    public Data(String ... parameters) throws DukeException {

        if (hasCorruptedData(parameters)) {
            throw new DukeException(CORRUPTED_DATA_ERROR);
        }

        this.parameters = parameters;
    }

    /**
     * <p>Converts a {@code Data} to a {@code Task}, based on {@code parameters}</p>
     * <p>One example of {@code parameters} can be [E, 1, attend wedding, 2021-09-01T19:00], where the entries
     * represent the task type, done status, description, and date and time (if any). These parameters are used
     * to construct the corresponding {@code Task}</p>
     *
     * @return {@code Task} corresponding to the {@code Data}
     * @throws DukeException if data is found to be corrupted
     */
    public Task toTask() throws DukeException {
        Task task;
        switch (parameters[0]) {
        case "T":
            task = new ToDo(parameters[2]);
            break;
        case "D":
            task = new Deadline(parameters[2], LocalDateTime.parse(parameters[3]));
            break;
        case "E":
            task = new Event(parameters[2], LocalDateTime.parse(parameters[3]));
            break;
        default:
            throw new DukeException(CORRUPTED_DATA_ERROR);
        }

        if (parameters[1].equals("1")) {
            task.setDone();
        }
        return task;
    }
}
