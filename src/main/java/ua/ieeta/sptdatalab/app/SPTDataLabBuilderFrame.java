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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.util.Assert;
import ua.ieeta.sptdatalab.controller.SPTDataLabBuilderController;
import ua.ieeta.sptdatalab.model.DisplayParameters;
import ua.ieeta.sptdatalab.model.GeometryEditModel;
import ua.ieeta.sptdatalab.model.GeometryEvent;
import ua.ieeta.sptdatalab.model.TestBuilderModel;
import ua.ieeta.sptdatalab.model.TestCaseEdit;

import ua.ieeta.sptdatalab.ui.SwingUtil;
import ua.ieeta.sptdatalab.ui.tools.DeleteVertexTool;
import ua.ieeta.sptdatalab.ui.tools.EditVertexTool;
import ua.ieeta.sptdatalab.ui.tools.InfoTool;
import ua.ieeta.sptdatalab.ui.tools.PanTool;
import ua.ieeta.sptdatalab.ui.tools.RectangleTool;
import ua.ieeta.sptdatalab.ui.tools.StreamPolygonTool;
import ua.ieeta.sptdatalab.ui.tools.ZoomTool;
import ua.ieeta.sptdatalab.util.StringUtil;
import ua.ieeta.sptdatalab.util.io.DatasetLoader;

public class SPTDataLabBuilderFrame extends JFrame {

    private static SPTDataLabBuilderFrame singleton = null;
    private SPTDataLabBuilderMenuBar tbMenuBar = new SPTDataLabBuilderMenuBar(this);
    private SPTDataLabBuilderToolBar tbToolBar = new SPTDataLabBuilderToolBar(this);
    //---------------------------------------------
    JPanel contentPane;
    BorderLayout borderLayout1 = new BorderLayout();
    Border border4;
    JSplitPane jSplitPane1 = new JSplitPane();
    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    TestCasePanel testCasePanel = new TestCasePanel();
    //this will be the 2nd panel, on the right
    TestCasePanel testCasePanel2 = new TestCasePanel(true);
    JPanel jPanel2 = new JPanel();
    //Panel with legends on top of panels
    LegendPanel legendPanel = new LegendPanel();

    JTabbedPane inputTabbedPane = new JTabbedPane();
    BorderLayout borderLayout3 = new BorderLayout();
    JPanel testPanel = new JPanel();
    WKTPanel wktPanel = new WKTPanel(this);

    TestListPanel testListPanel = new TestListPanel(this);
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    GridLayout gridLayout1 = new GridLayout();
    
    JTabbedPane tabbedPane = new JTabbedPane();
    MorphingGeometryOptionsPanel morphingPanel = new MorphingGeometryOptionsPanel();

    private ZoomTool zoomTool;
    private final ImageIcon appIcon = new ImageIcon(this.getClass().getResource("app-icon.gif"));

    private JFileChooser fileChooser = new JFileChooser();
    private JFileChooser fileAndDirectoryChooser = new JFileChooser();
    private JFileChooser directoryChooser = new JFileChooser();

    TestBuilderModel tbModel;
    TestBuilderModel tbModel2;

    /**
     * Construct the frame
     */
    public SPTDataLabBuilderFrame() {
        try {
            Assert.isTrue(singleton == null);
            singleton = this;
            enableEvents(AWTEvent.WINDOW_EVENT_MASK);
            setIconImage(appIcon.getImage());
            AppCorrGeometries.getInstance().setFrame(this);
            jbInit();

            testCasePanel.cbMagnifyTopo.addActionListener(
                    new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    revealTopo_actionPerformed();
                }
            });

            testCasePanel2.cbMagnifyTopo.addActionListener(
                    new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    revealTopo2_actionPerformed();
                }
            });

            testCasePanel.spStretchDist
                    .addChangeListener(new javax.swing.event.ChangeListener() {
                        public void stateChanged(javax.swing.event.ChangeEvent e) {
                            revealTopo_actionPerformed();
                        }
                    });

            testCasePanel2.spStretchDist
                    .addChangeListener(new javax.swing.event.ChangeListener() {
                        public void stateChanged(javax.swing.event.ChangeEvent e) {
                            revealTopo2_actionPerformed();
                        }
                    });
            //zoom factor is changed here!
            zoomTool = new ZoomTool(2, AppCursors.ZOOM);
            showGeomsTab();
            testCasePanel.getGeometryEditPanel().setCurrentTool(InfoTool.getInstance());
            testCasePanel2.getGeometryEditPanel().setCurrentTool(InfoTool.getInstance());

            //if there are unsaved changes, prompt user to save them before closing app
            this.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    showSavePromptQuitApp();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSavePromptQuitApp() {
        Object[] options = {"Save All", "Save All As...",
            "Quit"};
        String message = "You have unsaved changes.\nWould you like to save all changes before closing"
                + " SPTDataLab?\n";
        showSavePrompt(options, message);
    }

    public void showSavePromptDatasetChange() {
        Object[] options = {"Save All", "Save All As...",
            "Continue without saving"};
        String message = "You have unsaved changes.\nWould you like to save all changes in the current dataset"
                + " before switching to a different dataset?\n";
        showSavePrompt(options, message);
    }

    public void showSavePrompt(Object[] options, String message) {
        if (AppCorrGeometries.getInstance().changesMade()) {
            int dialogButton = JOptionPane.YES_NO_CANCEL_OPTION;
            //options

            boolean finished = false; //used to avoid application closing if user cancels saving
            //quit only when either user pressed quit, or saveing was a success (not canceled by user)
            while (!finished) {
                int result = JOptionPane.showOptionDialog(null, message, "Unsaved changes", dialogButton,
                        JOptionPane.WARNING_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"), options, null);

                switch (result) {
                    case JOptionPane.YES_OPTION:
                        //save all
                        finished = saveAllGeometries();
                        break;
                    case JOptionPane.NO_OPTION:
                        //save all as
                        finished = saveAllGeometriesAs();
                        break;
                    default:
                        return;//close application (user selected quit)
                }
            }
        }
    }

    public static SPTDataLabBuilderFrame instance() {
        if (singleton == null) {
            new SPTDataLabBuilderFrame();
        }
        return singleton;
    }

    public static GeometryEditPanel getGeometryEditPanel() {
        return instance().getTestCasePanel().getGeometryEditPanel();
    }

    public static GeometryEditPanel getGeometryEditPanel2() {
        return instance().getTestCasePanel2().getGeometryEditPanel();
    }

    public static GeometryEditPanel getGeometryEditPanelMouseIsIn() {
        if (isMouseWithinComponent(instance().getTestCasePanel2())) {
            return instance().getTestCasePanel2().getGeometryEditPanel();
        } else {
            return instance().getTestCasePanel().getGeometryEditPanel();
        }
    }

    public static GeometryEditPanel getOtherGeometryEditPanel(GeometryEditPanel editPanel) {
        if (editPanel.equals(instance().getTestCasePanel().getGeometryEditPanel())) {
            return instance().getTestCasePanel2().getGeometryEditPanel();
        }
        return instance().getTestCasePanel().getGeometryEditPanel();
    }

    public static boolean isMouseWithinComponent(Component c) {
        java.awt.Point mousePos = new java.awt.Point(MouseInfo.getPointerInfo().getLocation());
        Rectangle bounds = c.getBounds();
        bounds.setLocation(c.getLocationOnScreen());
        return bounds.contains(mousePos);
    }

    public TestBuilderModel getModel() {
        return tbModel;
    }

    public TestBuilderModel getModel2() {
        return tbModel2;
    }

    public void setModel(TestBuilderModel model, TestBuilderModel model2) {
        tbModel = model;
        tbModel2 = model2;
        testCasePanel.setModel(tbModel);
        testCasePanel2.setModel(tbModel2);
        wktPanel.setModel(model, model2);
        
        model.getGeometryEditModel().addGeometryListener(new ua.ieeta.sptdatalab.model.GeometryListener() {
            public void geometryChanged(GeometryEvent e) {
                model_geometryChanged(e);
            }
        });
        model2.getGeometryEditModel().addGeometryListener(new ua.ieeta.sptdatalab.model.GeometryListener() {
            public void geometryChanged(GeometryEvent e) {
                model2_geometryChanged(e);
            }
        });

        testListPanel.populateList();
        updateTestCaseView();
    }

    public static void reportException(Exception e) {
        SwingUtil.reportException(instance(), e);
    }

    public void setCurrentTestCase(TestCaseEdit testCase) {
        tbModel.cases().setCurrent(testCase);
        tbModel2.cases().setCurrent(testCase);
        updateTestCaseView();
        SPTDataLabBuilderController.zoomToInput();
    }

    public TestCasePanel getTestCasePanel() {
        return testCasePanel;
    }

    public TestCasePanel getTestCasePanel2() {
        return testCasePanel2;
    }

    
    /**
     * File | Exit action performed
     */
    public void jMenuFileExit_actionPerformed(ActionEvent e) {
        System.exit(0);
    }

    /**
     * Help | About action performed
     */
    public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
        SPTDataLabBuilder_AboutBox dlg = new SPTDataLabBuilder_AboutBox(this);
        java.awt.Dimension dlgSize = dlg.getPreferredSize();
        java.awt.Dimension frmSize = getSize();
        java.awt.Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height
                - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.setVisible(true);
    }

    public void showTab(String name) {
        inputTabbedPane.setSelectedIndex(inputTabbedPane.indexOfTab(name));
    }

    public void showGeomsTab() {
        showTab(AppStrings.TAB_LABEL_INPUT);
    }

    public void showResultWKTTab() {
        showTab(AppStrings.TAB_LABEL_RESULT);
    }

    public void showResultValueTab() {
        showTab(AppStrings.TAB_LABEL_VALUE);
    }

    public void showInfoTab() {
        showTab(AppStrings.TAB_LABEL_LOG);
    }

    /**
     * Overridden so we can exit when window is closed
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            jMenuFileExit_actionPerformed(null);
        }
    }

    void model_geometryChanged(GeometryEvent e) {
        //testCasePanel.relatePanel.clearResults();
        SPTDataLabBuilderController.geometryViewChanged();
        updateWktTopPanel();
    }

    void model2_geometryChanged(GeometryEvent e) {
        //testCasePanel.relatePanel.clearResults();
        SPTDataLabBuilderController.geometryViewChanged2();
        updateWktBottomPanel();
    }

    void createNewCase() {
        tbModel.cases().createNew();
        tbModel2.cases().createNew();
        showGeomsTab();
        updateTestCases();
    }

    //increment index of the selected image and reload panels.
    void moveToNextImage() {
        AppImage.getInstance().loadNextImage();
        this.reloadBothPanels();
    }

    //decrement index of the selected image and reload panels.
    void moveToPreviousImage() {
        AppImage.getInstance().loadPreviousImage();
        this.reloadBothPanels();
    }

    public void reloadBothPanels() {
        getTestCasePanel().getGeometryEditPanel().cleanAndDrawGeometry();
        getTestCasePanel2().getGeometryEditPanel().cleanAndDrawGeometry();
        getTestCasePanel().getGeometryEditPanel().forceRepaint();
        getTestCasePanel2().getGeometryEditPanel().forceRepaint();
    }

    //select the image i in the list of images and selects the image i+1 for the panel 2.
    void movePanel1ToImage(int i) {
        boolean imageChanged = AppImage.getInstance().selectImageForPanel1(i);
        if (imageChanged) {
            this.reloadBothPanels();
            if (AppImage.getInstance().isLastImageForPanel1()) {
                tbToolBar.disableNextBtn();
            } else {
                tbToolBar.enableNextBtn();
            }
            if (AppImage.getInstance().isFirstImageForPanel1()) {
                tbToolBar.disablePreviousBtn();
            } else {
                tbToolBar.enablePreviousBtn();
            }
        }
    }

    //select the image i in the list of images and selects the image i+1 for the panel 2.
    void movePanel2ToImage(int i) {
        boolean imageChanged = AppImage.getInstance().selectImageForPanel2(i);
        if (imageChanged) {
            this.reloadBothPanels();
            if (AppImage.getInstance().isLastImageForPanel2()) {
                tbToolBar.disableNextBtn();
            } else {
                tbToolBar.enableNextBtn();
            }
            if (AppImage.getInstance().isFirstImageForPanel2()) {
                tbToolBar.disablePreviousBtn();
            } else {
                tbToolBar.enablePreviousBtn();
            }
        }
    }

    void setToFirstImages() {
        tbToolBar.setToFirstImages();
    }

    void setTextForImageNumberLabels() {
        tbToolBar.setTextForImageNumberLabels();
    }

    /*void moveToNextCase(boolean isZoom) {
    tbModel.cases().nextCase();
    tbModel2.cases().nextCase();
    updateTestCaseView();
    if (isZoom) SPTDataLabBuilderController.zoomToInput();
    }*/
 /*void copyCase() {
    tbModel.cases().copyCase();
    tbModel2.cases().copyCase();
    updateTestCases();
    }*/
    void saveGeometries() {
        //inform user this operation will overwrite all files in the dataset. Ask for confirmation to continue
        int dialogResult = JOptionPane.showConfirmDialog(null,
                "You will overwrite the file " + AppCorrGeometries.getInstance().getCurrentCorrFile()
                + ".\nIf you want to save any changes made to the geometries, but dont want to overwrite, use the 'save as'"
                + " option.\nAre you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            boolean success = AppCorrGeometries.getInstance().saveGeometryToFile();
            if (success) {
                JOptionPane.showMessageDialog(null, "Geometries saved succesfully to file.",
                        "Success", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error saving geometries to file.",
                        "Failure", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    boolean saveAllGeometries() {
        //inform user this operation will overwrite all files in the dataset. Ask for confirmation to continue
        int dialogResult = JOptionPane.showConfirmDialog(null,
                "You will overwrite all files with information about source and target geometries."
                + ".\nIf you want to save any changes made to the geometries, but dont want to overwrite, use the 'save all as'"
                + " option.\nAre you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            boolean success = AppCorrGeometries.getInstance().saveAllGeometries();
            if (success) {
                JOptionPane.showMessageDialog(null, "Geometries saved succesfully to files.",
                        "Success", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error saving geometries to files.",
                        "Failure", JOptionPane.ERROR_MESSAGE);
            }
            return success;
        }
        return false;//nothing saved (user canceled operation)
    }

    void saveGeometriesAs() {
        //ask user where to save the file and name of the file
        JFileChooser fileSaveChooser = new JFileChooser();
        fileSaveChooser.setToolTipText("Select a directory, name and extension (wkt or corr) to save file");
        FileNameExtensionFilter filterWKT = new FileNameExtensionFilter(".wkt", "wkt");
        FileNameExtensionFilter filterCorr = new FileNameExtensionFilter(".corr", "corr");
        fileSaveChooser.addChoosableFileFilter(filterWKT);
        fileSaveChooser.addChoosableFileFilter(filterCorr);
        fileSaveChooser.setAcceptAllFileFilterUsed(false);//remove 'all files' filter
        while (true) {
            if (fileSaveChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileSaveChooser.getSelectedFile();
                if (!file.getName().contains(".")) {
                    file = new File(file.getAbsolutePath() + fileSaveChooser.getFileFilter().getDescription());//add file extension
                }
                if (file.getName().endsWith(".corr") || file.getName().endsWith(".wkt")) {
                    boolean success = AppCorrGeometries.getInstance().saveGeometryToFile(file);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Geometries saved succesfully to file.",
                                "Success", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error saving geometries to file.",
                                "Failure", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Your file must have one of this extensions:\n .corr\n.wkt",
                            "File extension", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    boolean saveAllGeometriesAs() {
        if (AppCorrGeometries.getInstance().getNumberOfEditedGeometries() == 0) {
            JOptionPane.showMessageDialog(null, "There are no edited geometries to save!", "Nothing to save", JOptionPane.PLAIN_MESSAGE);
            return true; //nothing to save, no failure
        }

        //ask user where to save the file and name of the file
        JFileChooser fileSaveChooser = new JFileChooser();
        fileSaveChooser.setToolTipText("Select a directory, and extension to save files. A folder will be created with all files.");
        FileNameExtensionFilter filterWKT = new FileNameExtensionFilter(".wkt", "wkt");
        FileNameExtensionFilter filterCorr = new FileNameExtensionFilter(".corr", "corr");
        fileSaveChooser.addChoosableFileFilter(filterWKT);
        fileSaveChooser.addChoosableFileFilter(filterCorr);
        fileSaveChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileSaveChooser.setAcceptAllFileFilterUsed(false);//remove 'all files' filter
        while (true) {
            if (fileSaveChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String directory = fileSaveChooser.getSelectedFile().getAbsolutePath();
                String extension = fileSaveChooser.getFileFilter().getDescription();
                //get selected dir, create new folder to save the images
                String saveDir = "geometries";
                saveDir = directory + File.separator + saveDir;
                new File(saveDir).mkdirs();
                boolean success = AppCorrGeometries.getInstance().saveAllGeometriesAs(saveDir, extension);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Geometries saved succesfully to file.",
                            "Success", JOptionPane.PLAIN_MESSAGE);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Error saving geometries to file.",
                            "Failure", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    

    TestCaseEdit currentCase() {
        return tbModel.cases().getCurrentCase();
    }

    TestCaseEdit currentCase2() {
        return tbModel2.cases().getCurrentCase();
    }

    public void updateTestCases() {
        testListPanel.populateList();
        updateTestCaseView();
    }

    public void enableDrawingButtons() {
        //tbToolBar.drawPolygonButton.setEnabled(true);
        tbToolBar.drawRectangleButton.setEnabled(true);
    }

    public void disableDrawingButtons() {
        //tbToolBar.drawPolygonButton.setEnabled(false);
        tbToolBar.drawRectangleButton.setEnabled(false);
    }
        
    void menuChangeDataSet_actionPerformed(ActionEvent e) {
        String message = DatasetLoader.loadAndSetDataset();
        if (message.equals(AppConstants.CONFIRMATION_STRING)){
            this.reloadBothPanels();
            this.setToFirstImages();//start with the first images
        }
        else{
            //error dialog
            if (message.isEmpty()) //use default error message
                message = AppConstants.DATASET_LOADING_GENERAL_ERROR;
            JOptionPane.showMessageDialog(null, message, "Error Starting SPTDataLab", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    void drawRectangleButton_actionPerformed(ActionEvent e) {
        testCasePanel2.getGeometryEditPanel().setCurrentTool(RectangleTool.getInstance());
        testCasePanel.getGeometryEditPanel().setCurrentTool(RectangleTool.getInstance());
    }

    void drawPolygonButton_actionPerformed(ActionEvent e) {
        testCasePanel.getGeometryEditPanel().setCurrentTool(StreamPolygonTool.getInstance());
        testCasePanel2.getGeometryEditPanel().setCurrentTool(StreamPolygonTool.getInstance());
    }

    void infoButton_actionPerformed() {
        testCasePanel.getGeometryEditPanel().setCurrentTool(InfoTool.getInstance());
        testCasePanel2.getGeometryEditPanel().setCurrentTool(InfoTool.getInstance());
    }

    
    void actionDeleteVertexButton() {
        testCasePanel.getGeometryEditPanel().setCurrentTool(DeleteVertexTool.getInstance());
        testCasePanel2.getGeometryEditPanel().setCurrentTool(DeleteVertexTool.getInstance());
    }

    void zoomInButton_actionPerformed(ActionEvent e) {
        testCasePanel2.getGeometryEditPanel().setCurrentTool(zoomTool);
        testCasePanel.getGeometryEditPanel().setCurrentTool(zoomTool);
        testCasePanel.lblMousePos.setVisible(false);
        testCasePanel2.lblMousePos.setVisible(false);
    }

    void oneToOneButton_actionPerformed(ActionEvent e) {
        resetZoom();
    }



    void zoomToInputButton_actionPerformed(ActionEvent e) {
        testCasePanel.getGeometryEditPanel().zoomToGeometry(0);
        testCasePanel2.getGeometryEditPanel().zoomToGeometry(0);
        testCasePanel.lblMousePos.setVisible(false);
        testCasePanel2.lblMousePos.setVisible(false);
    }

    void zoomToInputA_actionPerformed(ActionEvent e) {
        testCasePanel.getGeometryEditPanel().zoomToGeometry(0);
        testCasePanel.lblMousePos.setVisible(false);
    }

    void zoomToInputB_actionPerformed(ActionEvent e) {
        testCasePanel2.getGeometryEditPanel().zoomToGeometry(0);
        testCasePanel2.lblMousePos.setVisible(false);
    }

    void panButton_actionPerformed(ActionEvent e) {
        testCasePanel.getGeometryEditPanel().setCurrentTool(PanTool.getInstance());
        testCasePanel2.getGeometryEditPanel().setCurrentTool(PanTool.getInstance());
    }

    public void setShowingGrid(boolean showGrid) {
        testCasePanel.editPanel.setGridEnabled(showGrid);
        SPTDataLabBuilderController.geometryViewChanged();
    }

    void showVertexIndicesMenuItem_actionPerformed(ActionEvent e) {
        //    testCasePanel.editPanel.setShowVertexIndices(showVertexIndicesMenuItem.isSelected());
    }

    void revealTopo_actionPerformed() {
        DisplayParameters.setMagnifyingTopology(testCasePanel.cbMagnifyTopo.isSelected());
        DisplayParameters.setTopologyStretchSize(testCasePanel.getStretchSize());
        //tbModel.setMagnifyingTopology(testCasePanel.editCtlPanel.cbMagnifyTopo.isSelected());
        //tbModel.setTopologyStretchSize(testCasePanel.editCtlPanel.getStretchSize());
        SPTDataLabBuilderController.geometryViewChanged();
    }

    void revealTopo2_actionPerformed() {
        DisplayParameters.setMagnifyingTopology(testCasePanel2.cbMagnifyTopo.isSelected());
        DisplayParameters.setTopologyStretchSize(testCasePanel2.getStretchSize());
        //tbModel.setMagnifyingTopology(testCasePanel.editCtlPanel.cbMagnifyTopo.isSelected());
        //tbModel.setTopologyStretchSize(testCasePanel.editCtlPanel.getStretchSize());
        SPTDataLabBuilderController.geometryViewChanged2();
    }

    /**
     * Component initialization
     */
    private void jbInit() throws Exception {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileAndDirectoryChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileAndDirectoryChooser.setMultiSelectionEnabled(true);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setMultiSelectionEnabled(false);
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //---------------------------------------------------
        contentPane = (JPanel) this.getContentPane();
        border4 = BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.white,
                Color.white, new Color(93, 93, 93), new Color(134, 134, 134));
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(screenSize.width, screenSize.height));
        this.setTitle(AppStrings.APP_NAME);
        //start JFrame maximized
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);

        jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setPreferredSize(new Dimension(601, 460));
        //top panel (top half of window)
        jPanel1.setLayout(new GridLayout(1, 2));
        //jPanel1.setMinimumSize(new Dimension(431, 0));
        contentPane.setPreferredSize(new Dimension(601, 450));
        //panel with statistics and the buttons to swith statistics
        inputTabbedPane.setTabPlacement(JTabbedPane.LEFT);
        jPanel2.setLayout(borderLayout3);
        wktPanel.setMinimumSize(new Dimension(111, 180));
        wktPanel.setSize(new Dimension(600, 350));//switched from "setPreferedSize" (change back if problems arise)
        wktPanel.setToolTipText(AppStrings.TIP_TEXT_ENTRY);
        testPanel.setLayout(gridBagLayout2);
        gridLayout1.setRows(4);
        gridLayout1.setColumns(1);

        contentPane.add(legendPanel, BorderLayout.NORTH);
        contentPane.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(jPanel1, JSplitPane.TOP);
        jPanel1.add(testCasePanel);
        jPanel1.add(testCasePanel2);

        //try to add tabbed pane with the jpanel2
        jSplitPane1.add(tabbedPane, JSplitPane.BOTTOM);

        //load the icons for the tabs
        ImageIcon wktPanelIcon = new ImageIcon(this.getClass().getResource("wkt_info_panel.png"));
        ImageIcon morphingIcon = new ImageIcon(this.getClass().getResource("morphing_icon.png"));
        tabbedPane.addTab("WKT Panel", wktPanelIcon, jPanel2, "WKT Panel and other related operations");

        tabbedPane.addTab("Morphing", morphingIcon, morphingPanel, AppStrings.MORPHING_PANE_TOOLTIP);

        //previously, before the tabbed pane:
        //jSplitPane1.add(jPanel2, JSplitPane.BOTTOM);
        jPanel2.add(tbToolBar.getToolBar(), BorderLayout.NORTH);
        //jPanel2 contains the wkt panel results and other statistics (bottom)
        jPanel2.add(inputTabbedPane, BorderLayout.CENTER);
        jSplitPane1.setBorder(new EmptyBorder(2, 2, 2, 2));
        jSplitPane1.setResizeWeight(0.6);
        inputTabbedPane.add(wktPanel, AppStrings.TAB_LABEL_INPUT);

        jSplitPane1.setDividerLocation(500);
        this.setJMenuBar(tbMenuBar.getMenuBar());

    }

    public SPTDataLabBuilderToolBar getToolbar() {
        return tbToolBar;
    }

    private void updateWktPanel() {
        updateWktTopPanel();
        updateWktBottomPanel();
    }

    //update text on wkt top panel, which should contain the wkt format of the geometry shown in left panel 
    private void updateWktTopPanel() {
        try {
            Geometry g0 = AppCorrGeometries.getInstance().getGeometryInPanelOriginal(true);
            wktPanel.setText(g0, 0);
        } catch (NullPointerException e) {
        }

    }

    //update text on wkt bottom panel, which should contain the wkt format of the geometry shown in right panel 
    private void updateWktBottomPanel() {
        try {
            Geometry g1 = AppCorrGeometries.getInstance().getGeometryInPanelOriginal(false);
            wktPanel.setText(g1, 1);
        } catch (NullPointerException e) {
        }

    }

    public void updateModelGeometry(List coordinates, GeometryEditPanel geomEditPanel) {

        try {

            GeometryFactory geomFac;
            Geometry newGeom;
            geomFac = SPTDataLab.getGeometryFactory();
            AppCorrGeometries.getInstance().setCorrGeometry(coordinates, geomEditPanel.isSecondPanel());
            coordinates = AppCorrGeometries.getInstance().transformToOriginalCoordinates(coordinates, geomEditPanel);
            LinearRing ring = geomFac.createLinearRing(GeometryEditModel.getRing(coordinates));
            newGeom = geomFac.createPolygon(ring, null);

            wktPanel.setText(newGeom, geomEditPanel.isSecondPanel() ? 1 : 0);

            String geo1 = wktPanel.getGeometryTextClean(0);
            String geo2 = wktPanel.getGeometryTextClean(1);

            AppCorrGeometries.getInstance().updateGeometriesFromWKTPanel(geo1, geo2);
            
            String[] wkts = AppCorrGeometries.getInstance().getWKTextFromGeometriesInPanelsScreenCoordinates();
            tbModel.loadGeometryText(wkts[0]);//load content of top wkt panel to 1st panel
            tbModel2.loadGeometryText(wkts[1]);//

        } catch (Exception ex) {
            Logger.getLogger(SPTDataLabBuilderFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateTestCaseView() {
        testCasePanel.setTestCase(currentCase());
        getTestCasePanel().setCurrentTestCaseIndex(tbModel.getCurrentCaseIndex() + 1);
        getTestCasePanel().setMaxTestCaseIndex(tbModel.getCasesSize());
        testCasePanel2.setTestCase(currentCase2());
        getTestCasePanel2().setCurrentTestCaseIndex(tbModel2.getCurrentCaseIndex() + 1);
        getTestCasePanel2().setMaxTestCaseIndex(tbModel2.getCasesSize());
        updateWktPanel();
    }

    public void displayInfo(Coordinate modelPt) {
        //displayInfo(testCasePanel.getGeometryEditPanel().getInfo(modelPt));
    }

    public void displayInfo(String s) {
        displayInfo(s, true);
    }

    public void displayInfo(String s, boolean showTab) {
        //logPanel.addInfo(s);
        if (showTab) {
            showInfoTab();
        }
    }


    void btnEditVertex_actionPerformed(ActionEvent e) {
        testCasePanel.getGeometryEditPanel().setCurrentTool(EditVertexTool.getInstance());
        testCasePanel2.getGeometryEditPanel().setCurrentTool(EditVertexTool.getInstance());
    }

    public void resetZoom() {
        testCasePanel.getGeometryEditPanel().getViewport().zoomToInitialExtent();
        testCasePanel2.getGeometryEditPanel().getViewport().zoomToInitialExtent();
        testCasePanel.lblMousePos.setVisible(true);
        testCasePanel2.lblMousePos.setVisible(true);
    }

}
