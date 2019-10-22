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

package ua.ieeta.sptdatalab.controller;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import ua.ieeta.sptdatalab.app.GeometryEditPanel;
import ua.ieeta.sptdatalab.app.SPTDataLab;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderFrame;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderToolBar;
import ua.ieeta.sptdatalab.model.DisplayParameters;
import ua.ieeta.sptdatalab.model.LayerList;
import ua.ieeta.sptdatalab.model.TestBuilderModel;
import ua.ieeta.sptdatalab.ui.SwingUtil;


public class SPTDataLabBuilderController 
{
  /*
  private static boolean autoZoomOnNextChange = false;

  
  public static void requestAutoZoom()
  {
    autoZoomOnNextChange  = true;
  }
  */
 

  public static void setShowingStructure(boolean showStructure) {
    DisplayParameters.setShowingStructure(showStructure);
    SPTDataLabBuilderController.geometryViewChanged();
  }

  public static void setShowingOrientations(boolean showingOrientations) {
    DisplayParameters.setShowingOrientation(showingOrientations);
    SPTDataLabBuilderController.geometryViewChanged();
  }

  public void setShowVertexIndices(boolean showVertexIndices) {
    DisplayParameters.setShowingOrientation(showVertexIndices);
    SPTDataLabBuilderController.geometryViewChanged();
  }

  public static void setShowingVertices(boolean showingVertices) {
    DisplayParameters.setShowingVertices(showingVertices);
    SPTDataLabBuilderController.geometryViewChanged();
  }

  public static void setShowingLabel(boolean showLabel) {
    DisplayParameters.setShowingLabel(showLabel);
    SPTDataLabBuilderController.geometryViewChanged();
  }

  public static void setFillType(int fillType) {
    DisplayParameters.setFillType(fillType);
    SPTDataLabBuilderController.geometryViewChanged();
  }
  
    public static void geometryViewChanged()
    {     
        getGeometryEditPanel().updateView();

      //TODO: provide autoZoom checkbox on Edit tab to control autozooming (default = on)
    }
  
    public static void geometryViewChanged2()
    {     
        getGeometryEditPanel2().updateView();

    }

    public static GeometryEditPanel getGeometryEditPanel()
    {
        return SPTDataLabBuilderFrame.getGeometryEditPanel();
    }

    public static GeometryEditPanel getGeometryEditPanel2()
    {
        return SPTDataLabBuilderFrame.getGeometryEditPanel2();
    }
  
    
    //not necessary?
  public static Geometry getGeometryA() {
    return SPTDataLab.model().getGeometryEditModel().getGeometry(0);
  }
  //not necessary?
  public static Geometry getGeometryB() {
    return SPTDataLab.model().getGeometryEditModel().getGeometry(1);
  }

  public static void zoomToFullExtent()
  {
    getGeometryEditPanel().zoomToFullExtent();
    getGeometryEditPanel2().zoomToFullExtent();
  }
  
  public static void zoomToInput()
  {
    getGeometryEditPanel().zoomToInput();
    getGeometryEditPanel2().zoomToInput();
  }
  
  public static void addTestCase(Geometry[] geom, String name)
  {
    model().addCase(geom, name);
    SPTDataLabBuilderFrame.instance().updateTestCases();
  }
  
  public static void extractComponentsToTestCase(Coordinate pt)
  {
    double toleranceInModel = getGeometryEditPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry comp = lyrList.getComponent(pt, toleranceInModel);
    if (comp == null) 
      return;
    model().addCase(new Geometry[] { comp, null });
    SPTDataLabBuilderFrame.instance().updateTestCases();
  }
  
  public static void extractComponentsToTestCase(Geometry aoi)
  {
    //double toleranceInModel = SPTDataLabBuilderFrame.getGeometryEditPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry[] comp = lyrList.getComponents(aoi);
    if (comp == null) 
      return;
    model().addCase(comp);
    SPTDataLabBuilderFrame.instance().updateTestCases();
    toolbar().clearToolButtons();
    toolbar().unselectExtractComponentButton();
    editPanel().setCurrentTool(null);
  }

  public static void copyComponentToClipboard(Coordinate pt)
  {
    double toleranceInModel = getGeometryEditPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry comp = lyrList.getComponent(pt, toleranceInModel);
    if (comp == null) 
      return;
    SwingUtil.copyToClipboard(comp, false);
  }
  
  public static void setFocusGeometry(int index) {
    model().getGeometryEditModel().setEditGeomIndex(index);
    toolbar().setFocusGeometry(index);    
  }

  public static void inspectGeometry()
  {
    SPTDataLabBuilderFrame.instance().actionInspectGeometry();
  }
  public static void exchangeGeometry()
  {
    SPTDataLabBuilderFrame.instance().actionExchangeGeoms();
  }
  public static void clearResult()
  {
    SPTDataLabBuilderFrame.instance().getResultWKTPanel().clearResult();
    model().setResult(null);
    editPanel().updateView();
  }

  private static TestBuilderModel model() {
    return SPTDataLabBuilderFrame.instance().getModel();
  }
  private static GeometryEditPanel editPanel() {
    return SPTDataLabBuilderFrame.instance().getGeometryEditPanel();
  }
  private static GeometryEditPanel editPanel2() {
    return SPTDataLabBuilderFrame.instance().getGeometryEditPanel2();
  }


  private static SPTDataLabBuilderToolBar toolbar() {
    return SPTDataLabBuilderFrame.instance().getToolbar();
  }
  

}
