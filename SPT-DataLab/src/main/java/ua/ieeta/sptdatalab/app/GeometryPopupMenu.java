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

package ua.ieeta.sptdatalab.app;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.locationtech.jts.geom.Coordinate;
import ua.ieeta.sptdatalab.controller.SPTDataLabBuilderController;


public class GeometryPopupMenu extends JPopupMenu 
{
  Coordinate clickCoord;
  
  public GeometryPopupMenu(){
    initUI();
  }
  
  private void initUI()
  {
    JMenuItem extractComponentItem = new JMenuItem("Extract Component");
    extractComponentItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              SPTDataLabBuilderController.extractComponentsToTestCase(clickCoord);
            }
          });
    add(extractComponentItem);
    
    JMenuItem copyComponentItem = new JMenuItem("Copy Component");
    copyComponentItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              SPTDataLabBuilderController.copyComponentToClipboard(clickCoord);
            }
          });
    add(copyComponentItem);
    
    JMenuItem infoItem = new JMenuItem("Info");
    infoItem.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              SPTDataLabBuilderFrame.instance().displayInfo(clickCoord);
            }
          });
    add(infoItem);
    
  }
  
  /**
   * Record model coordinate of click point for use in menu operations
   */
  public void show(Component invoker, int x, int y)
  {
    GeometryEditPanel editPanel = (GeometryEditPanel) invoker;
    clickCoord = editPanel.getViewport().toModelCoordinate(new java.awt.Point(x, y));
    super.show(invoker, x, y);
  }
  
}

