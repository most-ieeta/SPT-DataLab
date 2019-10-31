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
package ua.ieeta.sptdatalab.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class SPTDataLabBuilderMenuBar 
{
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuAbout = new JMenuItem();
  JMenu jMenuView = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuEdit = new JMenu();
  JMenuItem changeDataSet = new JMenuItem();
  JMenuItem saveCurrent = new JMenuItem();
  JMenuItem saveCurrentAs = new JMenuItem();
  JMenuItem saveAll = new JMenuItem();
  JMenuItem saveAllAs = new JMenuItem();

  SPTDataLabBuilderFrame tbFrame;
  
  public SPTDataLabBuilderMenuBar(SPTDataLabBuilderFrame tbFrame) 
  {
    this.tbFrame = tbFrame;
  }

  public JMenuBar getMenuBar()
  {
    jMenuAbout.setText("About");
    jMenuAbout.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tbFrame.jMenuHelpAbout_actionPerformed(e);
        }
      });

    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tbFrame.jMenuFileExit_actionPerformed(e);
        }
      });
    
    changeDataSet.setText("Change Dataset...");
    changeDataSet.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          //ask user to save work (if there are unsaved changes)
          tbFrame.showSavePromptDatasetChange();
          tbFrame.resetZoom();
          tbFrame.menuChangeDataSet_actionPerformed(e);
          tbFrame.setTextForImageNumberLabels();
          tbFrame.legendPanel.updateFilesInLegend();
        }
      });
    
    saveCurrent.setText("Save Current");
    saveCurrentAs.setText("Save Current As...");
    saveCurrentAs.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tbFrame.saveGeometriesAs();
        }
      });
    saveAll.setText("Save All Edited");
    saveAll.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tbFrame.saveAllGeometries();
        }
      });
    saveAllAs.setText("Save All Edited As...");
    saveAllAs.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          tbFrame.saveAllGeometriesAs();
        }
      });
    jMenuFile.setText("File");


    jMenuFile.add(this.changeDataSet);
    jMenuFile.add(this.saveCurrent);
    jMenuFile.add(this.saveCurrentAs);
    jMenuFile.add(this.saveAll);
    jMenuFile.add(this.saveAllAs);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    //==========================
    
    jMenuHelp.setText("Help");
    jMenuHelp.add(jMenuAbout);
    
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);

    return jMenuBar1;
  }

  JMenuItem menuItemCheck(String name, boolean init) {
    return createMenuItemSelectable(new JCheckBoxMenuItem(), name, init, null);
  }
  JMenuItem menuItemCheck(String name, boolean init, ActionListener listener) {
    return createMenuItemSelectable(new JCheckBoxMenuItem(), name, init, listener);
  }
  JMenuItem menuItemRadio(String name, boolean init) {
    return createMenuItemSelectable(new JRadioButtonMenuItem(), name, init, null);
  }
  JMenuItem menuItemRadio(String name, boolean init, ActionListener listener) {
    return createMenuItemSelectable(new JRadioButtonMenuItem(), name, init, listener);
  }
  
  JMenuItem createMenuItemSelectable(JMenuItem item, String name, boolean init, ActionListener listener) {
    item.setText(name);
    item.setSelected(init);
    if (listener != null) {
      item.addActionListener(listener);
    }
    return item;
  }
}
