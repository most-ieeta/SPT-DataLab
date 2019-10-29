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


package ua.ieeta.sptdatalab.ui;

import ua.ieeta.sptdatalab.model.LayerList;
import ua.ieeta.sptdatalab.model.Layer;
import ua.ieeta.sptdatalab.geom.VertexLocater;
import ua.ieeta.sptdatalab.geom.ComponentLocater;
import ua.ieeta.sptdatalab.geom.GeometryLocation;
import ua.ieeta.sptdatalab.geom.FacetLocater;
import java.util.*;

import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.*;
import ua.ieeta.sptdatalab.app.AppCorrGeometries;


public class GeometryLocationsWriter 
{
  public static String writeLocation(LayerList layers, Coordinate pt, double tolerance, boolean isSecondPanel)
  {
    GeometryLocationsWriter writer = new GeometryLocationsWriter();
    return writer.writeLocationString(layers, pt, tolerance, isSecondPanel);
  }

  private static final int MAX_ITEMS_TO_DISPLAY = 10;
  
  private boolean isHtmlFormatted = true;
  private String eol = null;
  private String highlightStart = null;
  private String highlightEnd = null;
  private String documentStart = null;
  private String documentEnd = null;
  
  private static Set<Coordinate> cursorCoordinates = new HashSet<>();
  
  public GeometryLocationsWriter() {
    setHtml(true);
  }

  public void setHtml(boolean isHtmlFormatted) 
  {
    this.isHtmlFormatted = isHtmlFormatted;
    if (isHtmlFormatted) {
      eol = "<br>";
      highlightStart = "<b>";
      highlightEnd = "</b>";
      documentStart = "<html>";
      documentEnd = "</html>";
    }
    else {
      eol = "\n";
      highlightStart = "";
      highlightEnd = "";
      documentStart = "";
      documentEnd = "";     
    }
 }
  
  public String writeLocationString(LayerList layers, Coordinate pt, double tolerance, boolean isSecondPanel)
  {
    StringBuffer text = new StringBuffer();
    for (int i = 0; i < layers.size(); i++) {

      Layer lyr = layers.getLayer(i);
      if (lyr.getName().equals("A")){
          //this layer contains the geometry with the image and it's not relevant
          continue;
      }
      //contains the coordinates and the number of the point
      String locStr = writeLocation(lyr, pt, tolerance, isSecondPanel);
      if (locStr == null) {
            continue;
      }
      
      if (i > 0 && text.length() > 0) {
        text.append(eol);
        text.append(eol);
      }
      
      //text.append(highlightStart + lyr.getName() + highlightEnd + eol);
      
      text.append(locStr);
    }
    
    if (text.length() > 0) {
        //mark a point in red:
        /*for (int i = 0; i < cursorCoordinates.size(); i++){
            AppCorrGeometries.getInstance().higlightCorrespondingPointInPanel(cursorCoordinates.get(i).x, cursorCoordinates.get(i).y, isSecondPanel);
        }*/
        AppCorrGeometries.getInstance().higlightCorrespondingPointInPanel(cursorCoordinates, isSecondPanel);
        return documentStart + text.toString() +documentEnd;
    }
    //string is empty or null
    return null;
  }
    
  public String writeSingleLocation(Layer lyr, Coordinate p, double tolerance)
  {
    Geometry geom = lyr.getGeometry();
    if (geom == null) return null;
    
    VertexLocater locater = new VertexLocater(geom);
    Coordinate coord = locater.getVertex(p, tolerance);
    int index = locater.getIndex();
    
    if (coord == null) return null;
    return "[" + index + "]: " 
      + coord.x + ", " + coord.y;
  }
  
  public String writeLocation(Layer lyr, Coordinate p, double tolerance, boolean isSecondPanel)
  {
    Geometry geom = lyr.getGeometry();
    if (geom == null) return null;

    String locStr = writeComponentLocation(geom, p, tolerance);
    String facetStr = writeFacetLocation(geom, p, tolerance, isSecondPanel);
    if (facetStr == null) 
      return locStr;
    return locStr + facetStr;   
  }
  
  
  public String writeComponentLocation(Geometry geom, Coordinate p, double tolerance)
  {
    ComponentLocater locater = new ComponentLocater(geom);
    List locs = locater.getComponents(p, tolerance);
    
    StringBuffer buf = new StringBuffer();
    int count = 0;
    for (Iterator i = locs.iterator(); i.hasNext(); ) {
    	
    	GeometryLocation loc = (GeometryLocation) i.next();
    	Geometry comp = loc.getComponent();
        
        String path = loc.pathString();
        path = path.length() == 0 ? "" : path;
        buf.append("[" + path + "]  ");

        buf.append(comp.getGeometryType().toUpperCase());
        if (comp instanceof GeometryCollection) {
          buf.append("[" + comp.getNumGeometries() + "]");
        }
        else {
          buf.append("(" + comp.getNumPoints() + ")");
        }
        if (comp.getUserData() != null) {
          buf.append("  Data: ");
          buf.append(comp.getUserData().toString());
        }
        buf.append(eol);

        if (count++ > MAX_ITEMS_TO_DISPLAY) {
          buf.append(" & more..." + eol);
          break;
        }
    }
    String locStr = buf.toString();
    if (locStr.length() == 0)
      return null;
    return locStr;
  }
    
  public String writeFacetLocation(Geometry geom, Coordinate p, double tolerance, boolean isSecondPanel)
  {
    FacetLocater locater = new FacetLocater(geom);
    List locs = locater.getLocations(p, tolerance);
    List vertexLocs = FacetLocater.filterVertexLocations(locs);
    
    // only show vertices if some are present, to avoid confusing with segments
    if (! vertexLocs.isEmpty()) 
      return writeFacetLocations(vertexLocs, isSecondPanel);
    
    // write 'em all
    return writeFacetLocations(locs, isSecondPanel);
  }
    
  private String writeFacetLocations(List locs, boolean isSecondPanel)
  {
    cursorCoordinates.clear();
    if (locs.size() <= 0) {
        return null;
    }
    
    StringBuffer buf = new StringBuffer();
    boolean isFirst = true;
    for (Iterator i = locs.iterator(); i.hasNext(); ) {
    	GeometryLocation loc = (GeometryLocation) i.next();
    	if (! isFirst) {
            buf.append(eol);
    	}
    	isFirst = false;
        if(!loc.isVertex()){
            cursorCoordinates.addAll(loc.getCoordsInSegment());
        }
        else{
            //add this coordinate to the list of points the mouse is over
            cursorCoordinates.add(loc.getCoordinate());
        }
    }
    for (Coordinate c : cursorCoordinates){
        int index = AppCorrGeometries.getInstance().getCordIndex(c, isSecondPanel);
        //get current source or target geometry with original scale coordinates and get the coordinate in the index
        
        if(index > -1){
            Coordinate originalC = AppCorrGeometries.getInstance().getGeometryInPanelOriginal(!isSecondPanel).getCoordinates()[index];
            buf.append(highlightStart).append("Point Number: ").append(highlightEnd).append(index).append(eol);
            buf.append(originalC.x).append(", ").append(originalC.y).append(eol);
        }
    }
    return buf.toString();
  }

  private String componentType(GeometryLocation loc) {
    String compType = "";
    if (loc.getComponent() instanceof LinearRing) {
      boolean isCCW = Orientation.isCCW(loc.getComponent().getCoordinates());
      compType = "Ring" 
        + (isCCW ? "-CCW" : "-CW ")
          + " ";
    }
    else if (loc.getComponent() instanceof LineString) { 
      compType = "Line  ";
    }
    else if (loc.getComponent() instanceof Point) { 
      compType = "Point ";
    }
    return compType;
  }

  public String OLDwriteLocation(Geometry geom, Coordinate p, double tolerance)
  {
    VertexLocater locater = new VertexLocater(geom);
    List locs = locater.getLocations(p, tolerance);
    
    if (locs.size() <= 0) return null;
    
    StringBuffer buf = new StringBuffer();
    boolean isFirst = true;
    for (Iterator i = locs.iterator(); i.hasNext(); ) {
    	VertexLocater.Location vertLoc = (VertexLocater.Location) i.next();
    	int index = vertLoc.getIndices()[0];
    	Coordinate pt = vertLoc.getCoordinate();
    	if (! isFirst) {
    		buf.append(eol + "--");
    	}
    	isFirst = false;
    	String locStr = "[" + index + "]: " 
    					+ pt.x + ", " + pt.y;
    	buf.append(locStr);
    }
    
    return buf.toString();
  }

}
