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

import java.awt.Graphics2D;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import ua.ieeta.sptdatalab.app.AppConstants;
import ua.ieeta.sptdatalab.app.GeometryEditPanel;
import ua.ieeta.sptdatalab.app.SPTDataLab;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderFrame;
import ua.ieeta.sptdatalab.ui.render.GeometryPainter;


public class FunctionsUtil {

	public static final Envelope DEFAULT_ENVELOPE = new Envelope(0, 100, 0, 100);
	
	public static Envelope getEnvelopeOrDefault(Geometry g)
	{
		if (g == null) return DEFAULT_ENVELOPE;
		return g.getEnvelopeInternal();
	}
	
  public static GeometryFactory getFactoryOrDefault(Geometry g)
  {
    if (g == null) return SPTDataLab.getGeometryFactory();
    return g.getFactory();
  }
  
  public static GeometryFactory getFactoryOrDefault(Geometry g1, Geometry g2)
  {
    if (g1 != null) return g1.getFactory();
    if (g2 != null) return g2.getFactory();
    return SPTDataLab.getGeometryFactory(); 
  }
  
  public static void showIndicator(Geometry geom)
  {
    GeometryEditPanel panel = SPTDataLabBuilderFrame
    .instance().getTestCasePanel()
    .getGeometryEditPanel();
    Graphics2D gr = (Graphics2D) panel.getGraphics();
    GeometryPainter.paint(geom, panel.getViewport(), gr, 
        AppConstants.INDICATOR_LINE_CLR, 
        AppConstants.INDICATOR_FILL_CLR);
  }
  
  public static Geometry buildGeometry(List geoms, Geometry parentGeom)
  {
    if (geoms.size() <= 0)
      return null;
    if (geoms.size() == 1) 
      return (Geometry) geoms.get(0);
    // if parent was a GC, ensure returning a GC
    if (parentGeom != null && parentGeom.getGeometryType().equals("GeometryCollection"))
      return parentGeom.getFactory().createGeometryCollection(GeometryFactory.toGeometryArray(geoms));
    // otherwise return MultiGeom
    return getFactoryOrDefault(parentGeom).buildGeometry(geoms);
  }
  
  public static Geometry buildGeometry(Geometry[] geoms)
  {
    GeometryFactory gf = SPTDataLab.getGeometryFactory();
    if (geoms.length > 0) {
      gf = getFactoryOrDefault(geoms[0]);
    }
    return gf.createGeometryCollection(geoms);
  }
  
  public static Geometry buildGeometry(Geometry a, Geometry b) {
    Geometry[] geoms = toGeometryArray(a, b);
    return getFactoryOrDefault(a, b).createGeometryCollection(geoms);  }

  public static Geometry[] toGeometryArray(Geometry a, Geometry b) {
    int size = 0;
    if (a != null) size++;
    if (b != null) size++;
    Geometry[] geoms = new Geometry[size];
    size = 0;
    if (a != null) geoms[size++] = a;
    if (b != null) geoms[size] = b;
    return geoms;
  }
}
