/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

/* 
* This file has been modified to be part of SPT Data Lab.
*
* This code is distributed "AS IS" in the hope that it will be useful,
* but WITHOUT ANY WARRANTY. You can redistribute it and/or modify
* as explained in License and Readme.
* 
* Redistributions of source code must retain adequate copyright notices,
* as explained in License and Readme.
*/

package ua.ieeta.sptdatalab.util;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;



/**
 * Useful GUI utilities
 *
 * @version 1.7
 */
public class GuiUtil {

    /**
     * Centers the first component on the second
     */
    public static void center(Component componentToMove, Component componentToCenterOn) {
        Dimension componentToCenterOnSize = componentToCenterOn.getSize();
        componentToMove.setLocation(
            componentToCenterOn.getX()
                + ((componentToCenterOnSize.width - componentToMove.getWidth()) / 2),
            componentToCenterOn.getY()
                + ((componentToCenterOnSize.height - componentToMove.getHeight()) / 2));
    }




    /**
     * Workaround for bug: can't re-show internal frames. See bug parade 4138031.
     */
    public static void show(JInternalFrame internalFrame, JDesktopPane desktopPane)
        throws PropertyVetoException {
        if (!desktopPane.isAncestorOf(internalFrame))
            desktopPane.add(internalFrame);
        internalFrame.setClosed(false);
        internalFrame.setVisible(true);
        internalFrame.toFront();
    }


}