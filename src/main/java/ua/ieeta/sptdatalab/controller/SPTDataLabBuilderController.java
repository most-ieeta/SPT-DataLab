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

import ua.ieeta.sptdatalab.app.GeometryEditPanel;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderFrame;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderToolBar;
import ua.ieeta.sptdatalab.model.DisplayParameters;
import ua.ieeta.sptdatalab.model.TestBuilderModel;


public class SPTDataLabBuilderController 
{


  
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
  
    
  
  public static void zoomToInput()
  {
    getGeometryEditPanel().zoomToInput();
    getGeometryEditPanel2().zoomToInput();
  }
      

  public static void inspectGeometry()
  {
    SPTDataLabBuilderFrame.instance().actionInspectGeometry();
  }
  public static void exchangeGeometry()
  {
    SPTDataLabBuilderFrame.instance().actionExchangeGeoms();
  }
  
  private static TestBuilderModel model() {
    return SPTDataLabBuilderFrame.instance().getModel();
  }
  
  private static SPTDataLabBuilderToolBar toolbar() {
    return SPTDataLabBuilderFrame.instance().getToolbar();
  }
  

}
