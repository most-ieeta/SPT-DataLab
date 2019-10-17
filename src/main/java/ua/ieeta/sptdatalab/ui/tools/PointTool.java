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
package ua.ieeta.sptdatalab.ui.tools;

import ua.ieeta.sptdatalab.model.GeometryType;


/**
 * @version 1.7
 */
public class PointTool extends AbstractDrawTool 
{
    private static PointTool singleton = null;

    public static PointTool getInstance() {
        if (singleton == null)
            singleton = new PointTool();
        return singleton;
    }

    private PointTool() 
    {
    	setClickCountToFinishGesture(1);
    	setDrawBandLines(false);
    }
    
    protected int getGeometryType()
    {
    	return GeometryType.POINT;
    }
 }
