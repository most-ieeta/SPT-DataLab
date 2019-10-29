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
package ua.ieeta.sptdatalab.model;

public interface GeometryType {
  public final static int WELLKNOWNTEXT = 1;

  public final static int GEOMETRYCOLLECTION = 1;
  public final static int MULTIPOLYGON = 2;
  public final static int MULTILINESTRING = 3;
  public final static int MULTIPOINT = 4;
  public final static int POLYGON = 5;
  public final static int LINESTRING = 6;
  public final static int POINT = 7;

}
