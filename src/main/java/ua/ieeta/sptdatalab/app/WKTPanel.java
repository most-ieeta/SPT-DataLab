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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.locationtech.jts.geom.Geometry;
import ua.ieeta.sptdatalab.controller.SPTDataLabBuilderController;
import ua.ieeta.sptdatalab.model.DisplayParameters;
import ua.ieeta.sptdatalab.model.GeometryEditModel;
import ua.ieeta.sptdatalab.model.GeometryType;
import ua.ieeta.sptdatalab.model.TestBuilderModel;
import ua.ieeta.sptdatalab.ui.SwingUtil;
import ua.ieeta.sptdatalab.util.GeometryTextCleaner;
import ua.ieeta.sptdatalab.util.io.MultiFormatReader;

/**
 * @version 1.7
 */
public class WKTPanel extends JPanel {

    TestBuilderModel tbModel;
    TestBuilderModel tbModel2;

    GridBagLayout gridBagLayout1 = new GridBagLayout();
    Box panelButtons = Box.createVerticalBox();
    JPanel panelAB = new JPanel();
    JButton loadButton = new JButton();
    TitledBorder titledBorder1;
    JLabel bLabel = new JLabel();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JLabel aLabel = new JLabel();

    JPanel aPanel = new JPanel();
    JButton aCopyButton = new JButton();
    JButton aPasteButton = new JButton();
    JButton aClearButton = new JButton();
    Box aLabelPanel = Box.createVerticalBox();
    Box aButtonPanel = Box.createVerticalBox();
    FlowLayout aButtonPanelLayout = new FlowLayout();
    BorderLayout aPanelLayout = new BorderLayout();
    JRadioButton aRB = new JRadioButton();

    JPanel bPanel = new JPanel();
    JButton bCopyButton = new JButton();
    JButton bPasteButton = new JButton();
    JButton bClearButton = new JButton();
    Box bLabelPanel = Box.createVerticalBox();
    Box bButtonPanel = Box.createVerticalBox();
    FlowLayout bButtonPanelLayout = new FlowLayout();
    BorderLayout bPanelLayout = new BorderLayout();
    JRadioButton bRB = new JRadioButton();

    JScrollPane aScrollPane = new JScrollPane();
    JTextArea aTextArea = new JTextArea();
    JScrollPane bScrollPane = new JScrollPane();
    JTextArea bTextArea = new JTextArea();
    ButtonGroup editMode = new ButtonGroup();

    private final ImageIcon copyIcon = new ImageIcon(this.getClass().getResource("Copy.png"));
    private final ImageIcon pasteIcon = new ImageIcon(this.getClass().getResource("Paste.png"));
    private final ImageIcon cutIcon = new ImageIcon(this.getClass().getResource("Delete_small.png"));
    private final ImageIcon loadIcon = new ImageIcon(this.getClass().getResource("LoadWKTToTest.png"));
    
    protected SPTDataLabBuilderFrame tbFrame;

    public WKTPanel(SPTDataLabBuilderFrame tbFrame) {
        this.tbFrame = tbFrame;
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setModel(TestBuilderModel tbModel, TestBuilderModel tbModel2) {
        this.tbModel = tbModel;
        this.tbModel2 = tbModel2;
        setFocusGeometry(0);
    }

    void jbInit() throws Exception {
        titledBorder1 = new TitledBorder("");
        this.setLayout(gridBagLayout1);
        this.setPreferredSize(new java.awt.Dimension(394, 176));

        loadButton.setPreferredSize(new Dimension(38, 38));
        loadButton.setMargin(new Insets(8, 8, 8, 8));
       
        loadButton.setIcon(loadIcon);
        loadButton.setToolTipText(AppStrings.TIP_WKT_PANEL_LOAD_GEOMETRY);

        panelAB.setLayout(gridBagLayout2);

        aLabel.setFont(new java.awt.Font("Dialog", 1, 16));
        aLabel.setForeground(Color.blue);
        aLabel.setText("A");
        aLabel.setPreferredSize(new Dimension(20, 20));
        aLabel.setHorizontalTextPosition(SwingConstants.LEFT);

        bLabel.setFont(new java.awt.Font("Dialog", 1, 16));
        bLabel.setForeground(Color.red);
        bLabel.setText("B");
        bLabel.setPreferredSize(new Dimension(20, 20));

        aScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        aTextArea.setWrapStyleWord(true);
        aTextArea.setLineWrap(true);
        aTextArea.setBackground(Color.white);
        aTextArea.setFont(new java.awt.Font("Monospaced", 0, 12));
        aTextArea.setToolTipText(AppStrings.TIP_TEXT_ENTRY);
        aTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                setFocusGeometry(0);
            }
        });

        bScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        bTextArea.setWrapStyleWord(true);
        bTextArea.setLineWrap(true);
        bTextArea.setBackground(Color.white);
        bTextArea.setFont(new java.awt.Font("Monospaced", 0, 12));
        bTextArea.setToolTipText(AppStrings.TIP_TEXT_ENTRY);
        bTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                setFocusGeometry(1);
            }
        });

        aCopyButton.setToolTipText(AppStrings.TIP_COPY_DATA);
        aCopyButton.setIcon(copyIcon);
        aCopyButton.setMargin(new Insets(0, 0, 0, 0));

        aPasteButton.setToolTipText(AppStrings.TIP_PASTE_DATA);
        aPasteButton.setIcon(pasteIcon);
        aPasteButton.setMargin(new Insets(0, 0, 0, 0));

        aClearButton.setToolTipText("Clear");
        aClearButton.setIcon(cutIcon);
        aClearButton.setMargin(new Insets(0, 0, 0, 0));

        aButtonPanelLayout.setVgap(1);
        aButtonPanelLayout.setHgap(1);
//        aButtonPanel.setLayout(aButtonPanelLayout);
        aButtonPanel.add(aPasteButton);
        aButtonPanel.add(aCopyButton);
        aButtonPanel.add(aClearButton);

        aLabel.setAlignmentX(LEFT_ALIGNMENT);
        aRB.setAlignmentX(LEFT_ALIGNMENT);
        aRB.setSelected(true);
        aRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFocusGeometry(0);
            }
        });
//aLabelPanel.add(aRB);
        aLabelPanel.add(aLabel);
        aLabelPanel.add(aButtonPanel);

        aPanel.setLayout(aPanelLayout);
        aPanel.add(aLabelPanel, BorderLayout.WEST);
        aPanel.add(aScrollPane, BorderLayout.CENTER);
//aPanel.add(aButtonPanel, BorderLayout.EAST);

        bCopyButton.setToolTipText(AppStrings.TIP_COPY_DATA);
        bCopyButton.setIcon(copyIcon);
        bCopyButton.setMargin(new Insets(0, 0, 0, 0));

        bPasteButton.setToolTipText(AppStrings.TIP_PASTE_DATA);
        bPasteButton.setIcon(pasteIcon);
        bPasteButton.setMargin(new Insets(0, 0, 0, 0));

        bClearButton.setToolTipText("Clear");
        bClearButton.setIcon(cutIcon);
        bClearButton.setMargin(new Insets(0, 0, 0, 0));

        bButtonPanelLayout.setVgap(1);
        bButtonPanelLayout.setHgap(1);
//        bButtonPanel.setLayout(bButtonPanelLayout);
        bButtonPanel.add(bPasteButton);
        bButtonPanel.add(bCopyButton);
        bButtonPanel.add(bClearButton);

        bLabel.setAlignmentX(LEFT_ALIGNMENT);
//bLabelPanel.add(bRB);
        bRB.setAlignmentX(LEFT_ALIGNMENT);
        bRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFocusGeometry(1);
            }
        });
        bLabelPanel.add(bLabel);
        bLabelPanel.add(bButtonPanel);

        bPanel.setLayout(bPanelLayout);
        bPanel.add(bLabelPanel, BorderLayout.WEST);
        bPanel.add(bScrollPane, BorderLayout.CENTER);
//bPanel.add(bButtonPanel, BorderLayout.EAST);

        this.add(
                panelAB,
                new GridBagConstraints(0, 1, 1, 2,
                        1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0),
                        0, 0));
        panelAB.add(
                aPanel,
                new GridBagConstraints(1, 0, 1, 1,
                        1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0),
                        0, 0));
        panelAB.add(
                bPanel,
                new GridBagConstraints(1, 1, 1, 1,
                        1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0),
                        0, 0));
        bScrollPane.getViewport().add(bTextArea, null);
        aScrollPane.getViewport().add(aTextArea, null);

        panelButtons.add(loadButton);

        this.add(
                panelButtons,
                new GridBagConstraints(1, 1, 1, 1,
                        0.0, 0.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE,
                        new Insets(2, 2, 0, 2),
                        0, 0));

        loadButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadButton_actionPerformed(e);
            }
        });
        aCopyButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aCopyButton_actionPerformed(e);
            }
        });
        aPasteButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aPasteButton_actionPerformed(e);
            }
        });
        aClearButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aClearButton_actionPerformed(e);
            }
        });
        bCopyButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bCopyButton_actionPerformed(e);
            }
        });
        bPasteButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bPasteButton_actionPerformed(e);
            }
        });
        bClearButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bClearButton_actionPerformed(e);
            }
        });
        editMode.add(aRB);
        editMode.add(bRB);
    }

    public void setText(Geometry g, int geomIndex) {
        String txt = null;
        if (g == null) {
            txt = "";
        } else if (g.getNumPoints() > DisplayParameters.MAX_DISPLAY_POINTS) {
            txt = GeometryEditModel.toStringVeryLarge(g);
        } else {
            txt = GeometryEditModel.getText(g, GeometryType.WELLKNOWNTEXT);
        }

        switch (geomIndex) {
            case 0:
                aTextArea.setText(txt);
                break;
            case 1:
                bTextArea.setText(txt);
                break;
        }
    }

    public String getGeometryTextA() {
        return aTextArea.getText();
    }

    public String getGeometryTextB() {
        return bTextArea.getText();
    }

    public String getGeometryText(int geomIndex) {
        if (geomIndex == 0) {
            return aTextArea.getText();
        }
        return bTextArea.getText();
    }

    public String getGeometryTextClean(int geomIndex) {
        String text = getGeometryText(geomIndex);
        String textTrim = text.trim();
        if (textTrim.length() == 0) {
            return textTrim;
        }
        String textClean = textTrim;
        switch (MultiFormatReader.format(textTrim)) {
            case MultiFormatReader.FORMAT_WKT:
                textClean = GeometryTextCleaner.cleanWKT(textTrim);
                break;
        }
        return textClean;
    }

    void aTextArea_keyTyped(KeyEvent e) {
        loadButton.setEnabled(true);
    }

    void bTextArea_keyTyped(KeyEvent e) {
        loadButton.setEnabled(true);
    }

    void loadButton_actionPerformed(ActionEvent e) {
        try {
            //get wkt text (original coords) update the lists with that info, transform, and pass the transformed coordinates to the model to draw
            String geo1 = getGeometryTextClean(0);
            String geo2 = getGeometryTextClean(1);
            if (geo1.length() == 0 || geo2.length() == 0){
                JOptionPane.showMessageDialog(null, "You should write a WKT before loading it.");
                return;
            }
                
            AppCorrGeometries.getInstance().updateGeometriesFromWKTPanel(geo1, geo2);
            String[] wkts = AppCorrGeometries.getInstance().getWKTextFromGeometriesInPanelsScreenCoordinates();
            tbModel.loadGeometryText(wkts[0]);//load content of top wkt panel to 1st panel
            tbModel2.loadGeometryText(wkts[1]);//load content of bottom wkt panel to 2nd panel
        } catch (Exception ex) {
            SwingUtil.reportException(this, ex);
        }
    }

    void aCopyButton_actionPerformed(ActionEvent e) {
        copy(e, 0);
    }

    void bCopyButton_actionPerformed(ActionEvent e) {
        copy(e, 1);
    }

    void copy(ActionEvent e, int geomIndex) {
        boolean isFormatted = 0 != (e.getModifiers() & ActionEvent.CTRL_MASK);
        Geometry g = tbModel.getCurrentCase().getGeometry(geomIndex);
        if (g != null) {
            SwingUtil.copyToClipboard(g, isFormatted);
        }
    }

    void aPasteButton_actionPerformed(ActionEvent e) {
        this.aTextArea.setText(getClipboardContents());
    }

    void bPasteButton_actionPerformed(ActionEvent e) {
        this.bTextArea.setText(getClipboardContents());
    }

    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText
                = (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex) {
                //highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        return result;
    }

    void aClearButton_actionPerformed(ActionEvent e) {
        tbModel.getGeometryEditModel().clear(0);
        aTextArea.setText("");
        SPTDataLabBuilderFrame.instance().enableDrawingButtons();
    }

    void bClearButton_actionPerformed(ActionEvent e) {
        //tbModel.getGeometryEditModel().clear(1);
        tbModel2.getGeometryEditModel().clear(0);
        bTextArea.setText("");
        SPTDataLabBuilderFrame.instance().enableDrawingButtons();
    }

    Border focusBorder = BorderFactory.createMatteBorder(0, 2, 0, 0, Color.green);
    //Border otherBorder = BorderFactory.createEmptyBorder();
    Border otherBorder = BorderFactory.createMatteBorder(0, 2, 0, 0, Color.white);

    private static Color focusBackgroundColor = Color.white; //new Color(240,255,250);
    private static Color otherBackgroundColor = SystemColor.control;

    private void setFocusGeometry(int index) {
        //SPTDataLabBuilderController.setFocusGeometry(index);

        JTextArea focusTA = index == 0 ? aTextArea : bTextArea;
        JTextArea otherTA = index == 0 ? bTextArea : aTextArea;
        //focusTA.setBorder(focusBorder);
        //otherTA.setBorder(otherBorder);

        focusTA.setBackground(focusBackgroundColor);
        otherTA.setBackground(otherBackgroundColor);
        repaint();
    }

}
