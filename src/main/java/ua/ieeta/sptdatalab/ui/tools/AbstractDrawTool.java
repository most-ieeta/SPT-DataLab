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

import ua.ieeta.sptdatalab.model.GeometryType;
import java.awt.event.MouseEvent;

import ua.ieeta.sptdatalab.app.AppCursors;
import ua.ieeta.sptdatalab.app.GeometryEditPanel;

//import com.vividsolutions.jtstest.testbuilder.IconLoader;

/**
 * @version 1.7
 */
public abstract class AbstractDrawTool extends LineBandTool {

	protected AbstractDrawTool() {
	  super(AppCursors.DRAW_GEOM);
	}

	protected abstract int getGeometryType();

	public void mouseClicked(MouseEvent e) {
		setBandType();
		super.mouseClicked(e);
	}

	protected void bandFinished() throws Exception {
            setType();
            geomModel().addComponent(getCoordinates());
            getClickedPanel().updateGeom();
	}

	private void setType() {
            GeometryEditPanel panel = getClickedPanel();
            if (panel.getModel() == null)
		return;
            panel.getGeomModel().setGeometryType(getGeometryType());
	}

	private void setBandType() {
		int geomType = getGeometryType();
		setCloseRing(geomType == GeometryType.POLYGON);
	}
}
