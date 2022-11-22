/*
 LICENSE:

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 Copyright (C) 2004, GanttProject Development Team
 */
package net.sourceforge.ganttproject.test.task;


import biz.ganttproject.core.calendar.WeekendCalendarImpl;
import biz.ganttproject.core.time.CalendarFactory;
import net.sourceforge.ganttproject.TestSetupHelper;
import net.sourceforge.ganttproject.task.TaskInfo;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.Task;

import java.text.DateFormat;
import java.util.Locale;


public class TestTaskInfoImp extends TaskTestCase {
    static {
        new CalendarFactory() {
            {
                setLocaleApi(new LocaleApi() {
                    @Override
                    public Locale getLocale() {
                        return Locale.US;
                    }

                    @Override
                    public DateFormat getShortDateFormat() {
                        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
                    }
                });
            }
        };
    }

    public void testeGetTaskMainInfoHTML1() {
        Task task1 = getTaskManager().createTask();
        task1.setStart(TestSetupHelper.newFriday());// Friday
        task1.setEnd(TestSetupHelper.newTuesday()); // Tuesday
        task1.setName("task_1");
        TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_1<br>" +
                "Duration: 2 days<br>" +
                "Completion Percentage: 0%<br>" +
                "Remaining Time: 0 days<br> " +
                "Priority: normal<br>" +
                "Notes: </html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML() );
    }
    public void testGetTaskMainInfoHTML2() {
        Task task1 = getTaskManager().createTask();
        task1.setStart(CalendarFactory.createGanttCalendar(2022, 12, 25));
        task1.setEnd(CalendarFactory.createGanttCalendar(2023, 1, 2));
        task1.setName("task_1");
        TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_1<br>" +
                "Duration: 6 days<br>" +
                "Completion Percentage: 0%<br>" +
                "Remaining Time: 6 days<br> " +
                "Priority: normal<br>" +
                "Notes: </html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML() );
    }

    public void testGetTaskMainInfoHTML3() {
        Task task1 = getTaskManager().createTask();
        task1.setStart(CalendarFactory.createGanttCalendar(2022, 12, 25));
        task1.setEnd(CalendarFactory.createGanttCalendar(2023, 1, 2));
        task1.setName("task_1");
        task1.setNotes("test notes");
        TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_1<br>" +
                "Duration: 6 days<br>" +
                "Completion Percentage: 0%<br>" +
                "Remaining Time: 6 days<br> " +
                "Priority: normal<br>" +
                "Notes: test notes</html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML() );
    }

    public void testGetTaskMainInfoHTML4() {
        Task task1 = getTaskManager().createTask();
        task1.setStart(CalendarFactory.createGanttCalendar(2022, 12, 25));
        task1.setEnd(CalendarFactory.createGanttCalendar(2023, 1, 2));
        task1.setName("task_1");
        task1.setNotes("test notes test notes test notes test notes test notes test notes test notes test notes " +
                "test notes test notes test notes test notes test notes test notes test notes test notes test notes " +
                "test notes test notes test notes test notes test notes test notes test notes test notes test " +
                "notes test notes test notes test notes test notes test notes test notes test notes test notes test " +
                "notes test notes test notes test notes test notes test notes test notes test notes test notes test " +
                "notes test notes test notes test notes test notes test notes test notes test notes test notes test " +
                "notes test notes test notes test notes test notes test notes test notes test notes ");
        TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_1<br>" +
                "Duration: 6 days<br>" +
                "Completion Percentage: 0%<br>" +
                "Remaining Time: 6 days<br> " +
                "Priority: normal<br>" +
                "Notes: test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test <br><br><b>more...</b></html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML());
    }

    public void testGetTaskMainInfoHTML5() {
        Task task1 = getTaskManager().createTask();
        task1.setName("task_2");
        task1.setNotes("test notes");
        task1.setCompletionPercentage(20);
        TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_2<br>" +
                "Duration: 1 days<br>" +
                "Completion Percentage: 20%<br>" +
                "Remaining Time: 1 days<br> " +
                "Priority: normal<br>" +
                "Notes: test notes</html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML() );
    }
    @Override
    protected TaskManager newTaskManager() {
        return TestSetupHelper.newTaskManagerBuilder().withCalendar(myWeekendCalendar).build();
    }

    private WeekendCalendarImpl myWeekendCalendar = new WeekendCalendarImpl();



    /*public void testGetTaskMainInfoHTML() {
        Task task1 = getTaskManager().createTask();
        task1.setStart(TestSetupHelper.newFriday());// Friday
        task1.setEnd(TestSetupHelper.newTuesday()); // Tuesday
        //task1.setName("task_1");
        assertEquals("Unexpected length of task=" + task1
                + " which overlaps weekend", 2f, task1.getDuration().getLength(
                GregorianTimeUnitStack.DAY), 0.1);
        /*TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_1<br>" +
                "Duration: 6 days<br>" +
                "Completion Percentage: 0%<br>" +
                "Remaining Time: 6 days<br> " +
                "Priority: normal<br>" +
                "Notes: </html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML() );
    }*/
}
