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

package ua.ieeta.sptdatalab.ui.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import ua.ieeta.sptdatalab.app.AppConstants;
import ua.ieeta.sptdatalab.geom.ConstrainedInteriorPoint;
import ua.ieeta.sptdatalab.ui.GraphicsUtil;
import ua.ieeta.sptdatalab.ui.Viewport;


public class DataLabelStyle implements Style
{
  private Color color;

  public DataLabelStyle(Color color) {
    this.color = color;
  }

  public DataLabelStyle() {
  }

  public void paint(Geometry geom, Viewport viewport, Graphics2D g2d)
  {
    if (geom.getUserData() == null) return;
    
    Coordinate p = null;
    if (geom instanceof Polygon) {
      p = ConstrainedInteriorPoint.getCoordinate((Polygon) geom, viewport.getModelEnv());
    }
    else {
      p = geom.getInteriorPoint().getCoordinate();
    }
    
    Point2D vp = viewport.toView(new Point2D.Double(p.x, p.y));
    
    g2d.setColor(color);
    g2d.setFont(AppConstants.FONT_LABEL);
    
    String label = geom.getUserData().toString();
    //int stringLen = (int) g2d.getFontMetrics().getStringBounds(label, g2d).getWidth();
    GraphicsUtil.drawStringAlignCenter(g2d, label, (int) vp.getX(), (int) vp.getY()); 
  }
  
  public Color getColor() {
    return color;
  }



}
