/*
Copyright 2003-2012 Dmitry Barashev, GanttProject Team

This file is part of GanttProject, an opensource project management tool.

GanttProject is free software: you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

GanttProject is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with GanttProject.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.ganttproject.task;

import biz.ganttproject.core.time.GanttCalendar;
import biz.ganttproject.core.time.TimeDuration;

public interface TaskInfo {
    /**
     * change task start date to <code>start</code>
     * @param start new start date
     */
    void setStartDate(GanttCalendar start);
    /**
     * change task end date to <code>end</code>
     * @param end new end date
     */
    void setEndDate(GanttCalendar end);
    /**
     * change task duration to <code>length</code>
     * @param length task duration
     */
    void setDuration(TimeDuration length);
    /**
     * change task completion percentage to <code>percentage</code>
     * @param percentage completion percentage
     */
    void setCompletionPercentage(int percentage);
    /**
     * change task notes to <code>notes</code>
     * @param notes new notes
     */
    void setNotes(String notes);
    /**
     * add <code>notes</code> to old notes
     * @param notes notes to add
     */
    void addNotes(String notes);

    /**
     *
     * @return the task name
     */
    String getName();
    /**
     * change task name to <code>name</code>
     * @param name new task name
     */
    void setName(String name);
    /**
     * change task priority to <code>priority</code>
     * @param priority new task priority
     */
    void setPriority(Task.Priority priority);

    /**
     *
     * @return the task duration
     */
    int getDuration();

    /**
     *
     * @return the task completion percentage
     */
    int getCompletionPercentage();

    /**
     *
     * @return the remaining time to finish the task
     */
    int getRemainingTime();

    /**
     *
     * @return the task priority
     */
    String getPriority();

    /**
     *
     * @return task notes
     */
    String getNotes();

    /**
     *
     * @return  the most relevant task information in html format
     */
    String getTaskMainInfoHTML();

    /**
     *
     * @return the deadline to finish the task
     */
    GanttCalendar getEnd();

}
