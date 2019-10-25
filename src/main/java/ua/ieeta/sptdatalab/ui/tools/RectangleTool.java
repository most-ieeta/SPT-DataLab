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

package ua.ieeta.sptdatalab.ui.tools;

import java.awt.Cursor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import ua.ieeta.sptdatalab.app.AppCorrGeometries;
import ua.ieeta.sptdatalab.app.GeometryEditPanel;
import ua.ieeta.sptdatalab.app.SPTDataLab;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderFrame;
import ua.ieeta.sptdatalab.geom.GeometryCombiner;
import org.locationtech.jts.geom.GeometryFactory;
import ua.ieeta.sptdatalab.model.GeometryEditModel;

import ua.ieeta.sptdatalab.model.GeometryType;
import static ua.ieeta.sptdatalab.ui.tools.BasicTool.getClickedPanel;


public class RectangleTool extends BoxBandTool
{
    private static RectangleTool singleton = null;

    public static RectangleTool getInstance() {
        if (singleton == null)
            singleton = new RectangleTool();
        return singleton;
    }

    public RectangleTool() {
      super(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    protected void gestureFinished() 
    { 
        SPTDataLabBuilderFrame.instance().updateModelGeometry(getCoordinates(), getClickedPanel());
        SPTDataLabBuilderFrame.instance().disableDrawingButtons(); 
    }
  
}
