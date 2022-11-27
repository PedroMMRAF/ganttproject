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
import biz.ganttproject.core.time.GanttCalendar;
import biz.ganttproject.core.time.TimeDurationImpl;
import net.sourceforge.ganttproject.TestSetupHelper;
import net.sourceforge.ganttproject.task.TaskImpl;
import net.sourceforge.ganttproject.task.TaskInfo;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.Task;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TestTaskInfoImp extends TaskTestCase {
    static {
        new CalendarFactory() {
            {
                setLocaleApi(new LocaleApi() {
                    public Locale getLocale() {
                        return new Locale("pt");
                    }

                    public DateFormat getShortDateFormat() {
                        return DateFormat.getDateInstance(DateFormat.SHORT, getLocale());
                    }
                });
            }
        };
    }

    GanttCalendar getNextDate(int weekDay) {
        GanttCalendar currentDate = CalendarFactory.createGanttCalendar();
        while (currentDate.get(Calendar.DAY_OF_WEEK) != weekDay) {
            currentDate.add(Calendar.DATE, 1);
        }
        return currentDate;
    }


    public void testGetTaskMainInfoHTML1() {
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
                "Notes: <b></b></html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML());
    }

    public void testGetTaskMainInfoHTML2() {
        Task task1 = createTask();
        task1.setStart(getNextDate(Calendar.TUESDAY));
        task1.setDuration(getTaskManager().createLength(4));
        task1.setName("task_1");
        TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_1<br>" +
                "Duration: 4 days<br>" +
                "Completion Percentage: 0%<br>" +
                "Remaining Time: 4 days<br> " +
                "Priority: normal<br>" +
                "Notes: <b></b></html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML());
    }

    public void testGetTaskMainInfoHTML3() {
        Task task1 = createTask();
        task1.setStart(getNextDate(Calendar.TUESDAY));
        task1.setDuration(getTaskManager().createLength(4));
        task1.setName("task_1");
        task1.setNotes("test notes");
        TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_1<br>" +
                "Duration: 4 days<br>" +
                "Completion Percentage: 0%<br>" +
                "Remaining Time: 4 days<br> " +
                "Priority: normal<br>" +
                "Notes: test notes<b></b></html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML());
    }

    public void testGetTaskMainInfoHTML4() {
        Task task1 = createTask();
        task1.setStart(getNextDate(Calendar.TUESDAY));
        task1.setDuration(getTaskManager().createLength(4));
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
                "Duration: 4 days<br>" +
                "Completion Percentage: 0%<br>" +
                "Remaining Time: 4 days<br> " +
                "Priority: normal<br>" +
                "Notes: test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test<br>notes test notes test notes test notes<br>" +
                "test notes test notes test notes test <br><b>more...</b></html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML());
    }

    public void testGetTaskMainInfoHTML5() {
        Task task1 = createTask();
        task1.setName("task_2");
        task1.setNotes("test notes");
        task1.setCompletionPercentage(20);
        TaskInfo taskInfo = task1.getTaskInfo();
        String info = "<html>Name: task_2<br>" +
                "Duration: 1 days<br>" +
                "Completion Percentage: 20%<br>" +
                "Remaining Time: 1 days<br> " +
                "Priority: normal<br>" +
                "Notes: test notes<b></b></html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML());
    }

    public void testGetTaskMainInfoHTML6() {
        Task task1 = getTaskManager().createTask();
        task1.setName("task_2");
        task1.setNotes("test notes");
        task1.setCompletionPercentage(20);
        GanttCalendar twoDaysAgo = CalendarFactory.createGanttCalendar();
        twoDaysAgo.add(Calendar.DATE, -1);
        task1.setStart(twoDaysAgo);
        GanttCalendar inTwoDays = CalendarFactory.createGanttCalendar();
        inTwoDays.add(Calendar.DATE, 2);
        task1.setEnd(inTwoDays);
        System.out.println(task1.getStart() + " " + task1.getEnd());
        TaskInfo taskInfo = task1.getTaskInfo();
        int duration;
        if (task1.getStart().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || task1.getEnd().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            duration = 2;
        else if (task1.getStart().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || task1.getEnd().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            duration = 1;
        else
            duration = 3;
        int remaining;
        if (task1.getEnd().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            remaining = 0;
        else if (task1.getStart().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || task1.getEnd().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            remaining = 1;
        else remaining = 2;

        String info = "<html>Name: task_2<br>" +
                "Duration: " + duration + " days<br>" +
                "Completion Percentage: 20%<br>" +
                "Remaining Time: " + remaining + " days<br> " +
                "Priority: normal<br>" +
                "Notes: test notes<b></b></html>";
        assertEquals(info, taskInfo.getTaskMainInfoHTML());
    }

    @Override
    protected TaskManager newTaskManager() {
        return TestSetupHelper.newTaskManagerBuilder().withCalendar(myWeekendCalendar).build();
    }

    private WeekendCalendarImpl myWeekendCalendar = new WeekendCalendarImpl();

}
