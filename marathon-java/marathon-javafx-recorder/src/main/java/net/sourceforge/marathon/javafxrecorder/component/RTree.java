package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.marathon.javafxagent.components.JTreeJavaElement;
import net.sourceforge.marathon.javafxagent.components.PropertyHelper;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RTree extends RComponent {

    private int row = -1;
    private String text = null;
    private String cellInfo;

    public RTree(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        JTree tree = (JTree) source;
        if (point != null) {
            row = tree.getClosestRowForLocation(point.x, point.y);
        } else {
            if (tree.isEditing()) {
                TreePath editingPath = tree.getEditingPath();
                row = tree.getRowForPath(editingPath);
            }
        }
        cellInfo = getTextForNode(tree, row);
    }

    @Override public void focusLost(RComponent next) {
        JTree tree = (JTree) component;
        String currentText = getText();
        if (currentText != null && !currentText.equals(text)) {
            recorder.recordSelect2(this, currentText, true);
        }
        if (next == null || next.getComponent() != component) {
            int[] selectionRows = tree.getSelectionRows();
            if (selectionRows == null)
                selectionRows = new int[0];
            List<Properties> pa = new ArrayList<Properties>();
            for (int i = 0; i < selectionRows.length; i++) {
                Properties p = new Properties();
                p.put("Path", getTextForNode(tree, selectionRows[i]));
                pa.add(p);
            }
            recorder.recordSelect(this, PropertyHelper.toString(pa.toArray(new Properties[pa.size()]), new String[] { "Path" }));
        }
    }

    @Override public void focusGained(RComponent prev) {
        text = getText();
    }

    public String getText() {
        JTree tree = (JTree) component;
        if (row == -1)
            return null;
        TreePath rowPath = tree.getPathForRow(row);
        if (rowPath == null)
            return null;
        Object lastPathComponent = rowPath.getLastPathComponent();
        if (lastPathComponent != null)
            return getTextForNodeObject(tree, lastPathComponent);
        return null;
    }

    private String getTextForNode(JTree tree, int row) {
        TreePath treePath = tree.getPathForRow(row);
        if (treePath == null)
            return row + "";
        StringBuilder sb = new StringBuilder();
        int start = tree.isRootVisible() ? 0 : 1;
        Object[] objs = treePath.getPath();
        for (int i = start; i < objs.length; i++) {
            String pathString;
            if (objs[i].toString() == null)
                pathString = "";
            else
                pathString = escapeSpecialCharacters(getTextForNodeObject(tree, objs[i]));
            sb.append("/" + pathString);
        }
        return sb.toString();
    }

    private String getTextForNodeObject(JTree tree, Object lastPathComponent) {
        TreeCellRenderer renderer = tree.getCellRenderer();
        if (renderer == null)
            return null;
        Component c = renderer.getTreeCellRendererComponent(tree, lastPathComponent, false, false, false, 0, false);
        if (c != null && c instanceof JLabel) {
            return ((JLabel) c).getText();
        }
        return lastPathComponent.toString();
    }

    private String escapeSpecialCharacters(String name) {
        return name.replaceAll("/", "\\\\/");
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + row;
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RTree other = (RTree) obj;
        if (row != other.row)
            return false;
        return true;
    }

    @Override protected void mousePressed(MouseEvent me) {
        // Ignore double clicks on non-leaf tree nodes
        if (me.getButton() == MouseEvent.BUTTON1 && me.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
            if (me.getClickCount() == 1)
                return;
            TreePath path = ((JTree) component).getPathForRow(row);
            if (path != null) {
                Object lastPathComponent = path.getLastPathComponent();
                if (lastPathComponent instanceof TreeNode) {
                    TreeNode node = (TreeNode) lastPathComponent;
                    if (node.getChildCount() != 0)
                        return;
                }
            }
        }
        // Ignore Ctrl+Clicks used to select the nodes
        if (me.getButton() == MouseEvent.BUTTON1 && isMenuShortcutKeyDown(me))
            return;
        if (me.getButton() != MouseEvent.BUTTON1)
            focusLost(null);
        super.mousePressed(me);
    }

    @Override public String getCellInfo() {
        return cellInfo;
    }

    @Override public String[][] getContent() {
        return JTreeJavaElement.getContent((JTree) component);
    }
}
