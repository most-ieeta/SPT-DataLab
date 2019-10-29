package ua.ieeta.sptdatalab.geom;

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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;


public class SegmentClipper {
  
  public static void clip(Coordinate p0, Coordinate p1, Envelope env) {
    clipEndpoint(p0, p1, env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
    clipEndpoint(p1, p0, env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
  }

  private static void clipEndpoint(Coordinate p0, Coordinate p1, 
        double xmin, double ymin, double xmax, double ymax) {
    double dx = p1.getX() - p0.getX();
    double dy = p1.getY() - p0.getY();
    
    double x = p0.getX();
    double y = p0.getY();
    if (dx != 0) {
      if (x < xmin) {
        y = y + (xmin - x) * dy / dx;
        x = xmin;
      }
      else if (x > xmax) {
        y = y + (xmax - x) * dy / dx;
        x = xmax;
      }
    }
    if (dy != 0) {
      if (y < ymin) {
        x = x + (ymin - y) * dx / dy;
        y = ymin;
      }
      else if (y > ymax) {
        x = x + (ymax - y) * dx / dy;
        y = ymax;
      }
    }
    p0.setX(x);
    p0.setY(y);
  }
}
