package net.sourceforge.ganttproject.chart.mouse;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import net.sourceforge.ganttproject.GanttTree2;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskContainmentHierarchyFacade;
import net.sourceforge.ganttproject.task.TaskManager;

import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class ReorderTaskInteractions extends MouseInteractionBase implements MouseInteraction {
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
                                   TimelineFacade chartDateGrid, UIFacade uiFacade, GanttTree2 tree) {
        super(chartDateGrid.getDateAt(e.getX()), chartDateGrid);
        myUIFacade = uiFacade;
        myTasks = tasks;
        myTaskHierarchy = taskManager.getTaskHierarchy();
        myMouseEvent = e;
        myTree = tree;

        myTasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return myTaskHierarchy.getTaskIndex(o1) - myTaskHierarchy.getTaskIndex(o2);
            }
        });

        totalMovement = 0;

        minOffset = Integer.MAX_VALUE;
        maxOffset = Integer.MIN_VALUE;

        for (Task task : myTasks) {
            int index = myTaskHierarchy.getTaskIndex(task);
            minOffset = Math.min(index, minOffset);
            maxOffset = Math.max(index, maxOffset);
        }

        minOffset = -minOffset;
        maxOffset = taskManager.getTaskCount() - 1 - maxOffset;
    }

    @Override
    public void apply(MouseEvent event) {
        int currentMovement = event.getY() - myMouseEvent.getY();
        currentMovement /= MOVE_HEIGHT;
        currentMovement = Math.max(minOffset, Math.min(maxOffset, currentMovement));

        if (currentMovement == totalMovement)
            return;

        final int finalCurrentMovement = currentMovement;

        myUIFacade.getUndoManager().undoableEdit("Task reordered", new Runnable() {
            @Override
            public void run() {
                moveOffset(finalCurrentMovement - totalMovement);
            }
        });

        totalMovement = currentMovement;
    }

    public void moveOffset(int offset) {
        myTree.commitIfEditing();

        TwoWayIterator<Task> it = new TwoWayIterator<>(myTasks, offset > 0);
        while (it.hasNext()) {
            final Task task = it.next();
            final Task parent = myTaskHierarchy.getContainer(task);
            final int index = myTaskHierarchy.getTaskIndex(task) + offset;
            myTaskHierarchy.move(task, parent, index);
        }

        myUIFacade.getTaskTree().makeVisible(myTasks.get(0));
        myUIFacade.getGanttChart().getProject().setModified();
    }

    @Override
    public void finish() {
    }
}
