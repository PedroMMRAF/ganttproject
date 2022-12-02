package net.sourceforge.ganttproject.chart.mouse;

import net.sourceforge.ganttproject.GanttTree2;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskContainmentHierarchyFacade;
import net.sourceforge.ganttproject.task.TaskManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class ReorderTaskInteractions implements MouseInteraction {
    public static final int MOVE_HEIGHT = 20;

    private final List<Task> mySelectedTasks;
    private final UIFacade myUIFacade;
    private final int myStartMousePos;
    private final TaskContainmentHierarchyFacade myTaskHierarchy;

    private int currentIndexOffset;
    private int minIndexOffset;
    private int maxIndexOffset;

    /**
     * Mouse Interaction related to reordering tasks in the hierarchy.
     *
     * @param startMousePos the mouse's first start position
     * @param selectedTasks list of selected tasks
     * @param taskManager the project's task manager
     * @param uiFacade the project's UI
     */
    public ReorderTaskInteractions(int startMousePos, List<Task> selectedTasks,
                                   TaskManager taskManager, UIFacade uiFacade) {
        myUIFacade = uiFacade;
        mySelectedTasks = selectedTasks;
        myTaskHierarchy = taskManager.getTaskHierarchy();
        myStartMousePos = startMousePos;

        mySelectedTasks.sort(myTaskHierarchy::compareDocumentOrder);

        currentIndexOffset = 0;

        minIndexOffset = Integer.MIN_VALUE;
        maxIndexOffset = Integer.MAX_VALUE;

        for (Task task : mySelectedTasks) {
            int maxIndex = getContainerSize(task) - 1;
            int index = myTaskHierarchy.getTaskIndex(task);
            minIndexOffset = Math.max(- index, minIndexOffset);
            maxIndexOffset = Math.min(maxIndex - index, maxIndexOffset);
        }
    }

    /**
     * Gets the size of the container of a task,
     * regardless of implementation.
     *
     * @param task a nested task
     * @return the size of the container the given task is in
     */
    public int getContainerSize(Task task) {
        Task nextSibling;

        while ((nextSibling = myTaskHierarchy.getNextSibling(task)) != null) {
            task = nextSibling;
        }

        return myTaskHierarchy.getTaskIndex(task) + 1;
    }

    @Override
    public void apply(MouseEvent event) {
        reorderToIndexOffset((myStartMousePos - event.getY()) / MOVE_HEIGHT);
    }

    /**
     * Reorders tasks given offset to move by,
     * taking into consideration that the mouse moves
     * in the opposite direction of task indices.
     *
     * @param newIndexOffset the offset relative to the starting conditions
     */
    public void reorderToIndexOffset(int newIndexOffset) {
        newIndexOffset = Math.max(minIndexOffset, Math.min(maxIndexOffset, newIndexOffset));

        int movedIndexOffset = newIndexOffset - currentIndexOffset;
        currentIndexOffset = newIndexOffset;

        if (movedIndexOffset == 0) {
            return;
        }

        TwoWayIterator<Task> it = new TwoWayIterator<>(mySelectedTasks, movedIndexOffset > 0);

        while (it.hasNext()) {
            final Task task = it.next();
            final Task parent = myTaskHierarchy.getContainer(task);
            final int index = myTaskHierarchy.getTaskIndex(task) + movedIndexOffset;

            if (myUIFacade.getTaskTree() instanceof GanttTree2)
                ((GanttTree2)myUIFacade.getTaskTree()).commitIfEditing();

            myUIFacade.getUndoManager().undoableEdit("Task reordered", new Runnable() {
                @Override
                public void run() {
                    myTaskHierarchy.move(task, parent, index);
                }
            });

            myUIFacade.getTaskTree().makeVisible(mySelectedTasks.get(0));
            myUIFacade.getGanttChart().getProject().setModified();
        }
    }

    @Override
    public void finish() {
    }

    @Override
    public void paint(Graphics g) {
    }
}
