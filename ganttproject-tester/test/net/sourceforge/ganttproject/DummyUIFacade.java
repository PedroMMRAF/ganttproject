package net.sourceforge.ganttproject;

import biz.ganttproject.core.calendar.GPCalendarCalc;
import biz.ganttproject.core.option.*;
import biz.ganttproject.core.table.ColumnList;
import biz.ganttproject.core.time.TimeDuration;
import biz.ganttproject.core.time.TimeUnit;
import biz.ganttproject.core.time.TimeUnitStack;
import com.google.common.base.Predicate;
import net.sourceforge.ganttproject.*;
import net.sourceforge.ganttproject.action.GPAction;
import net.sourceforge.ganttproject.action.zoom.ZoomActionSet;
import net.sourceforge.ganttproject.chart.*;
import net.sourceforge.ganttproject.chart.export.ChartImageVisitor;
import net.sourceforge.ganttproject.document.Document;
import net.sourceforge.ganttproject.document.DocumentManager;
import net.sourceforge.ganttproject.gui.*;
import net.sourceforge.ganttproject.gui.scrolling.ScrollingManager;
import net.sourceforge.ganttproject.gui.zoom.ZoomManager;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.roles.RoleManager;
import net.sourceforge.ganttproject.task.*;
import net.sourceforge.ganttproject.undo.GPUndoListener;
import net.sourceforge.ganttproject.undo.GPUndoManager;
import org.eclipse.core.runtime.IStatus;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DummyUIFacade implements UIFacade {
    @Override
    public IntegerOption getDpiOption() {
        return null;
    }

    @Override
    public GPOption<String> getLafOption() {
        return null;
    }

    @Override
    public ScrollingManager getScrollingManager() {
        return null;
    }

    @Override
    public ZoomManager getZoomManager() {
        return null;
    }

    @Override
    public ZoomActionSet getZoomActionSet() {
        return null;
    }

    @Override
    public GPUndoManager getUndoManager() {
        return new GPUndoManager() {
            @Override
            public void undoableEdit(String localizedName, Runnable runnableEdit) {
                runnableEdit.run();
            }

            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }

            @Override
            public void undo() throws CannotUndoException {

            }

            @Override
            public void redo() throws CannotRedoException {

            }

            @Override
            public String getUndoPresentationName() {
                return null;
            }

            @Override
            public String getRedoPresentationName() {
                return null;
            }

            @Override
            public void addUndoableEditListener(GPUndoListener listener) {

            }

            @Override
            public void removeUndoableEditListener(GPUndoListener listener) {

            }

            @Override
            public void die() {

            }
        };
    }

    @Override
    public void setLookAndFeel(GanttLookAndFeelInfo laf) {

    }

    @Override
    public GanttLookAndFeelInfo getLookAndFeel() {
        return null;
    }

    @Override
    public Choice showConfirmationDialog(String message, String title) {
        return null;
    }

    @Override
    public void showPopupMenu(Component invoker, Action[] actions, int x, int y) {

    }

    @Override
    public void showPopupMenu(Component invoker, Collection<Action> actions, int x, int y) {

    }

    @Override
    public void showOptionDialog(int messageType, String message, Action[] actions) {

    }

    @Override
    public Dialog createDialog(Component content, Action[] buttonActions, String title) {
        return null;
    }

    @Override
    public void setStatusText(String text) {

    }

    @Override
    public void showErrorDialog(String errorMessage) {

    }

    @Override
    public void showNotificationDialog(NotificationChannel channel, String message) {

    }

    @Override
    public void showSettingsDialog(String pageID) {

    }

    @Override
    public void showErrorDialog(Throwable e) {

    }

    @Override
    public NotificationManager getNotificationManager() {
        return null;
    }

    @Override
    public GanttChart getGanttChart() {
        return new GanttChart() {
            @Override
            public void setBaseline(GanttPreviousState ganttPreviousState) {

            }

            @Override
            public GanttPreviousState getBaseline() {
                return null;
            }

            @Override
            public GPOptionGroup getBaselineColorOptions() {
                return null;
            }

            @Override
            public ColorOption getTaskDefaultColorOption() {
                return null;
            }

            @Override
            public GPOptionGroup getTaskLabelOptions() {
                return null;
            }

            @Override
            public void setBottomUnitWidth(int width) {

            }

            @Override
            public void setTopUnit(TimeUnit topUnit) {

            }

            @Override
            public void setBottomUnit(TimeUnit bottomUnit) {

            }

            @Override
            public void addRenderer(ChartRendererBase renderer) {

            }

            @Override
            public void resetRenderers() {

            }

            @Override
            public void scrollBy(TimeDuration duration) {

            }

            @Override
            public void setVScrollController(VScrollController vscrollController) {

            }

            @Override
            public ChartModel getModel() {
                return null;
            }

            @Override
            public ChartUIConfiguration getStyle() {
                return null;
            }

            @Override
            public void setStartOffset(int pixels) {

            }

            @Override
            public void setTimelineHeight(int height) {

            }

            @Override
            public IGanttProject getProject() {
                return new IGanttProject() {
                    @Override
                    public String getProjectName() {
                        return null;
                    }

                    @Override
                    public void setProjectName(String projectName) {

                    }

                    @Override
                    public String getDescription() {
                        return null;
                    }

                    @Override
                    public void setDescription(String description) {

                    }

                    @Override
                    public String getOrganization() {
                        return null;
                    }

                    @Override
                    public void setOrganization(String organization) {

                    }

                    @Override
                    public String getWebLink() {
                        return null;
                    }

                    @Override
                    public void setWebLink(String webLink) {

                    }

                    @Override
                    public UIConfiguration getUIConfiguration() {
                        return null;
                    }

                    @Override
                    public HumanResourceManager getHumanResourceManager() {
                        return null;
                    }

                    @Override
                    public RoleManager getRoleManager() {
                        return null;
                    }

                    @Override
                    public TaskManager getTaskManager() {
                        return null;
                    }

                    @Override
                    public TaskContainmentHierarchyFacade getTaskContainment() {
                        return null;
                    }

                    @Override
                    public GPCalendarCalc getActiveCalendar() {
                        return null;
                    }

                    @Override
                    public TimeUnitStack getTimeUnitStack() {
                        return null;
                    }

                    @Override
                    public void setModified() {

                    }

                    @Override
                    public void setModified(boolean modified) {

                    }

                    @Override
                    public void close() {

                    }

                    @Override
                    public Document getDocument() {
                        return null;
                    }

                    @Override
                    public void setDocument(Document document) {

                    }

                    @Override
                    public DocumentManager getDocumentManager() {
                        return null;
                    }

                    @Override
                    public void addProjectEventListener(ProjectEventListener listener) {

                    }

                    @Override
                    public void removeProjectEventListener(ProjectEventListener listener) {

                    }

                    @Override
                    public boolean isModified() {
                        return false;
                    }

                    @Override
                    public void open(Document document)
                            throws IOException, Document.DocumentException {

                    }

                    @Override
                    public CustomPropertyManager getResourceCustomPropertyManager() {
                        return null;
                    }

                    @Override
                    public CustomPropertyManager getTaskCustomColumnManager() {
                        return null;
                    }

                    @Override
                    public List<GanttPreviousState> getBaselines() {
                        return null;
                    }
                };
            }

            @Override
            public void init(IGanttProject project, IntegerOption dpiOption,
                             FontOption chartFontOption) {

            }

            @Override
            public void buildImage(GanttExportSettings settings, ChartImageVisitor imageVisitor) {

            }

            @Override
            public RenderedImage getRenderedImage(GanttExportSettings settings) {
                return null;
            }

            @Override
            public Date getStartDate() {
                return null;
            }

            @Override
            public void setStartDate(Date startDate) {

            }

            @Override
            public Date getEndDate() {
                return null;
            }

            @Override
            public void setDimensions(int height, int width) {

            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void reset() {

            }

            @Override
            public GPOptionGroup[] getOptionGroups() {
                return new GPOptionGroup[0];
            }

            @Override
            public Chart createCopy() {
                return null;
            }

            @Override
            public ChartSelection getSelection() {
                return null;
            }

            @Override
            public IStatus canPaste(ChartSelection selection) {
                return null;
            }

            @Override
            public void paste(ChartSelection selection) {

            }

            @Override
            public void addSelectionListener(ChartSelectionListener listener) {

            }

            @Override
            public void removeSelectionListener(ChartSelectionListener listener) {

            }

            @Override
            public Object getAdapter(Class aClass) {
                return null;
            }
        };
    }

    @Override
    public TimelineChart getResourceChart() {
        return null;
    }

    @Override
    public Chart getActiveChart() {
        return null;
    }

    @Override
    public int getViewIndex() {
        return 0;
    }

    @Override
    public void setViewIndex(int viewIndex) {

    }

    @Override
    public int getGanttDividerLocation() {
        return 0;
    }

    @Override
    public void setGanttDividerLocation(int location) {

    }

    @Override
    public int getResourceDividerLocation() {
        return 0;
    }

    @Override
    public void setResourceDividerLocation(int location) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public Frame getMainFrame() {
        return null;
    }

    @Override
    public Image getLogo() {
        return null;
    }

    @Override
    public void setWorkbenchTitle(String title) {

    }

    @Override
    public TaskView getCurrentTaskView() {
        return null;
    }

    @Override
    public TaskTreeUIFacade getTaskTree() {
        return new TaskTreeUIFacade() {
            @Override
            public Component getTreeComponent() {
                return null;
            }

            @Override
            public ColumnList getVisibleFields() {
                return null;
            }

            @Override
            public boolean isVisible(Task modelElement) {
                return false;
            }

            @Override
            public boolean isExpanded(Task modelElement) {
                return false;
            }

            @Override
            public void setExpanded(Task modelElement, boolean value) {

            }

            @Override
            public void applyPreservingExpansionState(Task modelElement, Predicate<Task> callable) {

            }

            @Override
            public void setSelected(Task modelElement, boolean clear) {

            }

            @Override
            public void clearSelection() {

            }

            @Override
            public void makeVisible(Task modelElement) {

            }

            @Override
            public GPAction getNewAction() {
                return null;
            }

            @Override
            public GPAction getPropertiesAction() {
                return null;
            }

            @Override
            public GPAction getDeleteAction() {
                return null;
            }

            @Override
            public void startDefaultEditing(Task modelElement) {

            }

            @Override
            public void stopEditing() {

            }

            @Override
            public AbstractAction[] getTreeActions() {
                return new AbstractAction[0];
            }
        };
    }

    @Override
    public ResourceTreeUIFacade getResourceTree() {
        return null;
    }

    @Override
    public TaskSelectionManager getTaskSelectionManager() {
        return null;
    }

    @Override
    public TaskSelectionContext getTaskSelectionContext() {
        return null;
    }

    @Override
    public DefaultEnumerationOption<Locale> getLanguageOption() {
        return null;
    }

    @Override
    public GPOptionGroup[] getOptions() {
        return new GPOptionGroup[0];
    }

    @Override
    public void addOnUpdateComponentTreeUi(Runnable callback) {

    }
}
