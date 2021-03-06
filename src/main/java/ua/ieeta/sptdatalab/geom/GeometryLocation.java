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
package ua.ieeta.sptdatalab.geom;

import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.*;

/**
 * Models the location of a point on a Geometry
 * 
 * @author Martin Davis
 *
 */
public class GeometryLocation 
{
  /**
   * The top-level geometry containing the location
   */
  private Geometry parent;
  /**
   * The Geometry component containing the location
   */
  private Geometry component;
  
  /**
   * The path of indexes to the component containing the location
   */
  private int[] componentPath;
  
  /**
   * The index of the vertex or segment the location occurs on
   */
  private int index;
  
  /**
   * Indicates whether this location is a vertex of the geometry
   */
  private boolean isVertex = true;
  
  /**
   * The actual coordinate for the location
   */
  private Coordinate pt;
  
  public GeometryLocation(Geometry parent, Geometry component, int[] componentPath) 
  {
    this.parent = parent;
    this.component = component;
    this.componentPath = componentPath;
  }

  public GeometryLocation(Geometry parent, Geometry component, int index, Coordinate pt) 
  {
    this.parent = parent;
    this.component = component;
    this.index = index;
    this.pt = pt;
  }

  public GeometryLocation(Geometry parent, Geometry component, int segmentIndex, boolean isVertex,
      Coordinate pt) 
  {
    this.parent = parent;
    this.component = component;
    this.index = segmentIndex;
    this.isVertex = isVertex;
    this.pt = pt;
  }

  public GeometryLocation(Geometry parent, Geometry component, int[] componentPath, int segmentIndex, boolean isVertex,
      Coordinate pt) 
  {
    this.parent = parent;
    this.component = component;
    this.componentPath = componentPath;
    this.index = segmentIndex;
    this.isVertex = isVertex;
    this.pt = pt;
  }

  public Geometry getComponent() 
  {
  	return component;
  }
  
  public Coordinate getCoordinate() { return pt; }
  
  public boolean isVertex() { return isVertex; }
  
  public Geometry insert()
  {
    return GeometryVertexInserter.insert(parent, (LineString) component, index, pt);
  }
  
  public Geometry delete()
  {
    return GeometryVertexDeleter.delete(parent, (LineString) component, index);
  }
  
  public double getLength()
  {
    if (isVertex()) return 0;
    Coordinate p1 = component.getCoordinates()[index + 1];
    return pt.distance(p1);
  }
  public String toString()
  {
    return pt.toString();
  }
  
    public String pathString()
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < componentPath.length; i++) {
            if (i > 0) {
                buf.append(":");
            }
            buf.append(componentPath[i]);
        }
        return buf.toString();
    }
  
    public String toFacetString()
    {
        StringBuffer buf = new StringBuffer();

        // facet index
        buf.append("[");
        for (int i = 0; i < componentPath.length; i++) {
            if (i > 0) {
                buf.append(":");
            }
            buf.append(componentPath[i]);
        }
        buf.append(" ");
        buf.append(index);
        if (! isVertex()) {
            buf.append("-" + (index + 1));
        }
        buf.append("]  ");

        // facet value
        buf.append(isVertex() ? "POINT " : "LINESTRING ");

        buf.append("( ");
        buf.append(pt.x);
        buf.append(" ");
        buf.append(pt.y);
        if (! isVertex()) {
            Coordinate p1 = component.getCoordinates()[index + 1];
            buf.append(", ");
            buf.append(p1.x);
            buf.append(" ");
            buf.append(p1.y);		
        }
        buf.append(" )");
        return buf.toString();
    }
    public List<Coordinate> getCoordsInSegment()
    {
        List<Coordinate> coords = new ArrayList<>();
        coords.add(pt);
        if (! isVertex()) {
            Coordinate p1 = component.getCoordinates()[index + 1];
            coords.add(p1);	
        }
        return coords;
    }
    
    //given a point in a line, returns the 2 points closest in the line
    public List<Coordinate> get2CoordsInSegment()
    {
        List<Coordinate> coords = new ArrayList<>();
        //coords.add(pt);
        if (! isVertex()) {
            Coordinate p1 = component.getCoordinates()[index + 1];
            coords.add(p1);	
            Coordinate p2 = component.getCoordinates()[index];
            coords.add(p2);
        }
        return coords;
    }

}
