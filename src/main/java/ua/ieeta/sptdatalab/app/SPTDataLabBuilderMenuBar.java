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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import javax.swing.filechooser.FileNameExtensionFilter;
import org.locationtech.jts.io.ParseException;
import ua.ieeta.sptdatalab.geom.GeometryMatching;
import ua.ieeta.sptdatalab.geom.GeometryUtil;
import ua.ieeta.sptdatalab.geom.GeometryUtil.FilterOperationEnum;
import ua.ieeta.sptdatalab.model.GeometrySimplifier;

import ua.ieeta.sptdatalab.model.KeyObservationSelection;
import ua.ieeta.sptdatalab.model.SQLBuilder;
import ua.ieeta.sptdatalab.morphing.InterpolationMethodEnum;
import ua.ieeta.sptdatalab.morphing.InterpolationMethodEvaluation;
import ua.ieeta.sptdatalab.util.io.GeometryCollectionSummary;
import ua.ieeta.sptdatalab.util.io.GeometrySimilarityCollectionSummary;

public class SPTDataLabBuilderMenuBar {

    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu();
    JMenu jMenuWorkflow = new JMenu();

    JMenu jMenuSegmentation = new JMenu();
    JMenu jMenuFrameExtraction = new JMenu();
    JMenu jMenuInterpolation = new JMenu();
    JMenu jMenuSimplification = new JMenu();
    JMenu jMenuDataLoad = new JMenu();
    JMenu jMenuDataSelection = new JMenu();
    JMenu jMenuUtils = new JMenu();

    JMenu jMenuHelp = new JMenu();

    JMenuItem jMenuImageManualSegmenter = new JMenuItem();
    JMenuItem jMenuImageAutomaticSegmenter = new JMenuItem();
    JMenuItem jMenuSegmenterFilterCreation = new JMenuItem();
    JMenuItem jMenuVideoFrameExtraction = new JMenuItem();
    JMenuItem jMenuVideoFrameExtractionTool = new JMenuItem();
    JMenuItem jMenuVideoAutomaticSegmenter = new JMenuItem();
    JMenuItem jMenuWKTVerification = new JMenuItem();
    JMenuItem jMenuPolygonSimplifier = new JMenuItem();
    JMenuItem jMenuPolygonSimplifierTool = new JMenuItem();
    JMenuItem jMenuPolygonSimplifyDP = new JMenuItem();
    JMenuItem jMenuPolygonSimplifyVW = new JMenuItem();
    JMenuItem jMenuPolygonSimplifyMatchingAwareFile = new JMenuItem();
    JMenuItem jMenuDBMSDataLoader = new JMenuItem();
    JMenuItem jMenuDBMSDataLoaderContinuous = new JMenuItem();
    JMenuItem jMenuDistanceBasedDataSampling = new JMenuItem();
    JMenuItem jMenuDistanceBasedDataSamplingIntervals = new JMenuItem();
    JMenuItem jMenuFixedSizeDataSampling = new JMenuItem();
    JMenuItem jMenuEvaluatePySpatioTemporalGeom = new JMenuItem();
    JMenuItem jMenuEvaluateSecondo = new JMenuItem();
    JMenuItem jMenuJaccardIndexSecondo = new JMenuItem();

    JMenuItem jMenuGeometriesStatistics = new JMenuItem();
    JMenuItem jMenuGeometriesValidate = new JMenuItem();
    JMenuItem jMenuGeometriesMetrics = new JMenuItem();
    JMenuItem jMenuGeometriesFilter = new JMenuItem();
    JMenuItem jMenuMatchingPairGeometries = new JMenuItem();
    JMenuItem jMenuMatchingSetGeometries = new JMenuItem();

    JMenuItem jMenuAbout = new JMenuItem();
    JMenuItem jMenuFileExit = new JMenuItem();
    JMenuItem changeDataSet = new JMenuItem();
    JMenuItem saveCurrent = new JMenuItem();
    JMenuItem saveCurrentAs = new JMenuItem();
    JMenuItem saveAll = new JMenuItem();
    JMenuItem saveAllAs = new JMenuItem();

    SPTDataLabBuilderFrame tbFrame;

    public SPTDataLabBuilderMenuBar(SPTDataLabBuilderFrame tbFrame) {
        this.tbFrame = tbFrame;
    }

    public JMenuBar getMenuBar() {
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
        jMenuFile.setText("Editing and Interpolation");

        jMenuFile.add(this.changeDataSet);
        jMenuFile.add(this.saveCurrent);
        jMenuFile.add(this.saveCurrentAs);
        jMenuFile.add(this.saveAll);
        jMenuFile.add(this.saveAllAs);
        jMenuFile.addSeparator();
        jMenuFile.add(jMenuFileExit);
        //==========================

        jMenuImageManualSegmenter.setText("Segment image manually");
        jMenuImageManualSegmenter.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String imageFileName = "";
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setDialogTitle("Choose the image file: ");

                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("png, gif, jpg, bmp or tif images", "png", "gif", "jpg", "bmp", "tif");
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    imageFileName = jfc.getSelectedFile().getPath();
                } else {
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "segmenter", imageFileName);

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuWKTVerification.setText("Place geometry in image");
        jMenuWKTVerification.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String imageFileName = "";
                String geometryFileName = "";
                String outputFileName = "";
                String drawMarkers = " ";

                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setAcceptAllFileFilterUsed(false);
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                jfc.setDialogTitle("Choose the image file: ");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("png, gif, jpg, bmp or tif images", "png", "gif", "jpg", "bmp", "tif");
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    imageFileName = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    return;
                }

                jfc.setDialogTitle("Choose the WKT file: ");
                FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                jfc.addChoosableFileFilter(filterWKT);
                jfc.removeChoosableFileFilter(filter);

                returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    geometryFileName = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    return;
                }

                jfc.setDialogTitle("Select the file to save output");
                jfc.addChoosableFileFilter(filter);
                jfc.removeChoosableFileFilter(filterWKT);

                jfc.setSelectedFile(new File("output.jpg"));
                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    if (!file.getName().contains(".")) {
                        file = new File(file.getAbsolutePath() + ".jpg");
                    }
                    outputFileName = file.getName();
                } else {
                    return;
                }

                int dialogResult = JOptionPane.showConfirmDialog(null, "Draw markers over the geometry?",
                        "Draw markes", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    drawMarkers = "-m";
                }

                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "draw_wkt", "-i", imageFileName, "-p", geometryFileName, "-o", outputFileName, drawMarkers);

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuGeometriesMetrics.setText("Similarity and distance metrics");
        jMenuGeometriesMetrics.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    String fullFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the file with full geometry dataset (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        fullFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    String selectedGeomFileName;
                    jfc.setDialogTitle("Choose the file with selected geometries to evaluate (with WKT): ");
                    returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        selectedGeomFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save interpolated geometries");
                    String outputFileName = "";

                    outputFileName = selectedGeomFileName.replaceFirst("[.][^.]+$", "").concat("Metrics").concat(".txt");

                    jfc.setSelectedFile(new File(outputFileName));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        outputFileName = file.getPath();
                    } else {
                        return;
                    }

                    String summaryFileName = selectedGeomFileName.replaceFirst("[.][^.]+$", "").concat("Summary").concat(".txt");

                    WaitDialog wDialog = new WaitDialog();

                    GeometrySimilarityCollectionSummary summary;

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait... This may take a long time.");

                    summary = GeometryUtil.computeMetrics(selectedGeomFileName, fullFileName, outputFileName, summaryFileName);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, summary.getHTMLSummary());
                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuSegmenterFilterCreation.setText("Create filters");
        jMenuSegmenterFilterCreation.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String imageFileName = "";
                JFileChooser jfc = new JFileChooser();

                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setAcceptAllFileFilterUsed(false);

                jfc.setDialogTitle("Choose the image file: ");

                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("png, gif, jpg, bmp or tif images", "png", "gif", "jpg", "bmp", "tif");
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    imageFileName = jfc.getSelectedFile().getPath();
                } else {
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "hsv", imageFileName);

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuImageAutomaticSegmenter.setText("Automatically segment image using a filter file");
        jMenuImageAutomaticSegmenter.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String imageFileName = "";
                String filterFileName = "";
                String outputFileName = "";
                String polygonFileName = "";
                String imageFileExtension;

                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setAcceptAllFileFilterUsed(false);
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                jfc.setDialogTitle("Choose the image file: ");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("png, gif, jpg, bmp or tif images", "png", "gif", "jpg", "bmp", "tif");
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    imageFileName = jfc.getSelectedFile().getAbsolutePath();
                    imageFileExtension = imageFileName.substring(imageFileName.lastIndexOf("."), imageFileName.length());
                } else {
                    return;
                }

                jfc.setDialogTitle("Choose the filter file: ");
                FileNameExtensionFilter filterTXT = new FileNameExtensionFilter("Txt files", "txt");
                jfc.addChoosableFileFilter(filterTXT);
                jfc.removeChoosableFileFilter(filter);

                returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    filterFileName = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    return;
                }

                jfc.setDialogTitle("Select the file to save output");
                jfc.addChoosableFileFilter(filter);
                jfc.removeChoosableFileFilter(filterTXT);

                jfc.setSelectedFile(new File("outputAutoSegmenterImage.jpg"));
                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    if (!file.getName().contains(".")) {
                        file = new File(file.getAbsolutePath() + imageFileExtension);
                    }
                    outputFileName = file.getName();

                } else {
                    return;
                }

                int dialogResult = JOptionPane.showConfirmDialog(null, "Create a WKT file for the geometry?",
                        "Create WKT", JOptionPane.YES_NO_OPTION);
                ProcessBuilder pb;
                if (dialogResult == JOptionPane.YES_OPTION) {

                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.setSelectedFile(new File("outputAutoSegmenterImage-Geometry.wkt"));
                    jfc.addChoosableFileFilter(filterWKT);
                    jfc.removeChoosableFileFilter(filter);
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        polygonFileName = file.getName();
                        pb = new ProcessBuilder("cmd", "/C", "auto_segmenter", "-i", "-m", imageFileName, "-f", filterFileName, "-o", outputFileName, "-p", polygonFileName);
                    } else {
                        return;
                    }
                } else {
                    pb = new ProcessBuilder("cmd", "/C", "auto_segmenter", "-i", "-m", imageFileName, "-f", filterFileName, "-o", outputFileName);
                }

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuVideoAutomaticSegmenter.setText("Automatically segment video using a filter file");
        jMenuVideoAutomaticSegmenter.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String videoFileName = "";
                String filterFileName = "";
                String outputFileName = "";
                String polygonFileName = "";
                String videoFileExtension = "";

                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setAcceptAllFileFilterUsed(false);
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                jfc.setDialogTitle("Choose the video file: ");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("avi, mov, mp4, mpg or wmv videos", "avi", "mov", "mp4", "mpg", "wmv");
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    videoFileName = jfc.getSelectedFile().getAbsolutePath();
                    videoFileExtension = videoFileName.substring(videoFileName.lastIndexOf("."), videoFileName.length());
                }

                jfc.setDialogTitle("Choose the filter file: ");
                FileNameExtensionFilter filterTXT = new FileNameExtensionFilter("Txt files", "txt");
                jfc.addChoosableFileFilter(filterTXT);
                jfc.removeChoosableFileFilter(filter);

                returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    filterFileName = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    return;
                }

                jfc.setDialogTitle("Select the file to save output");
                jfc.addChoosableFileFilter(filter);
                jfc.removeChoosableFileFilter(filterTXT);

                jfc.setSelectedFile(new File("outputAutoSegmenterVideo"));
                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    if (!file.getName().contains(".")) {
                        file = new File(file.getAbsolutePath() + videoFileExtension);
                    }
                    outputFileName = file.getName();

                }
                int dialogResult = JOptionPane.showConfirmDialog(null, "Create a WKT file for the geometry?",
                        "Create WKT", JOptionPane.YES_NO_OPTION);
                ProcessBuilder pb;
                if (dialogResult == JOptionPane.YES_OPTION) {

                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.setSelectedFile(new File("outputAutoSegmenterVideo-Geometry.wkt"));
                    jfc.addChoosableFileFilter(filterWKT);
                    jfc.removeChoosableFileFilter(filter);
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        polygonFileName = file.getName();
                        pb = new ProcessBuilder("cmd", "/C", "auto_segmenter", "-v", "-m", videoFileName, "-f", filterFileName, "-o", outputFileName, "-p", polygonFileName);
                    } else {
                        return;
                    }
                } else {
                    pb = new ProcessBuilder("cmd", "/C", "auto_segmenter", "-v", "-m", videoFileName, "-f", filterFileName, "-o", outputFileName);
                }

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuVideoFrameExtractionTool.setText("Open video frame extraction tool");
        jMenuVideoFrameExtractionTool.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String videoFileName = "";
                JFileChooser jfc = new JFileChooser();

                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setAcceptAllFileFilterUsed(false);

                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.setDialogTitle("Choose the video file: ");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("avi, mov, mp4, mpg or wmv videos", "avi", "mov", "mp4", "mpg", "wmv");
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    videoFileName = jfc.getSelectedFile().getPath();
                } else {
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "frame_extractor", videoFileName);

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuVideoFrameExtraction.setText("Extract frames from video (fixed size sampling)");
        jMenuVideoFrameExtraction.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String videoFileName = "";

                JFileChooser jfc = new JFileChooser();

                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setAcceptAllFileFilterUsed(false);

                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.setDialogTitle("Choose the video file: ");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("avi, mov, mp4, mpg or wmv videos", "avi", "mov", "mp4", "mpg", "wmv");
                jfc.addChoosableFileFilter(filter);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    videoFileName = jfc.getSelectedFile().getPath();
                } else {
                    return;
                }

                String userInput = JOptionPane.showInputDialog("Number of frames to extract");

                try {
                    int intFrames = Integer.parseInt(userInput);

                } catch (NumberFormatException en) {
                    JOptionPane.showMessageDialog(null, "Value must be an integer!");
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "frame_extractor", videoFileName, userInput);

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuPolygonSimplifierTool.setText("Open matching-aware geometry simplification tool");
        jMenuPolygonSimplifierTool.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String geometryFileName1 = "";
                String geometryFileName2 = "";

                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setAcceptAllFileFilterUsed(false);
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                jfc.setDialogTitle("Choose the first geometry file (with WKT): ");
                FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                jfc.addChoosableFileFilter(filterWKT);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    geometryFileName1 = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    return;
                }

                jfc.setDialogTitle("Choose the second geometry file (with WKT): ");
                returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    geometryFileName2 = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "simplifier", "-p", geometryFileName1, "-q", geometryFileName2);

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuPolygonSimplifier.setText("Run matching-aware geometry simplification for a pair of geometries");
        jMenuPolygonSimplifier.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String geometryFileName1 = "";
                String geometryFileName2 = "";

                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                jfc.setAcceptAllFileFilterUsed(false);
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                jfc.setDialogTitle("Choose the first geometry file (with WKT): ");
                FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                jfc.addChoosableFileFilter(filterWKT);

                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    geometryFileName1 = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    return;
                }

                jfc.setDialogTitle("Choose the second geometry file (with WKT): ");
                returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    geometryFileName2 = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    return;
                }

                String userInputPercentage = JOptionPane.showInputDialog("Percentage of points to remove (between 0 and 1):");

                float flPercentage;
                try {
                    flPercentage = Float.parseFloat(userInputPercentage);

                } catch (NumberFormatException en) {
                    JOptionPane.showMessageDialog(null, "Value must be real!");
                    return;
                }

                if (flPercentage < 0 || flPercentage > 1) {
                    JOptionPane.showMessageDialog(null, "Value must be between 0 and 1!");
                    return;
                }

                String userInputTime = JOptionPane.showInputDialog("Time value (between 0 and 1):");

                float flTime;
                try {
                    flTime = Float.parseFloat(userInputTime);

                } catch (NumberFormatException en) {
                    JOptionPane.showMessageDialog(null, "Value must be real!");
                    return;
                }

                if (flTime < 0 || flTime > 1) {
                    JOptionPane.showMessageDialog(null, "Value must be between 0 and 1!");
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "simplifier", "-p", geometryFileName1, "-q", geometryFileName2, "-o", "simplificationOutput.jpg", "-r", userInputPercentage, "-t", userInputTime);

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuPolygonSimplifyDP.setText("Simplify a set of geometries using Douglas-Peucker distance-based strategy");
        jMenuPolygonSimplifyDP.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    simplifyGeometryFile(GeometrySimplifier.SimplificationMethod.TopologyPreserve);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        jMenuPolygonSimplifyVW.setText("Simplify a set of geometries using Visvalingam-Whyatt area-based algorithm");
        jMenuPolygonSimplifyVW.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    simplifyGeometryFile(GeometrySimplifier.SimplificationMethod.VisvalingamWhyatt);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuPolygonSimplifyMatchingAwareFile.setText("Simplify a set of geometries using Matching-Aware Simplification");
        jMenuPolygonSimplifyMatchingAwareFile.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    simplifyGeometryFile(GeometrySimplifier.SimplificationMethod.MatchingAware);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuDBMSDataLoader.setText("Scripts to load geometries into DBMS - discrete model");
        jMenuDBMSDataLoader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the geometries file (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    String userInputTable = "";
                    userInputTable = JOptionPane.showInputDialog("Table name:");
                    if (userInputTable.length() == 0) {
                        return;
                    }

                    String userInputSeries = "";
                    userInputSeries = JOptionPane.showInputDialog("Name of the object series:");
                    if (userInputSeries.length() == 0) {
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save output");

                    FileNameExtensionFilter filterSQL = new FileNameExtensionFilter("SQL or txt files", "sql", "txt");
                    jfc.addChoosableFileFilter(filterSQL);

                    jfc.addChoosableFileFilter(filterSQL);
                    jfc.removeChoosableFileFilter(filterWKT);

                    String outputFileName;

                    jfc.setSelectedFile(new File(userInputSeries.concat("Discrete.sql")));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + "Discrete.sql");
                        }
                        outputFileName = file.getPath();
                    } else {
                        return;
                    }

                    int dialogResult = JOptionPane.showConfirmDialog(null, "Include delete command?",
                            "Include delete command", JOptionPane.YES_NO_OPTION);

                    boolean includeDelete = false;
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        includeDelete = true;
                    }

                    SQLBuilder writer = new SQLBuilder();

                    WaitDialog wDialog = new WaitDialog();

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait... This may take a long time.");

                    int cont = writer.createSQLDiscreteModel(geometriesFileName, outputFileName, userInputTable, userInputSeries, includeDelete);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, "Script file created containing " + String.valueOf(cont) + " insert commands.");

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuDistanceBasedDataSampling.setText("Select key (distance-based) observations");
        jMenuDistanceBasedDataSampling.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the geometries file (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    String userInputDistance = JOptionPane.showInputDialog("Maximum allowed distance (0--1):");
                    if (userInputDistance.length() == 0) {
                        return;
                    }
                    Double distance = 0.0;
                    try {
                        distance = Double.parseDouble(userInputDistance);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(null, "Distance must be a value between 0 and 1");
                        return;
                    }

                    if (distance < 0 || distance > 1) {
                        JOptionPane.showMessageDialog(null, "Distance must be a value between 0 and 1");
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save selected WKT");

                    String outputGeomFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("DistanceBasedSampling").concat(".wkt");

                    jfc.setSelectedFile(new File(outputGeomFileName));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        outputGeomFileName = file.getPath();
                    } else {
                        return;
                    }

                    KeyObservationSelection keyobs = new KeyObservationSelection();
                    GeometryCollectionSummary summary;

                    String statisticsFileName = outputGeomFileName.replaceFirst("[.][^.]+$", "").concat("Statistics").concat(".txt");

                    WaitDialog wDialog = new WaitDialog();

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait the geometries are read... This may take a long time.");

                    summary = keyobs.distanceBasedObservationSelection(geometriesFileName, outputGeomFileName, statisticsFileName, distance);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, summary.getHTMLSummary());

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

        jMenuDistanceBasedDataSamplingIntervals.setText("Identify distance-based intervals");
        jMenuDistanceBasedDataSamplingIntervals.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the geometries file (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    String userInputDistance = JOptionPane.showInputDialog("Maximum allowed distance (0--1):");
                    if (userInputDistance.length() == 0) {
                        return;
                    }
                    Double distance = 0.0;
                    try {
                        distance = Double.parseDouble(userInputDistance);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(null, "Distance must be a value between 0 and 1");
                        return;
                    }

                    if (distance < 0 || distance > 1) {
                        JOptionPane.showMessageDialog(null, "Distance must be a value between 0 and 1");
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save selected intervals");

                    String outputGeomFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("DistanceBasedSamplingIntervals").concat(".txt");

                    jfc.setSelectedFile(new File(outputGeomFileName));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".txt");
                        }
                        outputGeomFileName = file.getPath();
                    } else {
                        return;
                    }

                    KeyObservationSelection keyobs = new KeyObservationSelection();
                    GeometryCollectionSummary summary;

                    String statisticsFileName = outputGeomFileName.replaceFirst("[.][^.]+$", "").concat("Statistics").concat(".txt");

                    WaitDialog wDialog = new WaitDialog();

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait the geometries are read... This may take a long time.");

                    summary = keyobs.distanceBasedObservationIntervals(geometriesFileName, outputGeomFileName, statisticsFileName, distance);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, summary.getHTMLSummary());

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

        jMenuFixedSizeDataSampling.setText("Select observations at fixed intervals");
        jMenuFixedSizeDataSampling.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the geometries file (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    String userInputDistance = JOptionPane.showInputDialog("Step size:");
                    if (userInputDistance.length() == 0) {
                        return;
                    }
                    int observations;
                    try {
                        observations = Integer.valueOf(userInputDistance);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(null, "Step size must be a positive integer");
                        return;
                    }

                    if (observations <= 0) {
                        JOptionPane.showMessageDialog(null, "Step size must be a positive integer");
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save selected WKT");

                    String outputGeomFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("FixedSizeSampling").concat(".wkt");

                    jfc.setSelectedFile(new File(outputGeomFileName));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        outputGeomFileName = file.getPath();
                    } else {
                        return;
                    }

                    KeyObservationSelection keyobs = new KeyObservationSelection();
                    GeometryCollectionSummary summary;

                    String statisticsFileName = outputGeomFileName.replaceFirst("[.][^.]+$", "").concat("Statistics").concat(".txt");

                    WaitDialog wDialog = new WaitDialog();

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait the geometries are read... This may take a long time.");

                    summary = keyobs.fixedSizeObservationSelection(geometriesFileName, outputGeomFileName, statisticsFileName, observations);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, summary.getHTMLSummary());

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

        jMenuEvaluatePySpatioTemporalGeom.setText(
                "PySpatioTemporalGeom");
        jMenuEvaluatePySpatioTemporalGeom.addActionListener(
                new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {

                evaluateInterpolation(InterpolationMethodEnum.PySpatioTemporalGeom);

            }
        }
        );

        jMenuEvaluateSecondo.setText(
                "Secondo interpolation");
        jMenuEvaluateSecondo.addActionListener(
                new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {

                evaluateInterpolation(InterpolationMethodEnum.Secondo);

            }
        }
        );

        jMenuDBMSDataLoaderContinuous.setText("Scripts to load geometries into DBMS - continuous model");
        jMenuDBMSDataLoaderContinuous.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the geometries file (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    String userInputTable = JOptionPane.showInputDialog("Table name:");
                    if (userInputTable.length() == 0) {
                        return;
                    }

                    String userInputSeries = JOptionPane.showInputDialog("Name of the object series:");
                    if (userInputSeries.length() == 0) {
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save output");

                    FileNameExtensionFilter filterSQL = new FileNameExtensionFilter("SQL or txt files", "sql", "txt");
                    jfc.addChoosableFileFilter(filterSQL);

                    jfc.addChoosableFileFilter(filterSQL);
                    jfc.removeChoosableFileFilter(filterWKT);

                    String outputFileName;

                    jfc.setSelectedFile(new File(userInputSeries.concat("MovingMesh.sql")));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + "MovingMesh.sql");
                        }
                        outputFileName = file.getPath();
                    } else {
                        return;
                    }

                    int dialogResult = JOptionPane.showConfirmDialog(null, "Include delete command?",
                            "Include delete command", JOptionPane.YES_NO_OPTION);

                    boolean includeDelete = false;
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        includeDelete = true;
                    }

                    SQLBuilder writer = new SQLBuilder();

                    WaitDialog wDialog = new WaitDialog();

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait... This may take a long time.");

                    int cont = writer.createSQLContinuousModel(geometriesFileName, outputFileName, userInputTable, userInputSeries, includeDelete);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, "Script file created containing " + String.valueOf(cont) + " insert commands.");

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuGeometriesStatistics.setText("Create statistics for geometries file");
        jMenuGeometriesStatistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the geometries file (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    String summaryFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("Summary").concat(".txt");

                    GeometryCollectionSummary summary;
                    WaitDialog wDialog = new WaitDialog();

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait the geometries are read... This may take a long time.");

                    summary = GeometryUtil.createFileStatistics(geometriesFileName, summaryFileName);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, summary.getHTMLSummary());

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuGeometriesFilter.setText("Filter geometries based on their area");
        jMenuGeometriesFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the geometries file (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save resulting geometries");

                    String outputFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("Filtered").concat(".wkt");

                    jfc.setSelectedFile(new File(outputFileName));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        outputFileName = file.getPath();
                    } else {
                        return;
                    }

                    String statisticsFileName = outputFileName.replaceFirst("[.][^.]+$", "").concat("Statistics").concat(".txt");

                    WaitDialog wDialog = new WaitDialog();
                    GeometryCollectionSummary summary;

                    JPanel areaPanel = new JPanel();
                    JComboBox optionsCombo = new JComboBox(new String[]{" > ", " < ", " = "});

                    JScrollPane scrollPane = new JScrollPane(optionsCombo);

                    optionsCombo.setSelectedIndex(0);

                    JTextField areaValue = new JTextField(10);
                    areaPanel.add(new JLabel("Remove objects whose area is "));

                    areaPanel.add(scrollPane);
                    areaPanel.add(Box.createHorizontalStrut(15));
                    areaPanel.add(areaValue);

                    int result = JOptionPane.showConfirmDialog(null, areaPanel,
                            "Please specify the area value", JOptionPane.OK_CANCEL_OPTION);
                    if (!(result == JOptionPane.OK_OPTION)) {
                        return;
                    }

                    Double filterValue;
                    try {
                        filterValue = Double.parseDouble(areaValue.getText());

                    } catch (NumberFormatException en) {
                        JOptionPane.showMessageDialog(null, "Value must be a number.");
                        return;
                    }

                    FilterOperationEnum operation = null;

                    if (optionsCombo.getSelectedIndex() == 0) {
                        operation = FilterOperationEnum.AreaGreaterThan;
                    }

                    if (optionsCombo.getSelectedIndex() == 1) {
                        operation = FilterOperationEnum.AreaSmallerThan;
                    }

                    if (optionsCombo.getSelectedIndex() == 2) {
                        operation = FilterOperationEnum.AreaEqualTo;
                    }

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait while geometries are filtered... This may take a long time.");

                    summary = GeometryUtil.filterGeometriesByArea(geometriesFileName, outputFileName, statisticsFileName, operation, filterValue);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, summary.getHTMLSummary());

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuMatchingPairGeometries.setText("Match a pair of geometries");
        jMenuMatchingPairGeometries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the file with the first geometry (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    String geometriesFileName2 = "";
                    jfc.setDialogTitle("Choose the file with the second geometry (with WKT): ");
                    returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save matching geometries");
                    String outputFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("Matched").concat(".wkt");
                    jfc.setSelectedFile(new File(outputFileName));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        outputFileName = file.getPath();
                    } else {
                        return;
                    }

                    WaitDialog wDialog = new WaitDialog();
                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait while geometries are matched... This may take a long time.");
                    GeometryMatching.executePairMatching(geometriesFileName, geometriesFileName2, outputFileName);
                    wDialog.closeWaitingDialog();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        jMenuMatchingSetGeometries.setText("Match a set of geometries");
        jMenuMatchingSetGeometries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the file with the geometries (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save matching geometries");
                    String outputFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("Matched").concat(".wkt");
                    jfc.setSelectedFile(new File(outputFileName));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        outputFileName = file.getPath();
                    } else {
                        return;
                    }

                    WaitDialog wDialog = new WaitDialog();
                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait while geometries are matched... This may take a long time.");
                    GeometryMatching.executeMatching(geometriesFileName, outputFileName);
                    wDialog.closeWaitingDialog();

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        jMenuGeometriesValidate.setText("Try to make geometries valid");
        jMenuGeometriesValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String geometriesFileName;

                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
                    jfc.setAcceptAllFileFilterUsed(false);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    jfc.setDialogTitle("Choose the geometries file (with WKT): ");
                    FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
                    jfc.addChoosableFileFilter(filterWKT);

                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
                    } else {
                        return;
                    }

                    jfc.setDialogTitle("Select the file to save modified geometries");

                    String outputFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("Validated").concat(".wkt");

                    jfc.setSelectedFile(new File(outputFileName));
                    if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        if (!file.getName().contains(".")) {
                            file = new File(file.getAbsolutePath() + ".wkt");
                        }
                        outputFileName = file.getPath();
                    } else {
                        return;
                    }

                    String statisticsFileName = outputFileName.replaceFirst("[.][^.]+$", "").concat("Statistics").concat(".txt");

                    WaitDialog wDialog = new WaitDialog();
                    GeometryCollectionSummary summary;

                    wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait while geometries are validated... This may take a long time.");

                    summary = GeometryUtil.makeGeometryValid(geometriesFileName, outputFileName, statisticsFileName);

                    wDialog.closeWaitingDialog();

                    JOptionPane.showMessageDialog(null, summary.getHTMLSummary());

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        });


        jMenuWorkflow.setText("SPT Data Workflow");

        jMenuSegmentation.setText("Segmentation tools");
        jMenuSegmentation.add(jMenuImageManualSegmenter);
        jMenuSegmentation.add(jMenuWKTVerification);
        jMenuSegmentation.add(jMenuSegmenterFilterCreation);
        jMenuSegmentation.add(jMenuImageAutomaticSegmenter);
        jMenuSegmentation.add(jMenuVideoAutomaticSegmenter);
        jMenuWorkflow.add(jMenuSegmentation);

        jMenuFrameExtraction.setText("Frame extraction");
        jMenuFrameExtraction.add(jMenuVideoFrameExtractionTool);
        jMenuFrameExtraction.add(jMenuVideoFrameExtraction);
        jMenuWorkflow.add(jMenuFrameExtraction);

        jMenuSimplification.setText("Geometry simplification tools");
        jMenuSimplification.add(jMenuPolygonSimplifierTool);
        jMenuSimplification.add(jMenuPolygonSimplifier);
        jMenuSimplification.add(jMenuPolygonSimplifyDP);
        jMenuSimplification.add(jMenuPolygonSimplifyVW);
        jMenuSimplification.add(jMenuPolygonSimplifyMatchingAwareFile);
        jMenuWorkflow.add(jMenuSimplification);

        jMenuDataLoad.setText("Scripts to store SPT data in DBMS");
        jMenuDataLoad.add(jMenuDBMSDataLoader);
        jMenuDataLoad.add(jMenuDBMSDataLoaderContinuous);
        jMenuWorkflow.add(jMenuDataLoad);

        jMenuDataSelection.setText("Apply sampling strategies over a set of geometries");
        jMenuDataSelection.add(jMenuDistanceBasedDataSampling);
        jMenuDataSelection.add(jMenuDistanceBasedDataSamplingIntervals);
        jMenuDataSelection.add(jMenuFixedSizeDataSampling);
        jMenuWorkflow.add(jMenuDataSelection);

        jMenuInterpolation.setText("Evaluate interpolation algorithms over selected geometries");
        jMenuInterpolation.add(jMenuEvaluatePySpatioTemporalGeom);
        jMenuInterpolation.add(jMenuEvaluateSecondo);
        jMenuWorkflow.add(jMenuInterpolation);

        jMenuWorkflow.add(jMenuGeometriesMetrics);

        jMenuUtils.setText("Geometry utils");
        jMenuUtils.add(jMenuGeometriesValidate);
        jMenuUtils.add(jMenuGeometriesStatistics);
        jMenuUtils.add(jMenuGeometriesFilter);
        jMenuUtils.add(jMenuGeometriesMetrics);
        jMenuUtils.add(jMenuMatchingPairGeometries);
        jMenuUtils.add(jMenuMatchingSetGeometries);

        jMenuWorkflow.add(jMenuUtils);

        jMenuHelp.setText("Help");
        jMenuHelp.add(jMenuAbout);

        jMenuBar1.add(jMenuFile);
        jMenuBar1.add(jMenuWorkflow);
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

    private void simplifyGeometryFile(GeometrySimplifier.SimplificationMethod method) throws FileNotFoundException, InterruptedException {

        try {

            String geometriesFileName;
            JFileChooser jfc = new JFileChooser();
            jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
            jfc.setAcceptAllFileFilterUsed(false);
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setDialogTitle("Choose the geometries file (with WKT): ");
            FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
            jfc.addChoosableFileFilter(filterWKT);
            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
            } else {
                return;
            }
            jfc.setDialogTitle("Select the file to save output");

            String outputFileName = "";

            if (method == GeometrySimplifier.SimplificationMethod.TopologyPreserve) {
                outputFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("SimplifiedDP").concat(".wkt");
            } else {
                if (method == GeometrySimplifier.SimplificationMethod.VisvalingamWhyatt) {
                    outputFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("SimplifiedVW").concat(".wkt");
                } else if (method == GeometrySimplifier.SimplificationMethod.MatchingAware) {
                    outputFileName = geometriesFileName.replaceFirst("[.][^.]+$", "").concat("SimplifiedMA").concat(".wkt");
                }
            }

            jfc.setSelectedFile(new File(outputFileName));

            if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                if (!file.getName().contains(".")) {
                    file = new File(file.getAbsolutePath() + ".wkt");
                }
                outputFileName = file.getPath();
            } else {
                return;
            }

            Double tolerance = 0.0;
            String userInputPercentage = "";
            String userInputTime = "";

            if ((method == GeometrySimplifier.SimplificationMethod.TopologyPreserve)
                    || (method == GeometrySimplifier.SimplificationMethod.VisvalingamWhyatt)) {

                String userInputTolerance = JOptionPane.showInputDialog("Tolerance (>=0):");
                if (userInputTolerance == null)
                    return;
                
                if (userInputTolerance.length() == 0) {
                    return;
                }
                try {
                    tolerance = Double.parseDouble(userInputTolerance);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "Tolerance must be a non-negative value");
                    return;
                }
                if (tolerance < 0) {
                    JOptionPane.showMessageDialog(null, "Tolerance must be a non-negative value");
                    return;
                }

            } else {

                userInputPercentage = JOptionPane.showInputDialog("Percentage of points to remove (between 0 and 1):");

                float flPercentage;
                try {
                    flPercentage = Float.parseFloat(userInputPercentage);

                } catch (NumberFormatException en) {
                    JOptionPane.showMessageDialog(null, "Value must be real!");
                    return;
                }

                if (flPercentage < 0 || flPercentage > 1) {
                    JOptionPane.showMessageDialog(null, "Value must be between 0 and 1!");
                    return;
                }

                userInputTime = JOptionPane.showInputDialog("Time value (between 0 and 1):");

                float flTime;
                try {
                    flTime = Float.parseFloat(userInputTime);

                } catch (NumberFormatException en) {
                    JOptionPane.showMessageDialog(null, "Value must be real!");
                    return;
                }

                if (flTime < 0 || flTime > 1) {
                    JOptionPane.showMessageDialog(null, "Value must be between 0 and 1!");
                    return;
                }

            }

            String statisticsFileName = outputFileName.replaceFirst("[.][^.]+$", "").concat("Statistics").concat(".txt");

            String summaryStatisticsFileName = statisticsFileName.replaceFirst("[.][^.]+$", "").concat("Summary").concat(".txt");

            WaitDialog wDialog = new WaitDialog();

            wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait while geometries are simplified");

            GeometrySimplifier simp = new GeometrySimplifier();
            GeometrySimilarityCollectionSummary summary;

            if ((method == GeometrySimplifier.SimplificationMethod.TopologyPreserve)
                    || (method == GeometrySimplifier.SimplificationMethod.VisvalingamWhyatt)) {
                summary = simp.simplify(geometriesFileName, outputFileName, statisticsFileName, summaryStatisticsFileName, tolerance, method);
            } else {
                summary = simp.simplify(geometriesFileName, outputFileName, statisticsFileName, summaryStatisticsFileName, userInputPercentage, userInputTime, method);
            }

            wDialog.closeWaitingDialog();

            JOptionPane.showMessageDialog(null, summary.getHTMLSummary());

        } catch (IOException ex) {
            Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void evaluateInterpolation(InterpolationMethodEnum method) {

        String geometriesFileName;

        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(AppConstants.DEFAULT_DIRECTORY));
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        jfc.setDialogTitle("Choose the file with full geometry dataset (with WKT): ");
        FileNameExtensionFilter filterWKT = new FileNameExtensionFilter("WKT or txt files", "wkt", "txt");
        jfc.addChoosableFileFilter(filterWKT);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            geometriesFileName = jfc.getSelectedFile().getAbsolutePath();
        } else {
            return;
        }

        String selectedGeomFileName;
        jfc.setDialogTitle("Choose the file with selected geometries to interpolate (with WKT): ");
        returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedGeomFileName = jfc.getSelectedFile().getAbsolutePath();
        } else {
            return;
        }

        jfc.setDialogTitle("Select the file to save interpolated geometries");
        String outputFileName = "";

        outputFileName = "InterpolatedGeometries".concat(method.toString()).concat(".wkt");

        jfc.setSelectedFile(new File(outputFileName));
        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".wkt");
            }
            outputFileName = file.getPath();
        } else {
            return;
        }

        InterpolationMethodEvaluation eval = new InterpolationMethodEvaluation();

        String statisticsFileName = outputFileName.replaceFirst("[.][^.]+$", "").concat("Statistics").concat(".txt");

        WaitDialog wDialog = new WaitDialog();

        GeometrySimilarityCollectionSummary summary;

        wDialog.openWaitingDialog(SPTDataLabBuilderFrame.instance(), "Please wait... This may take a long time.");

        summary = eval.evaluate(geometriesFileName, selectedGeomFileName, outputFileName, statisticsFileName, method);

        wDialog.closeWaitingDialog();

        JOptionPane.showMessageDialog(null, summary.getHTMLSummary());

    }

}
