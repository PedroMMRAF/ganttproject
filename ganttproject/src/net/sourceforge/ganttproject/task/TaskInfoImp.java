package net.sourceforge.ganttproject.task;

import biz.ganttproject.core.time.CalendarFactory;
import biz.ganttproject.core.time.GanttCalendar;
import biz.ganttproject.core.time.TimeDuration;
import net.sourceforge.ganttproject.language.GanttLanguage;

import java.util.Calendar;

/**
 * Class that implements the TaskInfo interface
 */
public class TaskInfoImp implements TaskInfo {
    private static final int WRAP_NOTES_WIDTH = 40;
    private static final int MAX_OUTPUT_NOTES_LENGTH = 500;
    private GanttCalendar start;
    private GanttCalendar end;
    private TimeDuration length;
    private int completionPercentage;
    private String name;
    private String notes;
    private Task.Priority priority;

    public TaskInfoImp() {
        this.start = null;
        this.end = null;
        this.length = null;
        this.completionPercentage = 0;
        this.name = null;
        this.notes = null;
        this.priority = Task.Priority.NORMAL;
    }

    @Override
    public void setStartDate(GanttCalendar start) {
        this.start = start;
    }

    @Override
    public void setEndDate(GanttCalendar end) {
        this.end = end;
    }

    @Override
    public void setDuration(TimeDuration length) {
        this.length = length;
    }

    @Override
    public void setCompletionPercentage(int percentage) {
        this.completionPercentage = percentage;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public void addNotes(String notes) {
        this.notes += notes;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPriority(Task.Priority priority) {
        this.priority = priority;
    }

    @Override
    public int getDuration() {
        return (int) this.length.getValue();
    }

    @Override
    public int getCompletionPercentage() {
        return this.completionPercentage;
    }

    @Override
    public int getRemainingTime() {
        GanttCalendar currentDate = CalendarFactory.createGanttCalendar();
        if (currentDate.getTimeInMillis() < this.start.getTimeInMillis())
            currentDate.setTimeInMillis(this.start.getTimeInMillis());
        GanttCalendar endDate = CalendarFactory.createGanttCalendar();
        endDate.setTimeInMillis(getEnd().getTimeInMillis());
        int numberOfDays = 0;
        while (currentDate.before(endDate)) {
            if ((Calendar.SATURDAY != currentDate.get(Calendar.DAY_OF_WEEK))
                    && (Calendar.SUNDAY != currentDate.get(Calendar.DAY_OF_WEEK))) {
                numberOfDays++;
            }
            currentDate.add(Calendar.DATE, 1);
        }
        return numberOfDays;
    }

    @Override
    public String getPriority() {
        return this.priority.getLowerString();
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public String getTaskMainInfoHTML() {
        String outPutNotes = getNotes();
        boolean notesAreBiggerThenLimit = false;
        if (outPutNotes.length() > MAX_OUTPUT_NOTES_LENGTH) {
            outPutNotes = outPutNotes.substring(0, MAX_OUTPUT_NOTES_LENGTH) + "\n";
            notesAreBiggerThenLimit = true;
        }
        outPutNotes = wrapNotes(outPutNotes);
        String info = "Name: " + getName() + "\n" +
                "Duration: " + getDuration() + " days\n" +
                "Completion Percentage: " + getCompletionPercentage() + "%\n" +
                "Remaining Time: " + getRemainingTime() + " days\n " +
                "Priority: " + getPriority() + "\n" +
                "Notes: " + outPutNotes;
        info = GanttLanguage.getInstance().formatText("task.infoNotesTooltip.pattern",
                info.replace("\n", "<br>"), notesAreBiggerThenLimit ? "more..." : "");
        return info;
    }


    /**
     * adds a new break line whenever the length of a note line exceeds <code>WRAP_NOTES_WIDTH</code>
     *
     * @return a string with the processed notes
     */
    private String wrapNotes(String notes) {
        StringBuilder result = new StringBuilder();
        int lastDelimPos = 0;
        for (String token : notes.split(" ", -1)) {
            if (token.length() > WRAP_NOTES_WIDTH) {
                result.append(" ").append(token);
                for (int i = lastDelimPos; i < result.length(); i += WRAP_NOTES_WIDTH) {
                    result.insert(i + 1, '\n');
                    lastDelimPos = i + 1;
                }
            } else if (result.length() - lastDelimPos + token.length() > WRAP_NOTES_WIDTH) {
                lastDelimPos = result.length() + 1;
                result.append("\n").append(token);
            } else {
                result.append((result.length() == 0) ? "" : " ").append(token);
            }
        }
        return result.toString();
    }

    @Override
    public GanttCalendar getEnd() {
        return this.end;
    }

}
