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

import ua.ieeta.sptdatalab.ui.SwingUtil;
import java.io.*;
import java.util.*;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.*;
import org.locationtech.jts.math.MathUtil;
import org.locationtech.jts.util.Assert;
import ua.ieeta.sptdatalab.test.TestCaseList;
import ua.ieeta.sptdatalab.test.Testable;
import ua.ieeta.sptdatalab.app.AppConstants;
import ua.ieeta.sptdatalab.ui.style.BasicStyle;
import ua.ieeta.sptdatalab.ui.style.CostumBasicStyle;
import ua.ieeta.sptdatalab.util.StringUtil;
import ua.ieeta.sptdatalab.util.io.IOUtil;
import ua.ieeta.sptdatalab.util.io.MultiFormatReader;


public class TestBuilderModel 
{
    private PrecisionModel precisionModel = new PrecisionModel();
    private GeometryFactory geometryFactory = null;
    private GeometryEditModel geomEditModel;
    private LayerList layerList = new LayerList();
    private WKTWriter writer = new WKTWriter();
    private Object currResult = null;
    private String opName = "";
    private boolean isSecondPanelModel = false;

    public TestBuilderModel(boolean isSecondPanelModel)
    {
        this.isSecondPanelModel = isSecondPanelModel;
        geomEditModel = new GeometryEditModel();
        initLayers();
        caseList.init();
    }

    public GeometryEditModel getGeometryEditModel() { return geomEditModel; }

    public PrecisionModel getPrecisionModel() { return precisionModel; }
	
    public void setPrecisionModel(PrecisionModel precisionModel)
    {
      this.precisionModel = precisionModel;
      geometryFactory = null;
    }

    public GeometryFactory getGeometryFactory()
    {
      if (geometryFactory == null)
        geometryFactory = new GeometryFactory(getPrecisionModel());
      return geometryFactory;
    }
  
  
    public String getResultDisplayString(Geometry g)
    {
        if (g == null) return "";
        if (g.getNumPoints() > DisplayParameters.MAX_DISPLAY_POINTS)
            return GeometryEditModel.toStringVeryLarge(g);
	return writer.writeFormatted(g);
    }
	
    public LayerList getLayers() { return layerList; }

    private void initLayers()
    {  	
  	GeometryContainer geomCont0 = new IndexedGeometryContainer(geomEditModel, 0);
  	GeometryContainer geomCont1 = new IndexedGeometryContainer(geomEditModel, 1);
        GeometryContainer geomCont2 = new IndexedGeometryContainer(geomEditModel, 2);
  	
        layerList.getLayer(LayerList.LYR_A).setSource(geomCont0);
        layerList.getLayer(LayerList.LYR_B).setSource(geomCont1);
        layerList.getLayer(LayerList.LYR_RESULT).setSource(geomCont2);

        if (geomEditModel != null)
          layerList.getLayer(LayerList.LYR_RESULT).setSource(
              new ResultGeometryContainer(geomEditModel));

        Layer lyrA = layerList.getLayer(LayerList.LYR_A);
        lyrA.setGeometryStyle(new BasicStyle(GeometryDepiction.GEOM_A_LINE_CLR,
            GeometryDepiction.GEOM_A_FILL_CLR));

        //using a new created model for the geometry... one that uses a background
        //the lines and the fill of the geometry is transparent
        Layer lyrB = layerList.getLayer(LayerList.LYR_B);
        lyrB.setGeometryStyle(new CostumBasicStyle(GeometryDepiction.GEOM_TRANSPARENT, this.isSecondPanelModel));

        Layer lyrR = layerList.getLayer(LayerList.LYR_RESULT);
        lyrR.setGeometryStyle(new BasicStyle(GeometryDepiction.GEOM_RESULT_LINE_CLR,
            GeometryDepiction.GEOM_RESULT_FILL_CLR));
    }

    public void pasteGeometry(int geomIndex)
  	throws Exception
    {
  	Object obj = SwingUtil.getFromClipboard();
  	Geometry g = null;
  	if (obj instanceof String) {
  		g = readGeometryText((String) obj);
  	}
  	else
  		g = (Geometry) obj;
  	
    TestCaseEdit testCaseEdit = getCurrentCase();
    testCaseEdit.setGeometry(geomIndex, g);
    getGeometryEditModel().setTestCase(testCaseEdit);
  }
  
  private Geometry readGeometryText(String geomStr) 
  throws Exception
  {
    Geometry g = null;
    if (geomStr.length() > 0) {
      try {
        MultiFormatReader reader = new MultiFormatReader(getGeometryFactory());
        g = reader.read(geomStr);
      } catch (ParseException ex) {
        String msg = "Unable to parse data: '" + condense(geomStr) + "'";  
        throw new IllegalArgumentException(msg); 
      }
    }
    return g;
  }

  private String condense(String str) {
    final int N_START = 10;
    final int N_END = 10;
    int len = str.length();
    if (len <= N_START + N_END + 10) return str;
    return str.substring(0, N_START)
        + "..."
        + str.substring(len - N_START, len);
  }

  public void loadMultipleGeometriesFromFile(int geomIndex, String filename)
  throws Exception 
  {
    Geometry g = IOUtil.readFile(filename, getGeometryFactory());
    TestCaseEdit testCaseEdit = getCurrentCase();
    testCaseEdit.setGeometry(geomIndex, g);
    testCaseEdit.setName(filename);
    getGeometryEditModel().setTestCase(testCaseEdit);
  }
  
  //Updates the geometry in panel (previously, updated A and B geometry, now only A because B is the image)
  public void loadGeometryText(String wktA) throws ParseException, IOException {
    MultiFormatReader reader = new MultiFormatReader(new GeometryFactory(getPrecisionModel(),0));

    // read geom A
    Geometry g0 = null;
    if (wktA.length() > 0) {
      g0 = reader.read(wktA);
    }
    
    // read geom B
    /*Geometry g1 = null;
    if (wktB.length() > 0) {
      g1 = reader.read(wktB);
    }*/
    
    TestCaseEdit testCaseEdit = getCurrentCase();
    testCaseEdit.setGeometry(0, g0);
    //testCaseEdit.setGeometry(1, g1);
    getGeometryEditModel().setTestCase(testCaseEdit);
  }



  /*
  public Geometry readMultipleGeometriesFromFile(String filename)
  throws Exception, IOException 
  {
    String ext = FileUtil.extension(filename);
    if (ext.equalsIgnoreCase("shp"))
      return readMultipleGeometriesFromShapefile(filename);
    return readMultipleGeometryFromWKT(filename);
  }
    
  private Geometry readMultipleGeometriesFromShapefile(String filename)
  throws Exception 
  {
    Shapefile shpfile = new Shapefile(new FileInputStream(filename));
    GeometryFactory geomFact = getGeometryFactory();
    shpfile.readStream(geomFact);
    List geomList = new ArrayList();
    do {
      Geometry geom = shpfile.next();
      if (geom == null)
        break;
      geomList.add(geom);
    } while (true);
    
    return geomFact.createGeometryCollection(GeometryFactory.toGeometryArray(geomList));
  }
  
  private Geometry readMultipleGeometryFromWKT(String filename)
  throws ParseException, IOException 
  {
    return readMultipleGeometryFromWKTString(FileUtil.readText(filename));
  }
  
  private Geometry readMultipleGeometryFromWKTString(String geoms)
  throws ParseException, IOException 
  {
    GeometryFactory geomFact = getGeometryFactory();
    WKTReader reader = new WKTReader(geomFact);
    WKTFileReader fileReader = new WKTFileReader(new StringReader(geoms), reader);
    List geomList = fileReader.read();
    
    if (geomList.size() == 1)
      return (Geometry) geomList.get(0);
    
    // TODO: turn polygons into a GC   
    return geomFact.buildGeometry(geomList);
  }
  
  */
  
  //=============================================================
  
  private CaseList caseList = new CaseList(new CaseList.CaseFactory() {
    public TestCaseEdit create() {
      return new TestCaseEdit(precisionModel);
    }
  });

  public CaseList cases() {
    return caseList;
  }
  public TestCaseEdit getCurrentCase() {
    return caseList.getCurrentCase();
  }
  public int getCurrentCaseIndex() {
    return caseList.getCurrentTestIndex();
  }
  public int getCasesSize() {
    return caseList.getSize();
  }
  public List getCases() {
    return caseList.getCases();
  }
  public void addCase(Geometry[] geoms) {
    addCase(geoms, null);
  }

  public void addCase(Geometry[] geoms, String name) {
    TestCaseEdit tc = new TestCaseEdit(geoms, name);
    caseList.addCase(tc);
  }



  public void loadEditList(TestCaseList tcl) throws ParseException {
    TestCaseList newTcl = new TestCaseList();
    for (Iterator i = tcl.getList().iterator(); i.hasNext();) {
      Testable tc = (Testable) i.next();

      if (tc instanceof TestCaseEdit) {
        newTcl.add((TestCaseEdit) tc);
      } else {
        newTcl.add(new TestCaseEdit(tc));
      }
    }
    caseList.init(newTcl);
  }




  private List parseErrors = null;

  /**
   * 
   * @return empy list if no errors
   */
  public List getParsingProblems()
  {
    return parseErrors; 
  }
  
  public boolean hasParseErrors()
  {
    if (parseErrors == null) return false;
    return parseErrors.size() > 0;
  }
 
  public void setResult(Object result)
  {
  	currResult = result;
    if (result == null || result instanceof Geometry) {
    	getCurrentCase().setResult((Geometry) result);
    }
  }
  
  public Object getResult()
  {
  	return currResult;
  }
  public void setOpName(String opName)
  {
    if (opName == null) {
      this.opName = "";
    }
    else { 
      this.opName = StringUtil.capitalize(opName);
    }
  }
  
  public String getOpName()
  {
    return opName;
  }
  
  public void copyResult(boolean isFormatted)
  {
    SwingUtil.copyToClipboard(currResult, isFormatted);
  }

  private ArrayList wktABeforePMChange = new ArrayList();
  private ArrayList wktBBeforePMChange = new ArrayList();

  public void changePrecisionModel(PrecisionModel precisionModel)
  throws ParseException
  {
    saveWKTBeforePMChange();
    setPrecisionModel(precisionModel);
    loadWKTAfterPMChange();
  }
  
  private void saveWKTBeforePMChange() {
    wktABeforePMChange.clear();
    wktBBeforePMChange.clear();
    for (Iterator i = getCases().iterator(); i.hasNext(); ) {
      Testable testable = (Testable) i.next();
      Geometry a = testable.getGeometry(0);
      Geometry b = testable.getGeometry(1);
      wktABeforePMChange.add(a != null ? a.toText() : null);
      wktBBeforePMChange.add(b != null ? b.toText() : null);
    }
  }

  private void loadWKTAfterPMChange() throws ParseException {
    WKTReader reader = new WKTReader(new GeometryFactory(getPrecisionModel(), 0));
    for (int i = 0; i < getCases().size(); i++) {
      Testable testable = (Testable) getCases().get(i);
      String wktA = (String) wktABeforePMChange.get(i);
      String wktB = (String) wktBBeforePMChange.get(i);
      testable.setGeometry(0, wktA != null ? reader.read(wktA) : null);
      testable.setGeometry(1, wktB != null ? reader.read(wktB) : null);
    }
  }

  /**
   * Encapsulates test case cursor logic. 
   * @author Martin Davis
   *
   */
  public static class CaseList {
    
    public static interface CaseFactory {
      TestCaseEdit create();
    }

    private TestCaseList tcList = new TestCaseList();
    private int tcIndex = -1;
    private CaseFactory caseFactory;
  
    public CaseList(CaseFactory caseFactory) {
      this.caseFactory = caseFactory;
    }
    public void init()
    {
      tcList = new TestCaseList();
      // ensure that there is always a valid TestCase in the list
      createNew();
    }
    
    public void init(TestCaseList tcl) {
      tcList = tcl;
      if (tcList.size() > 0) {
        tcIndex = 0;
      }
      else {
        createNew();
      }
    }
  
    public List getCases() {
      return Collections.unmodifiableList(tcList.getList());
    }
  
    public void setCurrent(TestCaseEdit testCase) {
      for (int i = 0; i < tcList.size(); i++) {
        if (tcList.get(i) == testCase) {
          tcIndex = i;
          return;
        }
      }
    }
    
    public TestCaseEdit getCurrentCase()
    {
      return (TestCaseEdit) getCurrentTestable();
    }
    
    public Testable getCurrentTestable() {
      return (TestCaseEdit) tcList.get(tcIndex);
    }
  
    public int getCurrentTestIndex()
    {
      return tcIndex;
    }
    public void setCurrentTestIndex(int i) {
      tcIndex = MathUtil.clamp(i,  0, getSize() -1 );
    }
    public TestCaseList getTestList()
    {
      return tcList;
    }
    
    public int getSize()
    {
      return tcList.getList().size();
    }
    public void prevCase() {
      if (tcIndex > 0)
        tcIndex--;
    }
  
    public void nextCase() {
      if (tcIndex < tcList.size() - 1)
        tcIndex++;
    }
  
    public void copyCase() {
      TestCaseEdit copy = null;
      copy = new TestCaseEdit(getCurrentCase());
      addCase(copy);
    }
    
    public void createNew() {
      addCase( caseFactory.create());
    }
    
    private void addCase(TestCaseEdit testcase) {
      if (tcIndex < 0) {
        tcList.add(testcase);
      }
      else {
        tcList.add(testcase, tcIndex+1);
      }
      tcIndex++;
    }
  
    public void deleteCase() {
      tcList.remove(tcIndex);
      if (tcList.size() == 0) {
        createNew();
      }
      if (tcIndex >= tcList.size())
        tcIndex = tcList.size() - 1;
    }  
  
  }

    public boolean isIsSecondPanelModel() {
        return isSecondPanelModel;
    }


}


