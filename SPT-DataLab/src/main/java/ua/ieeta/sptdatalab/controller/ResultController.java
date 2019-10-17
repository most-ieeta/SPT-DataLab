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

package ua.ieeta.sptdatalab.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.Timer;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.util.Stopwatch;
import ua.ieeta.sptdatalab.geomfunction.GeometryFunction;
import ua.ieeta.sptdatalab.geomfunction.GeometryFunctionInvocation;
import ua.ieeta.sptdatalab.app.SPTDataLab;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderFrame;
import ua.ieeta.sptdatalab.event.SpatialFunctionPanelEvent;
import ua.ieeta.sptdatalab.model.TestBuilderModel;
import ua.ieeta.sptdatalab.ui.SwingWorker;


public class ResultController 
{
  private static NumberFormat timeFmt;
  static {
    timeFmt = NumberFormat.getNumberInstance();
    timeFmt.setMinimumFractionDigits(3);
  }

	SPTDataLabBuilderFrame frame;
	TestBuilderModel model = null;
	
	public ResultController(SPTDataLabBuilderFrame frame)
	{
		this.frame = frame;
		model = SPTDataLab.model();
	}
	
  
  

  private void clearResult()
  {
    frame.getResultWKTPanel().clearResult();
    // for good measure do a GC
    System.gc();
    updateResult(null,null,null);
  }
  	
  /**
   * If result is null, clears result info.
   * 
   * @param result
   * @param object 
   * @param object 
   * @param timer
   */
  private void resetUI() {
     frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }
  private void updateResult(GeometryFunctionInvocation functionDesc, Object result, Stopwatch timer) {
     model.setResult(result);
     String timeString = timer != null ? timer.getTimeString() : "";
     frame.getResultWKTPanel().setExecutedTime(timeString);
     frame.getResultWKTPanel().updateResult();
     SPTDataLabBuilderController.geometryViewChanged();
     // log it
     if (result != null) {
       resultLogEntry(functionDesc, timeString, result);
     }
   }

  private void resultLogEntry(GeometryFunctionInvocation functionDesc, String timeString, Object result) {
    String funDesc = functionDesc.getSignature() + " : " + timeString;
    String resultDesc = GeometryFunctionInvocation.toString(result);
    SPTDataLabBuilderFrame.instance().displayInfo(
        funDesc + "\n ==> " + resultDesc,
        false);
  }
  
  private SwingWorker worker = null;
  
  private void runFunctionWorker(final GeometryFunctionInvocation functionInvoc, final boolean createNew)
  {
    worker = new SwingWorker() {
    	Stopwatch timer;
    	
      public Object construct()
      {
        return computeResult();
      }
      
      private Object computeResult() {
        Object result = null;
        GeometryFunction currentFunc = functionInvoc.getFunction();
        if (currentFunc == null)
          return null;
        
        try {
          timer = new Stopwatch();
          try {
            result = currentFunc.invoke(model.getGeometryEditModel()
                .getGeometry(0), functionInvoc.getArgs());
          } finally {
            timer.stop();
          }
          // result = currentState.getActualValue();
        }
        catch (Exception ex) {
          ex.printStackTrace(System.out);
          result = ex;
        }
        return result;
      }

      public void finished() {
        stopFunctionMonitor();
        resetUI();
        Object result = getValue();
        if (createNew) {
          String desc = "Result of " + functionInvoc.getSignature();
          SPTDataLabBuilderController.addTestCase(new Geometry[] { (Geometry) result, null }, desc);          
        } else {
          updateResult(functionInvoc, result, timer);
        }
        worker = null;
      }
    };
    worker.start();
  }
  
  private void clearFunctionWorker()
  {
    
  }
  
  private Timer funcTimer;
  private long runMillis = 0;
  private static final int TIMER_DELAY_IN_MILLIS = 10;
  
  private void startFunctionMonitor()
  {
    runMillis = 0;
    funcTimer = new Timer(TIMER_DELAY_IN_MILLIS, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
//        Stopwatch timer = testCasePanel.getSpatialFunctionPanel().getTimer();
        runMillis += TIMER_DELAY_IN_MILLIS;
        String timeStr = "";
        if (runMillis < 10000) {
          timeStr = runMillis + " ms";
        }
        else {
          timeStr = timeFmt.format(runMillis/1000.0) + " s";
        }
        frame.getResultWKTPanel().setRunningTime(timeStr);
      }
    });
    funcTimer.setInitialDelay(0);
    funcTimer.start(); 
  }
  
  private void stopFunctionMonitor()
  {
    funcTimer.stop();
  }

  public void scalarFunctionPanel_functionExecuted(SpatialFunctionPanelEvent e) 
  {
    /**
     * For now scalar functions are executed on the calling thread.
     * They are expected to be of short duration
     */
    // initialize UI view
    //frame.getResultValuePanel().setResult(opName, "", null);
    
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    
    //frame.getResultValuePanel().setResult(opName, timer.getTimeString(), result);
    frame.showResultValueTab();
  }
  
  public void scalarFunctionPanel2_functionExecuted(SpatialFunctionPanelEvent e) 
  {
    /**
     * For now scalar functions are executed on the calling thread.
     * They are expected to be of short duration
     */
    // initialize UI view
    //frame.getResultValuePanel().setResult(opName, "", null);
    
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    
    //frame.getResultValuePanel().setResult(opName, timer.getTimeString(), result);
    frame.showResultValueTab();
  }


}
