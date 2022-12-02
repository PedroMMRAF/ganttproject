/*
 LICENSE:

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 Copyright (C) 2004, GanttProject Development Team
 */
package net.sourceforge.ganttproject.chart.mouse;

import net.sourceforge.ganttproject.DummyUIFacade;
import net.sourceforge.ganttproject.TestSetupHelper;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.test.task.TaskTestCase;

import java.util.*;
import java.util.List;

public class TestTaskReordering extends TaskTestCase {
    public void expectTaskOrder(TaskManager manager, Task[] expectedOrder) {
        List<Task> actualOrder = manager.getTaskHierarchy().getTasksInDocumentOrder();

        for (int i = 0; i < expectedOrder.length; i++) {
            //System.out.printf("%s -> %s\n", actualOrder.get(i), expectedOrder[i]);
            assertEquals(actualOrder.get(i), expectedOrder[i]);
        }
    }

    /**
     * Creates a simple ReorderTaskInteraction to reorder tasks with
     * @param manager the task manager
     * @param selectedTasks the tasks to move
     */
    public ReorderTaskInteractions startReorder(TaskManager manager, Task[] selectedTasks) {
        return new ReorderTaskInteractions(
                0, Arrays.asList(selectedTasks), manager, new DummyUIFacade()
        );
    }

    /**
     * Moving a task up the hierarchy
     */
    public void testTaskReordering1() {
        TaskManager manager = TestSetupHelper.newTaskManagerBuilder().build();
        Task task1 = manager.createTask();
        Task task2 = manager.createTask();
        Task task3 = manager.createTask();
        Task task4 = manager.createTask();

        // Reorder selected tasks
        ReorderTaskInteractions interaction = startReorder(manager, new Task[] {
                task2
        });
        interaction.reorderToIndexOffset(-1);

        // Check task order
        expectTaskOrder(manager, new Task[] {
                task2, task1, task3, task4
        });
    }

    /**
     * Tests moving the mouse beyond the offset limit
     * (in this case moving 3 where it should at most move 2)
     */
    public void testTaskReordering2() {
        TaskManager manager = TestSetupHelper.newTaskManagerBuilder().build();
        Task task1 = manager.createTask();
        Task task2 = manager.createTask();
        Task task3 = manager.createTask();
        Task task4 = manager.createTask();

        // Reorder selected tasks
        ReorderTaskInteractions interaction = startReorder(manager, new Task[] {
                task2
        });
        interaction.reorderToIndexOffset(3);

        // Check task order
        expectTaskOrder(manager, new Task[] {
                task1, task3, task4, task2
        });
    }

    /**
     * Tests moving the mouse twice on the same interaction
     * (moving up and down on the same held combination).
     * The result will essentially ignore the reordering done by the first call.
     */
    public void testTaskReordering3() {
        TaskManager manager = TestSetupHelper.newTaskManagerBuilder().build();
        Task task1 = manager.createTask();
        Task task2 = manager.createTask();
        Task task3 = manager.createTask();
        Task task4 = manager.createTask();

        // Reorder selected tasks
        ReorderTaskInteractions interaction = startReorder(manager, new Task[] {
                task2
        });
        interaction.reorderToIndexOffset(-1);
        interaction.reorderToIndexOffset(1);

        // Check task order
        expectTaskOrder(manager, new Task[] {
                task1, task3, task2, task4
        });
    }

    /**
     * Moving multiple tasks up the hierarchy
     */
    public void testTaskReordering4() {
        TaskManager manager = TestSetupHelper.newTaskManagerBuilder().build();
        Task task1 = manager.createTask();
        Task task2 = manager.createTask();
        Task task3 = manager.createTask();
        Task task4 = manager.createTask();
        Task task5 = manager.createTask();
        Task task6 = manager.createTask();

        // Reorder selected tasks
        ReorderTaskInteractions interaction = startReorder(manager, new Task[] {
                task2, task4, task5
        });
        interaction.reorderToIndexOffset(-1);

        // Check task order
        expectTaskOrder(manager, new Task[] {
                task2, task1, task4, task5, task3, task6
        });
    }

    /**
     * Moving a sub task
     */
    public void testTaskReordering5() {
        TaskManager manager = TestSetupHelper.newTaskManagerBuilder().build();
        Task task0 = manager.createTask();
        Task task1 = manager.createTask();
        Task task2 = manager.createTask();
        Task task3 = manager.createTask();
        Task task4 = manager.createTask();
        Task task5 = manager.createTask();
        Task task6 = manager.createTask();
        Task task7 = manager.createTask();

        manager.getTaskHierarchy().move(task4, task3);
        manager.getTaskHierarchy().move(task5, task3);
        manager.getTaskHierarchy().move(task6, task3);
        manager.getTaskHierarchy().move(task7, task3);

        // Reorder selected tasks
        ReorderTaskInteractions interaction = startReorder(manager, new Task[] {
                task6
        });
        interaction.reorderToIndexOffset(-4);

        // Check task order
        expectTaskOrder(manager, new Task[] {
                task0, task1, task2, task3, task6, task4, task5, task7
        });
    }

    /**
     * Moving a task and a sub task
     */
    public void testTaskReordering6() {
        TaskManager manager = TestSetupHelper.newTaskManagerBuilder().build();
        Task task0 = manager.createTask();
        Task task1 = manager.createTask();
        Task task2 = manager.createTask();
        Task task3 = manager.createTask();
        Task task4 = manager.createTask();
        Task task5 = manager.createTask();
        Task task6 = manager.createTask();
        Task task7 = manager.createTask();

        manager.getTaskHierarchy().move(task4, task3);
        manager.getTaskHierarchy().move(task5, task3);
        manager.getTaskHierarchy().move(task6, task3);
        manager.getTaskHierarchy().move(task7, task3);

        // Reorder selected tasks
        ReorderTaskInteractions interaction = startReorder(manager, new Task[] {
                task0, task6
        });
        interaction.reorderToIndexOffset(3);

        // Check task order
        expectTaskOrder(manager, new Task[] {
                task1, task0, task2, task3, task4, task5, task7, task6
        });
    }
}
