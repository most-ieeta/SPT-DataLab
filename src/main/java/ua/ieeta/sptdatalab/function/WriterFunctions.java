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

import java.lang.reflect.InvocationTargetException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBWriter;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.locationtech.jts.io.gml2.GMLWriter;
import org.locationtech.jts.io.kml.KMLWriter;
import ua.ieeta.sptdatalab.geomfunction.Metadata;
import ua.ieeta.sptdatalab.util.ClassUtil;


public class WriterFunctions 
{
  public static String writeKML(Geometry geom)
  {
    if (geom == null) return "";
    KMLWriter writer = new KMLWriter();
    return writer.write(geom);
  }
  
  public static String writeGML(Geometry geom)
  {
    if (geom == null) return "";
    return (new GMLWriter()).write(geom);
  }

  public static String writeOra(Geometry g) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
    if (g == null) return "";
    // call dynamically to avoid dependency on OraWriter
    String sql = (String) ClassUtil.dynamicCall("com.vividsolutions.jts.io.oracle.OraWriter", 
        "writeSQL", 
        new Class[] { Geometry.class }, 
        new Object[] { g });
    return sql;
    //return (new OraWriter(null)).writeSQL(g);
  }
  
  public static String writeWKB(Geometry g)
  {
    if (g == null) return "";
    return WKBWriter.toHex((new WKBWriter().write(g)));
  }
  
  public static String writeGeoJSON(Geometry g)
  {
    if (g == null) return "";
    return (new GeoJsonWriter().write(g));
  }
  
  public static String writeGeoJSONFixDecimal(Geometry g,
      @Metadata(title="Num Decimals")
      int numDecimals)
  {
    if (g == null) return "";
    return (new GeoJsonWriter(numDecimals).write(g));
  }

}
