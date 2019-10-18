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

import ua.ieeta.sptdatalab.app.SPTDataLab;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderFrame;
import ua.ieeta.sptdatalab.event.SpatialFunctionPanelEvent;
import ua.ieeta.sptdatalab.model.TestBuilderModel;


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
