/*
 * Copyright (c) 2003 Open Source Geospatial Foundation, All rights reserved.
 * 
 * This program and the accompanying materials are made available under the terms
 * of the OSGeo BSD License v1.0 available at:
 *
 * https://www.osgeo.org/sites/osgeo.org/files/Page/osgeo-bsd-license.txt
 */
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

package ua.ieeta.sptdatalab.io.shapefile;

/**
 * Thrown when an error relating to the shapefile
 * occures
 */
public class ShapefileException extends Exception{
    public ShapefileException(){
        super();
    }    
    public ShapefileException(String s){
        super(s);
    }
}




