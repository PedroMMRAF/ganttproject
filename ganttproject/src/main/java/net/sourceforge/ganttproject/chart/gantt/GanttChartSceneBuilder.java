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
package net.sourceforge.ganttproject.chart.gantt;

import biz.ganttproject.core.chart.canvas.Canvas;
import biz.ganttproject.core.chart.canvas.Canvas.Polygon;
import biz.ganttproject.core.chart.canvas.Canvas.Rectangle;
import biz.ganttproject.core.chart.grid.OffsetList;
import biz.ganttproject.core.chart.render.AlphaRenderingOption;
import biz.ganttproject.core.chart.render.ShapeConstants;
import biz.ganttproject.core.chart.render.ShapePaint;
import biz.ganttproject.core.chart.scene.gantt.DependencySceneBuilder;
import biz.ganttproject.core.chart.scene.gantt.TaskActivitySceneBuilder;
import biz.ganttproject.core.chart.scene.gantt.TaskLabelSceneBuilder;
import biz.ganttproject.core.option.DefaultEnumerationOption;
import biz.ganttproject.core.option.EnumerationOption;
import biz.ganttproject.core.option.GPOption;
import biz.ganttproject.core.option.GPOptionGroup;
import biz.ganttproject.core.time.TimeDuration;
import biz.ganttproject.core.time.TimeUnit;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.sourceforge.ganttproject.GanttPreviousStateTask;
import net.sourceforge.ganttproject.chart.ChartModelImpl;
import net.sourceforge.ganttproject.chart.ChartOptionGroup;
import net.sourceforge.ganttproject.chart.ChartRendererBase;
import net.sourceforge.ganttproject.chart.MilestoneTaskFakeActivity;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.TaskActivitiesAlgorithm;
import net.sourceforge.ganttproject.task.TaskActivity;
import net.sourceforge.ganttproject.task.TaskContainmentHierarchyFacade;
import net.sourceforge.ganttproject.task.TaskImpl;
import net.sourceforge.ganttproject.task.TaskProperties;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.sourceforge.ganttproject.chart.gantt.DependencySceneApiImplKt.splitOnBounds;

/**
 * Renders task rectangles, dependency lines and all task-related text strings
 * in the gantt chart
 */
public class GanttChartSceneBuilder extends ChartRendererBase {
  private ChartModelImpl myModel;

  private GPOptionGroup myLabelOptions;

  private final TaskLabelSceneBuilder<Task> myLabelsRenderer;

  private TaskActivitySceneBuilder.TaskApi<Task, TaskActivity> myTaskApi = new TaskActivitySceneBuilder.TaskApi<Task, TaskActivity>() {
    @Override
    public boolean isFirst(TaskActivity activity) {
      return activity.isFirst();
    }
    @Override
    public boolean isLast(TaskActivity activity) {
      return activity.isLast();
    }
    @Override
    public boolean isVoid(TaskActivity activity) {
      return activity.getIntensity() == 0f;
    }
    @Override
    public boolean isCriticalTask(Task task) {
      return myModel.getChartUIConfiguration().isCriticalPathOn() && task.isCritical();
    }
    @Override
    public boolean isProjectTask(Task task) {
      return task.isProjectTask();
    }
    @Override
    public boolean isMilestone(Task task) {
      return ((TaskImpl)task).isLegacyMilestone();
    }
    @Override
    public boolean hasNestedTasks(Task task) {
      return getChartModel().getTaskManager().getTaskHierarchy().hasNestedTasks(task);
    }
    @Override
    public Color getColor(Task task) {
      return task.getColor();
    }
    @Override
    public ShapePaint getShapePaint(Task task) {
      if (task.getShape() == null) {
        return ShapeConstants.TRANSPARENT;
      }
      return task.getShape();
    }
    @Override
    public boolean hasNotes(Task task) {
      return !Strings.isNullOrEmpty(task.getNotes());
    }
  };

  class TaskActivityChartApi implements TaskActivitySceneBuilder.ChartApi {
    TaskActivityChartApi() {
    }
    @Override
    public Date getChartStartDate() {
      return myModel.getOffsetAnchorDate();
    }
    @Override
    public Date getEndDate() {
      return getChartModel().getEndDate();
    }
    @Override
    public OffsetList getBottomUnitOffsets() {
      return getChartModel().getBottomUnitOffsets();
    }
    @Override
    public int getRowHeight() {
      return calculateRowHeight();
    }
    @Override
    public int getBarHeight() {
      return getRectangleHeight();
    }
    @Override
    public int getViewportWidth() {
      return myModel.getBounds().width;
    }
    @Override
    public AlphaRenderingOption getWeekendOpacityOption() {
      return myModel.getChartUIConfiguration().getWeekendAlphaValue();
    }
  }

  private final TaskActivitySceneBuilder<Task, TaskActivity> myTaskActivityRenderer;
  private final TaskActivitySceneBuilder<Task, TaskActivity> myBaselineActivityRenderer;

  private final Canvas myLabelsLayer;

  private TaskActivityChartApi myChartApi;

  public GanttChartSceneBuilder(ChartModelImpl model) {
    super(model);
    this.myModel = model;
    getPrimitiveContainer().setOffset(0, model.getChartUIConfiguration().getHeaderHeight());
    getPrimitiveContainer().newLayer();
    getPrimitiveContainer().newLayer();
    getPrimitiveContainer().newLayer();
    myLabelsLayer = getPrimitiveContainer().newLayer();

    List<String> taskProperties = Lists.newArrayList("", "id", "taskDates", "name", "length", "advancement", "coordinator", "resources", "predecessors");
    final DefaultEnumerationOption<String> topLabelOption = new DefaultEnumerationOption<String>("taskLabelUp", taskProperties);
    final DefaultEnumerationOption<String> bottomLabelOption = new DefaultEnumerationOption<String>("taskLabelDown", taskProperties);
    final DefaultEnumerationOption<String> leftLabelOption = new DefaultEnumerationOption<String>("taskLabelLeft", taskProperties);
    final DefaultEnumerationOption<String> rightLabelOption = new DefaultEnumerationOption<String>("taskLabelRight", taskProperties);

    myLabelsRenderer = new TaskLabelSceneBuilder<Task>(new TaskLabelSceneBuilder.TaskApi<Task>() {
      TaskProperties myLabelFormatter = new TaskProperties(getChartModel().getTimeUnitStack());

      @Override
      public Object getProperty(Task task, String propertyID) {
        return myLabelFormatter.getProperty(task, propertyID);
      }
    }, new TaskLabelSceneBuilder.InputApi() {
      @Override
      public EnumerationOption getTopLabelOption() {
        return topLabelOption;
      }

      @Override
      public EnumerationOption getBottomLabelOption() {
        return bottomLabelOption;
      }

      @Override
      public EnumerationOption getLeftLabelOption() {
        return leftLabelOption;
      }

      @Override
      public EnumerationOption getRightLabelOption() {
        return rightLabelOption;
      }

      @Override
      public int getFontSize() {
        return getChartModel().getChartUIConfiguration().getBaseFontSize();
      }
    }, myLabelsLayer);
    myLabelOptions = new ChartOptionGroup("ganttChartDetails",
        new GPOption[] {topLabelOption, bottomLabelOption, leftLabelOption, rightLabelOption},
        model.getOptionEventDispatcher());

    myChartApi = new TaskActivityChartApi();
    myTaskActivityRenderer = createTaskActivitySceneBuilder(getPrimitiveContainer(), myChartApi,
        new TaskActivitySceneBuilder.Style(0));
    myBaselineActivityRenderer = createTaskActivitySceneBuilder(
        getPrimitiveContainer().getLayer(2), new TaskActivityChartApi(),
        new TaskActivitySceneBuilder.Style(getRectangleHeight()));
  }

  private List<Task> getVisibleTasks() {
    return ((ChartModelImpl) getChartModel()).getVisibleTasks();
  }

  /**
    This class splits all tasks into 4 groups. One group is pure virtual: it contains
    tasks which are hidden under some collapsed parent and hence are just filtered out.
    The remaining groups are: tasks which are shown in the chart viewport, tasks above the viewport
    and tasks below the viewport. We need tasks outside the viewport because we want to show
    dependency lines which may connect them with tasks inside the viewport.
   */
  static class VerticalPartitioning {
    final List<Task> aboveViewport = Lists.newArrayList();
    final List<Task> belowViewport = Lists.newArrayList();
    final List<Task> insideViewport;

    /**
     * @param tasksInsideViewport partition with tasks inside viewport, with hidden tasks already filtered.
     *        Tasks must be ordered in their document order.
     */
    VerticalPartitioning(List<Task> tasksInsideViewport) {
      insideViewport = tasksInsideViewport;
    }

    /**
     * Builds the remaining partitions.
     *
     * In this method we iterate through *all* the tasks in their document order. If we find some
     * collapsed task then we filter out its children. Until we reach the first task in the vieport
     * partition,  we're above the viewport, then we skip the viewport partition and proceed to
     * below viewport
     */
    void build(TaskContainmentHierarchyFacade containment) {
      List<Task> tasksInDocumentOrder = containment.getTasksInDocumentOrder();
      final Task firstVisible = insideViewport.isEmpty() ? null : insideViewport.get(0);
      final Task lastVisible = insideViewport.isEmpty() ? null : insideViewport.get(insideViewport.size() - 1);
      List<Task> addTo = aboveViewport;

      Task collapsedRoot = null;
      for (Task nextTask : tasksInDocumentOrder) {
        if (addTo == null) {
          if (nextTask.equals(lastVisible)) {
            addTo = belowViewport;
          }
          continue;
        }
        if (nextTask.equals(firstVisible)) {
          addTo = null;
          continue;
        }

        if (collapsedRoot != null) {
          if (containment.areUnrelated(nextTask, collapsedRoot)) {
            collapsedRoot = null;
          } else {
            continue;
          }
        }
        addTo.add(nextTask);

        if (!nextTask.getExpand()) {
          assert collapsedRoot == null : "All tasks processed prior to this one must be expanded";
          collapsedRoot = nextTask;
        }
      }
    }
  }

  @Override
  public void render() {
    getPrimitiveContainer().clear();
    getPrimitiveContainer().getLayer(0).clear();
    getPrimitiveContainer().getLayer(1).clear();
    getPrimitiveContainer().getLayer(2).clear();
    getPrimitiveContainer().setOffset(0,
        myModel.getChartUIConfiguration().getHeaderHeight() - myModel.getVerticalOffset());
    getPrimitiveContainer().getLayer(2).setOffset(0,
        myModel.getChartUIConfiguration().getHeaderHeight() - myModel.getVerticalOffset());

    VerticalPartitioning vp = new VerticalPartitioning(getVisibleTasks());
    vp.build(getChartModel().getTaskManager().getTaskHierarchy());
    OffsetList defaultUnitOffsets = getChartModel().getDefaultUnitOffsets();

    renderVisibleTasks(getVisibleTasks(), defaultUnitOffsets);
    renderTasksAboveAndBelowViewport(vp.aboveViewport, vp.belowViewport, defaultUnitOffsets);
    renderDependencies();
  }


  private void renderDependencies() {
    DependencySceneBuilder.ChartApi chartApi = new DependencySceneBuilder.ChartApi() {
      @Override
      public int getBarHeight() {
        return getRectangleHeight();
      }
    };
    DependencySceneBuilder.TaskApi<Task, BarChartConnectorImpl> taskApi = new DependencySceneTaskApi(
        myModel.getVisibleTasks(), getChartModel().getStartDate(), myChartApi.getEndDate());
    DependencySceneBuilder<Task, BarChartConnectorImpl> dependencyRenderer = new DependencySceneBuilder<>(
        getPrimitiveContainer(), getPrimitiveContainer().getLayer(1), taskApi, chartApi);
    dependencyRenderer.build();
  }

  private void renderTasksAboveAndBelowViewport(List<Task> tasksAboveViewport, List<Task> tasksBelowViewport,
      OffsetList defaultUnitOffsets) {
    for (Task nextAbove : tasksAboveViewport) {
      List<TaskActivity> activities = /*nextAbove.isMilestone() ? Collections.<TaskActivity> singletonList(new MilestoneTaskFakeActivity(
          nextAbove)) : */nextAbove.getActivities();
      for (Canvas.Shape s : renderActivities(-1, nextAbove, activities, defaultUnitOffsets, false)) {
        s.setVisible(false);
      }
    }
    for (Task nextBelow : tasksBelowViewport) {
      List<TaskActivity> activities = /*nextBelow.isMilestone() ? Collections.<TaskActivity> singletonList(new MilestoneTaskFakeActivity(
          nextBelow)) : */nextBelow.getActivities();
      List<Polygon> rectangles = renderActivities(getVisibleTasks().size() + 1, nextBelow, activities,
          defaultUnitOffsets, false);
      for (Polygon nextRectangle : rectangles) {
        nextRectangle.setVisible(false);
      }
    }
  }

  private void renderVisibleTasks(List<Task> visibleTasks, OffsetList defaultUnitOffsets) {
    List<Polygon> boundPolygons = Lists.newArrayList();
    int rowNum = 0;
    for (Task t : visibleTasks) {
      boundPolygons.clear();
      List<TaskActivity> activities = t.getActivities();
      activities = splitOnBounds(activities, getChartModel().getStartDate(), myChartApi.getEndDate());
      List<Polygon> rectangles = renderActivities(rowNum, t, activities, defaultUnitOffsets, true);
      for (Polygon p : rectangles) {
        if (p.getModelObject() != null) {
          boundPolygons.add(p);
        }
      }
      renderLabels(boundPolygons);
      renderBaseline(t, rowNum, defaultUnitOffsets);
      rowNum++;
      Canvas.Line nextLine = getPrimitiveContainer().createLine(0, rowNum * getRowHeight(),
          (int) getChartModel().getBounds().getWidth(), rowNum * getRowHeight());
      nextLine.setForegroundColor(Color.GRAY);
    }
  }

  private int getRowHeight() {
    return calculateRowHeight();
  }

  private void renderBaseline(Task t, int rowNum, OffsetList defaultUnitOffsets) {
    TaskActivitiesAlgorithm alg = new TaskActivitiesAlgorithm(getCalendar());
    List<GanttPreviousStateTask> baseline = myModel.getBaseline();
    if (baseline != null) {
      for (GanttPreviousStateTask taskBaseline : baseline) {
        if (taskBaseline.getId() == t.getTaskID()) {
          Date startDate = taskBaseline.getStart().getTime();
          TimeDuration duration = getChartModel().getTaskManager().createLength(taskBaseline.getDuration());
          Date endDate = getCalendar().shiftDate(startDate, duration);
          if (endDate.equals(t.getEnd().getTime())) {
            return;
          }
          List<String> styles = new ArrayList<String>();
          if (t.isMilestone()) {
            styles.add("milestone");
          }
          if (endDate.compareTo(t.getEnd().getTime()) < 0) {
            styles.add("later");
          } else {
            styles.add("earlier");
          }
          List<TaskActivity> baselineActivities = new ArrayList<TaskActivity>();
          if (t.isMilestone()) {
            baselineActivities.add(new MilestoneTaskFakeActivity(t, startDate, endDate));
          } else {
            alg.recalculateActivities(t, baselineActivities, startDate, endDate);
          }
          List<Polygon> baselineRectangles = myBaselineActivityRenderer.renderActivities(rowNum, baselineActivities,
              defaultUnitOffsets);
          for (int i = 0; i < baselineRectangles.size(); i++) {
            Polygon r = baselineRectangles.get(i);
            r.setStyle("previousStateTask");
            for (String s : styles) {
              r.addStyle(s);
            }
            if (i == 0) {
              r.addStyle("start");
            }
            if (i == baselineRectangles.size() - 1) {
              r.addStyle("end");
            }
          }
          return;
        }
      }
    }
  }

  private static Predicate<Polygon> REMOVE_SUPERTASK_ENDINGS = new Predicate<Polygon>() {
    @Override
    public boolean apply(@Nullable Polygon shape) {
      return !shape.hasStyle("task.ending");
    }
  };
  private List<Polygon> renderActivities(final int rowNum, Task t, List<TaskActivity> activities,
      OffsetList defaultUnitOffsets, boolean areVisible) {
    List<Polygon> rectangles = myTaskActivityRenderer.renderActivities(rowNum, activities, defaultUnitOffsets);
    if (areVisible && !getChartModel().getTaskManager().getTaskHierarchy().hasNestedTasks(t) && !t.isMilestone() && !t.isProjectTask()) {
      renderProgressBar(Lists.newArrayList(Iterables.filter(rectangles, REMOVE_SUPERTASK_ENDINGS)));
    }
    if (areVisible && myTaskApi.hasNotes(t)) {
      Rectangle notes = getPrimitiveContainer().createRectangle(myModel.getBounds().width - 24, rowNum * getRowHeight() + getRowHeight()/2 - 8, 16, 16);
      notes.setStyle("task.notesMark");
      getPrimitiveContainer().bind(notes, t);
    }
    return rectangles;
  }

  private void renderLabels(List<Polygon> rectangles) {
    if (!rectangles.isEmpty()) {
      myLabelsRenderer.renderLabels(rectangles);
    }
  }

  private void renderProgressBar(List<Polygon> rectangles) {
    if (rectangles.isEmpty()) {
      return;
    }
    final Canvas container = getPrimitiveContainer().getLayer(0);
    final TimeUnit timeUnit = getChartModel().getTimeUnitStack().getDefaultTimeUnit();
    final Task task = ((TaskActivity) rectangles.get(0).getModelObject()).getOwner();
    float length = task.getDuration().getLength(timeUnit);
    float completed = task.getCompletionPercentage() * length / 100f;
    Polygon lastProgressRectangle = null;

    for (Polygon nextRectangle : rectangles) {
      final TaskActivity nextActivity = (TaskActivity) nextRectangle.getModelObject();
      final float nextLength = nextActivity.getDuration().getLength(timeUnit);

      final int nextProgressBarLength;
      if (completed > nextLength || nextActivity.getIntensity() == 0f) {
        nextProgressBarLength = nextRectangle.getWidth();
        if (nextActivity.getIntensity() > 0f) {
          completed -= nextLength;
        }
      } else {
        nextProgressBarLength = (int) (nextRectangle.getWidth() * (completed / nextLength));
        completed = 0f;
      }

      final Rectangle nextProgressBar = container.createRectangle(nextRectangle.getLeftX(),
          nextRectangle.getMiddleY() - 1, nextProgressBarLength, 3);
      nextProgressBar.setStyle(completed == 0f ? "task.progress.end" : "task.progress");
      getPrimitiveContainer().getLayer(0).bind(nextProgressBar, task);
      if (completed == 0) {
        lastProgressRectangle = nextRectangle;
        break;
      }
    }
    if (lastProgressRectangle == null) {
      lastProgressRectangle = rectangles.get(rectangles.size() - 1);
    }
    // createDownSideText(lastProgressRectangle);
  }

  public GPOptionGroup getLabelOptions() {
    return myLabelOptions;
  }

  public int calculateRowHeight() {
    int rowHeight = myLabelsRenderer.calculateRowHeight();
    if (myModel.getBaseline() != null) {
      rowHeight = rowHeight + 8;
    }
    int appFontSize = myModel.getProjectConfig().getAppFontSize().get();
    return Math.max(appFontSize, rowHeight);
  }

  private int getRectangleHeight() {
    return myLabelsRenderer.getFontHeight();
  }

  Canvas getLabelLayer() {
    return myLabelsLayer;
  }

  private TaskActivitySceneBuilder<Task, TaskActivity> createTaskActivitySceneBuilder(
      Canvas canvas, TaskActivitySceneBuilder.ChartApi chartApi, TaskActivitySceneBuilder.Style style) {
    return new TaskActivitySceneBuilder<Task, TaskActivity>(myTaskApi, chartApi, canvas, myLabelsRenderer, style);
  }

  public static List<Rectangle> getTaskRectangles(Task t, ChartModelImpl chartModel) {
    List<Rectangle> result = new ArrayList<Rectangle>();
    List<TaskActivity> originalActivities = t.getActivities();
    List<TaskActivity> splitOnBounds = splitOnBounds(originalActivities, chartModel.getStartDate(), chartModel.getEndDate());
    for (TaskActivity activity : splitOnBounds) {
      assert activity != null : "Got null activity in task="+t;
      Canvas.Shape graphicPrimitive = chartModel.getGraphicPrimitive(activity);
      assert graphicPrimitive != null : "Got null for activity="+activity;
      assert graphicPrimitive instanceof Rectangle;
      result.add((Rectangle) graphicPrimitive);
    }
    return result;

  }
}