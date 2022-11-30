/*
 LICENSE:

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 Copyright (C) 2004, GanttProject Development Team
 */
package net.sourceforge.ganttproject.test.task;

import biz.ganttproject.core.time.CalendarFactory;
import biz.ganttproject.core.time.GanttCalendar;
import net.sourceforge.ganttproject.GanttGraphicArea;
import net.sourceforge.ganttproject.GanttProject;
import net.sourceforge.ganttproject.TestSetupHelper;
import net.sourceforge.ganttproject.action.task.TaskMoveUpAction;
import net.sourceforge.ganttproject.chart.gantt.GanttChartController;
import net.sourceforge.ganttproject.chart.mouse.ReorderTaskInteractions;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.task.TaskContainmentHierarchyFacade;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.algorithm.RecalculateTaskCompletionPercentageAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class TestTaskReordering extends TaskTestCase {
    public void testReorderingTasks1() {
        TaskManager manager = TestSetupHelper.newTaskManagerBuilder().build();
        Task task1 = manager.createTask();
        Task task2 = manager.createTask();
        Task task3 = manager.createTask();
        Task task4 = manager.createTask();

        List<Task> selectedTasks = new ArrayList<>();
        selectedTasks.add(task2);

        ReorderTaskInteractions t = new ReorderTaskInteractions(
                null, selectedTasks, manager, null, null
        );

        t.moveOffset(-1);

        Task[] expectedOrder = new Task[] {
          task2, task1, task3, task4
        };

        assertEquals(manager.getTasks(), expectedOrder);
    }
}
