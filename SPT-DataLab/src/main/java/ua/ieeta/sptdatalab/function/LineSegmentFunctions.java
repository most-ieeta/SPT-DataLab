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

package ua.ieeta.sptdatalab.function;

import org.locationtech.jts.algorithm.CGAlgorithmsDD;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

public class LineSegmentFunctions
{
  public static boolean segmentIntersects(Geometry g1, Geometry g2)
  {
    Coordinate[] pt1 = g1.getCoordinates();
    Coordinate[] pt2 = g2.getCoordinates();
    RobustLineIntersector ri = new RobustLineIntersector();
    ri.computeIntersection(pt1[0], pt1[1], pt2[0], pt2[1]);
    return ri.hasIntersection();
  }
  
  public static Geometry segmentIntersection(Geometry g1, Geometry g2)
  {
    Coordinate[] pt1 = g1.getCoordinates();
    Coordinate[] pt2 = g2.getCoordinates();
    RobustLineIntersector ri = new RobustLineIntersector();
    ri.computeIntersection(pt1[0], pt1[1], pt2[0], pt2[1]);
    switch (ri.getIntersectionNum()) {
    case 0:
      // no intersection => return empty point
      return g1.getFactory().createPoint((Coordinate) null);
    case 1:
      // return point
      return g1.getFactory().createPoint(ri.getIntersection(0));
    case 2:
      // return line
      return g1.getFactory().createLineString(
          new Coordinate[] {
              ri.getIntersection(0),
              ri.getIntersection(1)
          });
    }
    return null;
  }
  
  public static Geometry segmentIntersectionDD(Geometry g1, Geometry g2)
  {
    Coordinate[] pt1 = g1.getCoordinates();
    Coordinate[] pt2 = g2.getCoordinates();
    
    // first check if there actually is an intersection
    RobustLineIntersector ri = new RobustLineIntersector();
    ri.computeIntersection(pt1[0], pt1[1], pt2[0], pt2[1]);
    if (! ri.hasIntersection()) {
      // no intersection => return empty point
      return g1.getFactory().createPoint((Coordinate) null);
    }
    
    Coordinate intPt = CGAlgorithmsDD.intersection(pt1[0], pt1[1], pt2[0], pt2[1]);
    return g1.getFactory().createPoint(intPt);
  }
}
