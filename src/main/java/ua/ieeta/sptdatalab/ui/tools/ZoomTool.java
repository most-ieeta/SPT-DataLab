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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import ua.ieeta.sptdatalab.app.AppConstants;



public class ZoomTool extends BasicTool 
{
  private double zoomFactor = 1.5;
  private Point mouseStart = null;
  private Point mouseEnd = null;
  private Point2D panStart;
  
  public ZoomTool(double zoomFactor, Cursor cursor) {
    super(cursor);
    this.zoomFactor = zoomFactor;
  }

  public void mouseClicked(MouseEvent mouseEvent) 
  {
    // determine if zoom in (left) or zoom out (right)
    double realZoomFactor = SwingUtilities.isRightMouseButton(mouseEvent)
         ? (1d / zoomFactor) : zoomFactor;
    getClickedPanel().zoom(toModel(mouseEvent.getPoint()), realZoomFactor);
  }

  public void mousePressed(MouseEvent e)
  {
  	mouseStart = e.getPoint();
  	mouseEnd = e.getPoint();
  	panStart = isPanGesture(e) ? toModel(mouseStart) : null;
  }
  
  public void mouseReleased(MouseEvent e) {
    // don't process if mouse was dragged a very short distance
    if (! isSignificantMouseMove(e.getPoint()))
      return;
    
    if (isPanGesture(e)) {
      Point2D panEnd = toModel(e.getPoint());
      PanTool.pan(panel(), panStart, panEnd);
      return;
    }
    // no key -> do Zoom
    getClickedPanel().zoom(toModel(mouseStart), toModel(mouseEnd));
    /*
    AppImage.setImageHeight( (int) Math.round(AppImage.getImageHeight() * zoomFactor));
    AppImage.setImageWidth( (int) Math.round(AppImage.getImageWidth() * zoomFactor));
    */
  }

  private static boolean isPanGesture(MouseEvent e) {
    return e.isControlDown() || SwingUtilities.isRightMouseButton(e);
  }
  private boolean isPanning() {
    return panStart != null;
  }
  
  public void mouseDragged(MouseEvent e)
  {   
  	Graphics g = getBandGraphics();
  	// erase old band
  	drawBand(g);

  	// draw new band
  	Point currPoint = e.getPoint();
  	mouseEnd = currPoint;
  	drawBand(g);
  }

  private Graphics getBandGraphics() {
        Graphics g = getClickedPanel().getGraphics();
  	g.setColor(AppConstants.BAND_CLR);
  	g.setXORMode(Color.white);
    return g;
  }
  
  private void drawBand(Graphics g) {
    if (isPanning()) {
      drawLine(g, mouseStart, mouseEnd);
    }
    else {
      drawRect(g, mouseStart, mouseEnd);
    }
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    double notches = e.getPreciseWheelRotation();
    double zoom = Math.abs(notches) * this.zoomFactor;
    if (notches > 0 && zoom > 0) zoom = 1.0 / zoom;
    getClickedPanel().zoom(toModel(e.getPoint()), zoom);
  }
  
  private static final int MIN_MOVEMENT = 5;
  
  private boolean isSignificantMouseMove(Point p)
  {
    int delta = Math.abs(mouseStart.x - p.x) + Math.abs(mouseStart.y - p.y);
  	if (delta < MIN_MOVEMENT)
  		return false;
  	return true;
  }
  
    public static void drawRect(Graphics g, Point p0, Point p1)
    {
        Point base = new Point(Math.min(p0.x, p1.x), Math.min(p0.y, p1.y));
        int width = Math.abs(p1.x - p0.x);
        int height = Math.abs(p1.y - p0.y);
        g.drawRect(base.x, base.y, width, height);
    }
    public static void drawLine(Graphics g, Point p0, Point p1)
    {
        g.drawLine(p0.x, p0.y, p1.x, p1.y);
    }
}

