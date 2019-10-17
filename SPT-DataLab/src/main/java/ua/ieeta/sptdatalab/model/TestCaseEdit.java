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

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import ua.ieeta.sptdatalab.test.TestCase;
import ua.ieeta.sptdatalab.test.Testable;
import ua.ieeta.sptdatalab.app.SPTDataLab;



/**
 * @version 1.7
 */
public class TestCaseEdit implements Testable {
  private Geometry[] geom = new Geometry[2];
  private Testable testable;
  
  private String opName = "";
  private Geometry resultGeom = null;
  

  public TestCaseEdit(PrecisionModel pm) {
    TestCase testCase = new TestCase();
    testCase.setPrecisionModel(pm);
    testable = testCase;
  }

  public TestCaseEdit(Testable tc) throws ParseException {
    this.testable = tc;
    testable.initGeometry();
    setGeometry(0, testable.getGeometry(0));
    setGeometry(1, testable.getGeometry(1));
  }

  public TestCaseEdit(TestCaseEdit tce)  {
    this.testable = new TestCase();
    setGeometry(0, tce.getGeometry(0));
    setGeometry(1, tce.getGeometry(1));
  }

  public TestCaseEdit(Geometry[] geom) {
    this.testable = new TestCase();
    setGeometry(0, geom[0]);
    setGeometry(1, geom[1]);
  }

  public TestCaseEdit(Geometry[] geom, String name) {
    this.testable = new TestCase();
    setGeometry(0, geom[0]);
    setGeometry(1, geom[1]);
    testable.setName(name);
  }

  private static Geometry cloneGeometry(Geometry geom)
  {
    if (geom == null) return null;
    return (Geometry) geom.clone();
  }
  
  public void setGeometry(int i, Geometry geom) {
    testable.setGeometry(i, geom);
  }

  public void setName(String name) {
    testable.setName(name);
  }

  public Geometry getResult() 
  {
    return resultGeom;
  }
  
  public void setResult(Geometry geom)
  {
    resultGeom = geom;
  }
  
  public String getOpName() { return opName; }
  
  public void setOpName(String name) { opName = name; }
  
  public Geometry getGeometry(int i) {
//    return geom[i];
    return testable.getGeometry(i);
  }


  public Testable getTestable() {
    return testable;
  }


  public String getName() {
    return testable.getName();
  }

  public IntersectionMatrix getIntersectionMatrix() {
    return testable.getIntersectionMatrix();
  }

  public void setIntersectionMatrix(IntersectionMatrix im) {
    testable.setIntersectionMatrix(im);
  }
  public String getDescription() {
    return testable.getDescription();
  }

  public String getWellKnownText(int i) {
    return testable.getWellKnownText(i);
  }

  public void exchange() 
  {
    Geometry temp = testable.getGeometry(0);
    testable.setGeometry(0, testable.getGeometry(1));
    testable.setGeometry(1, temp);
  }

  public void initGeometry() throws ParseException {
    testable.initGeometry();
  }

  public Geometry[] getGeometries() {
    return new Geometry[] {
        testable.getGeometry(0), testable.getGeometry(1)
        };
  }

  public IntersectionMatrix getIM() {
    runRelate();
    return testable.getIntersectionMatrix();
  }
  
  void runRelate() {
    Geometry[] geom = getGeometries();
    if (geom[0] == null || geom[1] == null) {
      return;
    }
    testable.setIntersectionMatrix(geom[0].relate(geom[1]));
  }
}


