package net.sourceforge.ganttproject.chart.mouse;

import com.google.common.base.Predicate;
import net.sourceforge.ganttproject.GanttTree2;
import net.sourceforge.ganttproject.action.task.TaskActionBase;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskContainmentHierarchyFacade;
import net.sourceforge.ganttproject.task.TaskManager;

import java.awt.event.MouseEvent;
import java.util.List;

public class ReorderTaskInteractions extends MouseInteractionBase implements MouseInteraction {
    private static final int MOVE_OFFSET = 20;

    private final List<Task> myTasks;
    private final TaskManager myTaskManager;
    private final UIFacade myUIFacade;
    private final MouseEvent myMouseEvent;
    private final TaskContainmentHierarchyFacade myTaskHierarchy;
    private final GanttTree2 myTree;

    private int currentOffset;
    private int minOffset;
    private int maxOffset;

    public ReorderTaskInteractions(MouseEvent e, List<Task> tasks, TaskManager taskManager,
                                   TimelineFacade chartDateGrid, UIFacade uiFacade, GanttTree2 tree) {
        super(chartDateGrid.getDateAt(e.getX()), chartDateGrid);
        myUIFacade = uiFacade;
        myTasks = tasks;
        myTaskManager = taskManager;
        myTaskHierarchy = taskManager.getTaskHierarchy();
        myMouseEvent = e;
        myTree = tree;

        currentOffset = 0;
        maxOffset = Integer.MAX_VALUE;
        minOffset = Integer.MIN_VALUE;
    }

    public boolean isValidOffset(int offset) {
        if (currentOffset == offset || offset >= maxOffset || offset <= minOffset)
            return false;

        for (Task task : myTasks) {
            if (offset < currentOffset && myTaskHierarchy.getTaskIndex(task) <= 0) {
                minOffset = offset;
                return false;
            }
            if (offset > currentOffset && myTaskHierarchy.getTaskIndex(task) >= myTaskManager.getTaskCount() - 1) {
                maxOffset = offset;
                return false;
            }
        }

        return true;
    }

    @Override
    public void apply(MouseEvent event) {
        int offset = event.getY() - myMouseEvent.getY();
        offset /= MOVE_OFFSET;

        final int upDown = Integer.compare(offset, currentOffset);
        offset = currentOffset + upDown;

        if (!isValidOffset(offset))
            return;

        currentOffset = offset;

        myTree.commitIfEditing();

        moveIndex(upDown);

        myUIFacade.getTaskTree().makeVisible(myTasks.get(0));
        myUIFacade.getGanttChart().getProject().setModified();
    }

    public void moveIndex(int upDown) {
        for (Task task : myTasks) {
            final Task parent = myTaskHierarchy.getContainer(task);
            final int index = myTaskHierarchy.getTaskIndex(task) + upDown;

            myUIFacade.getTaskTree().applyPreservingExpansionState(task, new Predicate<Task>() {
                public boolean apply(Task t) {
                    myTaskHierarchy.move(t, parent, index);
                    return true;
                }
            });
        }
    }

    @Override
    public void finish() {
        final int sign = Integer.compare(currentOffset, 0);
        final int move = Math.abs(currentOffset);
        for (int i = 0; i < move; i++) {
            moveIndex(-sign);
        }

        myUIFacade.getUndoManager().undoableEdit("Task reordered", new Runnable() {
            @Override
            public void run() {
                myTree.commitIfEditing();
                for (int i = 0; i < move; i++) {
                    moveIndex(sign);
                }
                myUIFacade.getTaskTree().makeVisible(myTasks.get(0));
                myUIFacade.getGanttChart().getProject().setModified();
            }
        });
    }
}
