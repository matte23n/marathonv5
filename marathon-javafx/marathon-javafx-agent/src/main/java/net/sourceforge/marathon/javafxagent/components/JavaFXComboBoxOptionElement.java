/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javafxagent.components;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.input.PickResult;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class JavaFXComboBoxOptionElement extends JavaFXElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXComboBoxOptionElement.class.getName());

    private JavaFXElement parent;
    private int option;

    public JavaFXComboBoxOptionElement(JavaFXElement parent, int option) {
        super(parent);
        this.parent = parent;
        this.option = option;
    }

    @Override
    public IJavaFXElement getParent() {
        return parent;
    }

    @Override
    public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-option").put("parameters", new JSONArray().put(option + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override
    public Node getPseudoComponent() {
        return null;
    }

    @Override
    public String _getText() {
        return getComboBoxText((ComboBox<?>) getComponent(), option, true);
    }

    @Override
    public void click(int button, Node target, PickResult pickResult, int clickCount, double xoffset, double yoffset) {
        Platform.runLater(() -> ((ComboBox<?>) getComponent()).getSelectionModel().select(option));
    }
}
