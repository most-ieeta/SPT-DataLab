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

package ua.ieeta.sptdatalab.util.io;

import java.io.IOException;
import java.util.List;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.*;


public class MultiFormatReader
{
  public static final int FORMAT_UNKNOWN = 0;
  public static final int FORMAT_WKT = 1;
  public static final int FORMAT_WKB = 2;
  public static final int FORMAT_GML = 3;
  private static final int FORMAT_GEOJSON = 4;

  public static boolean isWKT(String s) {
    return !isWKB(s) && !isGML(s);
  }

  public static boolean isWKB(String str) {
    return isHex(str, MAX_CHARS_TO_CHECK);
  }

  public static boolean isGML(String str) {
    return str.indexOf("<") >= 0;
  }

  public static boolean isGeoJSON(String str) {
    return str.indexOf("{") >= 0;
  }

  public static int format(String s) {
    if (isWKB(s))
      return FORMAT_WKB;
    if (isGML(s))
      return FORMAT_GML;
    if (isGeoJSON(s))
      return FORMAT_GEOJSON;
    if (isWKT(s))
      return FORMAT_WKT;
    return FORMAT_UNKNOWN;
  }
	
  private static boolean isHex(String str, int maxCharsToTest)
  {
    for (int i = 0; i < maxCharsToTest && i < str.length(); i++) {
      char ch = str.charAt(i);
      if (! isHexDigit(ch))
        return false;
    }
    return true;
  }

  private static boolean isHexDigit(char ch)
  {
    if (Character.isDigit(ch)) return true;
    char chLow = Character.toLowerCase(ch);
    if (chLow >= 'a' && chLow <= 'f') return true;
    return false;
  }

  private static final int MAX_CHARS_TO_CHECK = 6;

  private GeometryFactory geomFactory;
  private WKTReader wktReader;
  private WKBReader wkbReader;

  public MultiFormatReader()
  {
    this(new GeometryFactory());
  }

  public MultiFormatReader(GeometryFactory geomFactory)
  {
    this.geomFactory = geomFactory;
    wktReader = new WKTReader(geomFactory);
    wkbReader = new WKBReader(geomFactory);
  }

  public Geometry read(String geomStr)
      throws ParseException, IOException
  {
    String trimStr = geomStr.trim();
    if (isWKB(trimStr)) {
      return IOUtil.readWKBHexString(trimStr, geomFactory);
    }
      
    return IOUtil.readWKTString(trimStr, geomFactory);
  }
  
   
}