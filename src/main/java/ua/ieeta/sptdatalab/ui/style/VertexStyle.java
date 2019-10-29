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

import java.awt.*;
import java.awt.geom.*;

import org.locationtech.jts.geom.*;
import ua.ieeta.sptdatalab.app.AppConstants;
import ua.ieeta.sptdatalab.ui.Viewport;


public class VertexStyle  implements Style
{
  private double sizeOver2 = AppConstants.VERTEX_SIZE / 2d;
  
  protected Rectangle shape;
  private Color color;
  
  // reuse point objects to avoid creation overhead
  private Point2D pM = new Point2D.Double();
  private Point2D pV = new Point2D.Double();

  public VertexStyle(Color color) {
    this.color = color;
    // create basic rectangle shape
    shape = new Rectangle(0,
        0, 
        AppConstants.VERTEX_SIZE, 
        AppConstants.VERTEX_SIZE);
  }


  public void paint(Geometry geom, Viewport viewport, Graphics2D g)
  {
    g.setPaint(color);
    Coordinate[] coordinates = geom.getCoordinates();
    
    for (int i = 0; i < coordinates.length; i++) {
        if (! viewport.containsInModel(coordinates[i])) {
            //Otherwise get "sun.dc.pr.PRException: endPath: bad path" exception 
            continue;
        }       
        pM.setLocation(coordinates[i].x, coordinates[i].y);
        viewport.toView(pM, pV);
      	shape.setLocation((int) (pV.getX() - sizeOver2), (int) (pV.getY() - sizeOver2));
        g.fill(shape);
    }
  }
  
}
