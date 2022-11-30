package net.sourceforge.ganttproject.chart.mouse;

import net.sourceforge.ganttproject.GanttTree2;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskContainmentHierarchyFacade;
import net.sourceforge.ganttproject.task.TaskManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class ReorderTaskInteractions implements MouseInteraction {
    private static final int MOVE_HEIGHT = 20;

    private final List<Task> myTasks;
    private final UIFacade myUIFacade;
    private final MouseEvent myMouseEvent;
    private final TaskContainmentHierarchyFacade myTaskHierarchy;
    private final GanttTree2 myTree;

    private int totalMovement;
    private int minOffset;
    private int maxOffset;

    public ReorderTaskInteractions(MouseEvent e, List<Task> tasks, TaskManager taskManager,
                                   UIFacade uiFacade, GanttTree2 tree) {
        myUIFacade = uiFacade;
        myTasks = tasks;
        myTaskHierarchy = taskManager.getTaskHierarchy();
        myMouseEvent = e;
        myTree = tree;

        myTasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return myTaskHierarchy.compareDocumentOrder(o1, o2);
            }
        });

        totalMovement = 0;

        minOffset = Integer.MIN_VALUE;
        maxOffset = Integer.MAX_VALUE;

        for (Task task : myTasks) {
            int maxIndex = getLastSiblingIndex(task);
            int index = myTaskHierarchy.getTaskIndex(task);
            minOffset = Math.max(- index, minOffset);
            maxOffset = Math.min(maxIndex - index, maxOffset);
        }
    }

    public int getLastSiblingIndex(Task task) {
        Task nextSibling;

        while ((nextSibling = myTaskHierarchy.getNextSibling(task)) != null) {
            task = nextSibling;
        }

        return myTaskHierarchy.getTaskIndex(task);
    }

    @Override
    public void apply(MouseEvent event) {
        int currentMovement = event.getY() - myMouseEvent.getY();
        currentMovement /= MOVE_HEIGHT;
        currentMovement = Math.max(minOffset, Math.min(maxOffset, currentMovement));

        if (currentMovement == totalMovement)
            return;

        final int finalCurrentMovement = currentMovement;

        myTree.commitIfEditing();

        myUIFacade.getUndoManager().undoableEdit("Task reordered", new Runnable() {
            @Override
            public void run() {
                moveOffset(finalCurrentMovement - totalMovement);
            }
        });

        myUIFacade.getTaskTree().makeVisible(myTasks.get(0));
        myUIFacade.getGanttChart().getProject().setModified();

        totalMovement = currentMovement;
    }

    public void moveOffset(int offset) {
        TwoWayIterator<Task> it = new TwoWayIterator<>(myTasks, offset > 0);
        while (it.hasNext()) {
            final Task task = it.next();
            final Task parent = myTaskHierarchy.getContainer(task);
            final int index = myTaskHierarchy.getTaskIndex(task) + offset;

            myTaskHierarchy.move(task, parent, index);
        }
    }

    @Override
    public void finish() {
    }

    @Override
    public void paint(Graphics g) {

    }
}
