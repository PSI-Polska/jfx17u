/*
 * Copyright (c) 2010, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.ButtonBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;

/**
 * Default skin implementation for the tri-state {@link CheckBox} control.
 *
 * @see CheckBox
 * @since 9
 */
public class CheckBoxSkin extends LabeledSkinBase<CheckBox> {

    /* *************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private final StackPane box = new StackPane();
    private StackPane innerbox;
    private final ButtonBehavior< ? extends CheckBox > behavior;



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new CheckBoxSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public CheckBoxSkin(CheckBox control) {
        super(control);

        // install default input map for the CheckBox control
        behavior = createBehavior( control );
//        control.setInputMap(behavior.getInputMap());

        box.getStyleClass().setAll("box");
        innerbox = new StackPane();
        innerbox.getStyleClass().setAll("mark");
        innerbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        box.getChildren().add(innerbox);
        updateChildren();
    }

    protected ButtonBehavior< ? extends CheckBox > createBehavior( final CheckBox control )
    {
        return new ButtonBehavior<>(control);
    }


    /* *************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override public void dispose() {
        super.dispose();

        if (behavior != null) {
            behavior.dispose();
        }
    }

    /** {@inheritDoc} */
    @Override protected void updateChildren() {
        super.updateChildren();
        if (box != null) {
            getChildren().add(box);
        }
    }

    /** {@inheritDoc} */
    @Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSizeX(box.minWidth(-1));
    }

    /** {@inheritDoc} */
    @Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinHeight(width - box.minWidth(-1), topInset, rightInset, bottomInset, leftInset),
                topInset + box.minHeight(-1) + bottomInset);
    }

    /** {@inheritDoc} */
    @Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSizeX(box.prefWidth(-1));
    }

    /** {@inheritDoc} */
    @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computePrefHeight(width - box.prefWidth(-1), topInset, rightInset, bottomInset, leftInset),
                        topInset + box.prefHeight(-1) + bottomInset);
    }

    /** {@inheritDoc} */
    @Override protected void layoutChildren(final double x, final double y,
            final double w, final double h) {
        final CheckBox checkBox = getSkinnable();
        final Pos alignment = checkBox.getAlignment();
        final String txt = checkBox.getText();
        final boolean noTxt = txt == null || txt.isEmpty();
        final boolean noGraphic = checkBox.getGraphic() == null;
        final boolean labelIsEmpty = noTxt && noGraphic; // fixing centering box inside layout width if there's no label

        final double boxWidth = snapSizeX(box.prefWidth(-1));
        final double boxHeight = snapSizeY(box.prefHeight(-1));
        final double computeWidth = Math.max(checkBox.prefWidth(-1), checkBox.minWidth(-1));
        final double labelWidth = labelIsEmpty ? 0 : Math.min( computeWidth - boxWidth, w - snapSizeX(boxWidth));
        final double labelHeight = labelIsEmpty ? 0 : Math.min(checkBox.prefHeight(labelWidth), h);
        final double maxHeight = Math.max(boxHeight, labelHeight);
        final double xOffset = Utils.computeXOffset(w, labelWidth + boxWidth, alignment.getHpos()) + x;
        final double yOffset = Utils.computeYOffset(h, maxHeight, alignment.getVpos()) + y;

        layoutLabelInArea(xOffset + boxWidth, yOffset, labelWidth, maxHeight, alignment );
        box.resize(boxWidth, boxHeight);
        positionInArea(box, xOffset, yOffset, boxWidth, maxHeight, 0, alignment.getHpos(), alignment.getVpos());
    }
}
