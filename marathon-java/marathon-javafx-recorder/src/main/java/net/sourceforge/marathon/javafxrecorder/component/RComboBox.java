package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import net.sourceforge.marathon.javafxagent.components.JComboBoxJavaElement;
import net.sourceforge.marathon.javafxagent.components.JComboBoxOptionJavaElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RComboBox extends RComponent {

    private Object prevSelectedItem;

    public RComboBox(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusLost(RComponent next) {
        JComboBox comboBox = (JComboBox) component;
        Object selectedItem = comboBox.getSelectedItem();
        if (selectedItem != null && selectedItem.equals(prevSelectedItem))
            return;
        if (!comboBox.isEditable()) {
            recorder.recordSelect(this, getText(comboBox, true));
        } else {
            String editorText = ((JTextField) comboBox.getEditor().getEditorComponent()).getText();
            String selectedItemText = getText(comboBox, false);
            if (editorText.equals(selectedItemText))
                recorder.recordSelect(this, getText(comboBox, true));
            else
                recorder.recordSelect(this, editorText);
        }
    }

    private String getText(JComboBox comboBox, boolean appendText) {
        return JComboBoxOptionJavaElement.getText(comboBox, comboBox.getSelectedIndex(), appendText);
    }

    @Override public void focusGained(RComponent prev) {
        prevSelectedItem = ((JComboBox) component).getSelectedItem();
    }

    @Override public String[][] getContent() {
        return JComboBoxJavaElement.getContent((JComboBox) component);
    }
    
    @Override public String getText() {
        return JComboBoxJavaElement.getSelectedItemText((JComboBox) component);
    }
}
