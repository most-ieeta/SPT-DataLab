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

import java.awt.Graphics2D;

import org.locationtech.jts.geom.Geometry;
import ua.ieeta.sptdatalab.ui.Viewport;

public class LayerStyle implements Style  {

  private Style geomStyle;
  private Style decoratorStyle;

  public LayerStyle(Style geomStyle, Style decoratorStyle) {
    this.geomStyle = geomStyle;
    this.decoratorStyle = decoratorStyle;
  }
  

  public Style getDecoratorStyle() {
    return decoratorStyle;
  }

  public void paint(Geometry geom, Viewport viewport, Graphics2D g) throws Exception {
    geomStyle.paint(geom, viewport, g);
    decoratorStyle.paint(geom, viewport, g);
  }

}
