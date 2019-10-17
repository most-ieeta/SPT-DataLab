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

import ua.ieeta.sptdatalab.model.TestBuilderModel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import javax.swing.UIManager;
import ua.ieeta.sptdatalab.morphing.SimilarityMetricsEnum;

import org.locationtech.jts.geom.*;
import static ua.ieeta.sptdatalab.app.AppConstants.CACHE_FILE;
import ua.ieeta.sptdatalab.morphing.InterpolationMethodEnum;
import ua.ieeta.sptdatalab.morphing.InterpolationMethodFacade;
import ua.ieeta.sptdatalab.morphing.Metrics;
import ua.ieeta.sptdatalab.util.io.DatasetLoader;

 /*
        Initializes SPTDataLab Application. No commands: beggins with GUI

        Using options -i -> interpolation
        -m -> metric
        -h -> help
        -c -> installations required for the interpolation and similarity metrics
        
        Interpolation at instant: arguments must be passed in the form -i <p> <q> <instant> <interpolation method> <file to save (optional)> perform interpolation using a specified method
        Interpolation during period: arguments must be passed in the form -p <p> <q> <number of samples> <interpolation method> <file to save (optional)> perform interpolation using a specified method

        interpolation methods:
        0 - JDuarte
        1 - Mckenney
        2 - Gutting
        usage example for interpolation: -i "POLYGON ((949 781, 926 812, 891.6666656434536 796.66666620969772, 857.3333312869072 781.33333241939545, 823 766, 804 736, 807 689, 810 642, 833.6666673719883 612.99999913573265, 857.33333474397659 583.9999982714653, 881 555, 892 572, 909 576, 914 583, 922 580, 928 597, 920 640, 946 640, 968 647.5, 990 655, 949 781))" "POLYGON ((972 774, 955 806, 922 817, 905 816, 850 782, 822 755, 812 697, 812 656, 835 582, 848 565, 863 562, 876 576, 893 576, 899 583, 909 579, 917 592, 916 637, 962 632, 968 638, 990 640, 972 774))" 0.5 2 "C:\Users\bjpsi\Desktop\Investigacao\SPT-DataLab\save.txt"
        Metrics: arguments must be passed in the form -m <geo1> <geo2> <metric> <file to save (optional)>
        similarity metrics:
        0 - Jaccard Index
        1 - Hausdorff distance
        -m <geo 1 (wkt form)> <geo 2 (wkt form)> <metric> <file to save (optional)>
        //usage example for similarity metric:
        -m "POLYGON ((949 781, 926 812, 891.6666656434536 796.66666620969772, 857.3333312869072 781.33333241939545, 823 766, 804 736, 807 689, 810 642, 833.6666673719883 612.99999913573265, 857.33333474397659 583.9999982714653, 881 555, 892 572, 909 576, 914 583, 922 580, 928 597, 920 640, 946 640, 968 647.5, 990 655, 949 781))" "POLYGON ((972 774, 955 806, 922 817, 905 816, 850 782, 822 755, 812 697, 812 656, 835 582, 848 565, 863 562, 876 576, 893 576, 899 583, 909 579, 917 592, 916 637, 962 632, 968 638, 990 640, 972 774))" 1 "C:\Users\bjpsi\Desktop\Investigacao\SPT-DataLab\save.txt"
        */

public class SPTDataLab
{
    private static final String PROP_SWING_DEFAULTLAF = "swing.defaultlaf";
    
    private static final String OPT_GEOMFUNC = "geomfunc";
    
    private TestBuilderModel tbModel = new TestBuilderModel(false);
    
    private TestBuilderModel tbModel2 = new TestBuilderModel(true);
    
    boolean packFrame = false;
    
    public static SPTDataLab instance()
    {
        return app;
    }
    
    public static TestBuilderModel model() { return instance().tbModel; }
    
    public static TestBuilderModel model2() { return instance().tbModel2; }
    
   
    public static SPTDataLab app;
    
    
    
    public static PrecisionModel getPrecisionModel()
    {
        return model().getPrecisionModel();
    }
    
    public static GeometryFactory getGeometryFactory()
    {
        return model().getGeometryFactory();
    }
    
    /**Construct the application*/
    public SPTDataLab() {
    }
    
    private void initFrame()
    {
        SPTDataLabBuilderFrame frame = new SPTDataLabBuilderFrame();
        frame.setModel(model(), model2());
        
        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        /*if (packFrame) {
        frame.pack();
        } else {
        frame.validate();
        }*/
        frame.validate();
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation(
                (screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }
    
    
    /**Main method*/
    public static void main(String[] args)
    {
        
       
        switch (args.length) {
            case 6:
            case 5:
            case 4:
                //handle interpolation or metric logic
                String sourceWKT = args[1];
                String targetWKT = args[2];
            switch (args[0]) {
                case "-i":
                    //interpolation at instant
                    double instant = 0;
                    int interpolationMethod = 1;
                    try{
                        instant = Double.parseDouble(args[3]);
                        interpolationMethod = Integer.parseInt(args[4]);
                    } catch (NumberFormatException ex){
                        System.err.println("Instant and interpolation method must be a number");
                        System.exit(1);
                    }
                    if (instant < 0.0 || instant > 1.0){
                        System.out.println("Instant must be a value between 0.0 and 1.0");
                        return;
                    }
                    System.out.println("Performing interpolation at instant " + instant);
                    InterpolationMethodFacade interpolation = new InterpolationMethodFacade(sourceWKT, targetWKT, 0.0, 1000.0);
                    InterpolationMethodEnum method;
                    
                    method = getMethodEnum(interpolationMethod);
                    System.out.print("Applying interpolation method: " +method+"\n");
                    
                    //convert instant from range 0.0 - 1.0 to 0.0 - 1000.0 (SPTMesh has a bug if range is 0 - 1)
                    double instantConversion = instant * 1000.0;
                    String interpolationResult = interpolation.interpolationAtInstant(instantConversion, method);
                    if (interpolationResult == null){
                        System.out.println("Error performing interpolation.");
                        return;
                    }
                    if (args.length == 6 && !args[5].isEmpty()){
                        //save result to file
                        try {
                            System.out.println("Writing to file");
                            PrintWriter writer = new PrintWriter(args[5], "UTF-8");
                            writer.println(interpolationResult);
                            writer.close();
                        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                            //Logger.getLogger(SPTDataLab.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Invalid directory or file.");
                        }
                    }
                    else{
                        //no file to write results provided. Print result
                        System.out.println("Result: \n" + interpolationResult);
                    }
                    System.out.println("Finished");
                    return;
                case "-p":
                    //interpolation during period
                    System.out.println("Performing interpolation during period");
                    int nSamples = 0;
                    interpolationMethod = 1;
                    try{
                        nSamples = Integer.parseInt(args[3]);
                        interpolationMethod = Integer.parseInt(args[4]);
                    } catch (NumberFormatException ex){
                        System.err.println("Number of samples and interpolation method must be a number");
                        System.exit(1);
                    }
                    if (nSamples <= 1){
                        System.out.println("Number of samples must be at least 2.");
                        return;
                    }   
                    interpolation = new InterpolationMethodFacade(sourceWKT, targetWKT, 0.0, 1000.0);
                    method = getMethodEnum(interpolationMethod);
                    System.out.print("Applying interpolation method: " +method+"\n");
                    String[] interpolationsResults = interpolation.interpolationDuringPeriod(0.0, 1000.0, nSamples, method);
                    
                    if (interpolationsResults == null){
                        System.out.println("Error performing interpolation.");
                        return;
                    }
                    
                    if (args.length == 6 && !args[5].isEmpty()){
                        //save results to file
                        try {
                            System.out.println("Writing to file");
                            PrintWriter writer = new PrintWriter(args[5], "UTF-8");
                            for (String wkt : interpolationsResults)
                                writer.println(wkt);
                            writer.close();
                        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                            //Logger.getLogger(SPTDataLab.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Invalid directory or file.");
                        }
                    }
                    else{
                        //no file to write results provided. Print result
                        System.out.println("Results:");
                        for (String wkt : interpolationsResults)
                            System.out.println(wkt);
                    }
                    System.out.println("Finished");
                    return;
                case "-m":
                    //calculate metrics
                    System.out.print("Calculating similarity metric ");
                    SimilarityMetricsEnum metric;
                    int metricInt = 0;
                    try{
                        metricInt = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ex){
                        System.err.println("Metric method must be an integer number. Use -h to show help.");
                        System.exit(1);
                    }
                    switch (metricInt){
                        case 0:
                            metric = SimilarityMetricsEnum.Jaccard_Index;
                            break;
                        case 1:
                            metric = SimilarityMetricsEnum.Hausdorff_Distance;
                            break;
                        default:
                            metric = SimilarityMetricsEnum.Jaccard_Index;
                            break;
                    }
                    System.out.println(metric.toString());
                    Metrics metrics = new Metrics();
                    double value = metrics.computeSimilarityMetric(sourceWKT, targetWKT, metric);
                    if (args.length == 5 && !args[4].isEmpty()){
                        //save result to file
                        try {
                            System.out.println("Writing to file");
                            PrintWriter writer = new PrintWriter(args[4], "UTF-8");
                            writer.println("geometry1 -> "+sourceWKT);
                            writer.println("geometry2 -> "+targetWKT);
                            writer.println("Similarity using " + metric.toString() + " is "+value);
                            writer.close();
                        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                            //Logger.getLogger(SPTDataLab.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Invalid directory or file.");
                        }
                    }
                    else{
                        System.out.println("Similarity: "+value);
                    }
                    break;
                default:
                    printHelpMessages();
                    break;
            }

            case 1:
                if (args[0].equals("-h")){
                    printHelpMessages();
                }
                else if (args[0].equals("-c")){
                    System.out.println("Installation requirements:");
                    System.out.println("To use SPTMesh interpolation algorithm, this application must be executed on windows OS with a 32 bit JDK folder. The jar must be executed in the same directory as the DLL's.");
                    System.out.println("To use PySpatioTemporalGeom interpolation algorithm, python 2.7 must be installed and pyspatiotemporalgeom"
                            +"library must be installed using pip install pyspatiotemporalgeom. (Most recent version might not be updated for pip yet."
                            + " In this case, access this link https://bitbucket.org/marmcke/pyspatiotemporalgeom/commits/all and download the most recent"
                            + " commit file and place it inside the Lib\\site-packages located in python installation directory.");
                    System.out.println("To use Secondo interpolation algorithm, you must be connected to UA's eduroam (by being in the university or using a vpn");
                }   break;
            case 0:
                //no arguments provided, start application normally, with UI
                
                //check if there is a directory with last used directory for images and correlation files
                //first line of file -> directory with images
                //second line -> directory with correlation files
                File f = new File(CACHE_FILE);
                File dirImages = null;
                File dirCorr = null;
                boolean isValid = false;
                if(f.exists() && !f.isDirectory()) {
                    //file exists
                    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                        String dirImageStr;
                        String dirCorrStr;
                        //check if there are 2 lines in file (one with images directory, second with corr files directory)
                        if ( (dirImageStr = br.readLine()) != null && (dirCorrStr = br.readLine()) != null){
                            //images directory
                            dirImages = new File(dirImageStr);
                            dirCorr = new File(dirCorrStr);
                            //check if directories exist and contain approrpiate files
                            if ( (dirImages.exists() && dirImages.isDirectory() && dirImages.listFiles().length > 0)
                                    && (dirCorr.exists() && dirCorr.isDirectory() && dirCorr.listFiles().length > 0)) {
                                isValid = true;
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(SPTDataLab.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if (isValid){
                    // ask user if he wants to use last dataset or choose another
                    int dialogResult = JOptionPane.showConfirmDialog (null, "Would you like to reuse your dataset used in the previous session?",
                            "Choose dataset", JOptionPane.YES_NO_OPTION);
                    switch (dialogResult) {
                        
                        case JOptionPane.YES_OPTION:
                            // load files from directories previously used
                            AppImage.getInstance().loadImages(dirImages.listFiles());
                            AppCorrGeometries.getInstance().setNewCoordinatesDataset(dirCorr);
                            startApp();
                            return;
                        case JOptionPane.NO_OPTION:
                            break;
                        default:
                            return;
                    }
                }
                
                //load images and coordinates files (also stores the selected dataset location in a cache file)
                boolean success = DatasetLoader.loadAndSetDataset();
                if (dirImages == null || dirCorr == null)
                    return; //close program
                
                startApp();
                break;
                
            default:
                //wrong parameters passed. Inform user
                printHelpMessages();
                break;
        }
    }
    
    private static void startApp(){
        //readArgs(args);
        //setLookAndFeel();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch(Exception ignored){}
        app = new SPTDataLab();
        app.initFrame();
    }
    
    private static void printHelpMessages(){
        System.out.println("Usage (interpolation at instant): -> -i <Source wkt> <Target wkt> <instant> <interpolation method number> <file to save (optional)>");
        System.out.println("Usage (interpolation during period): -> -p <Source wkt> <Target wkt> <number of samples> <interpolation method number> <file to save (optional)>");
        System.out.println("Available interpolation methods are: \n 0 - SPTMesh (Note: requires that both geometries have the same number of points)\n 1 - PySpatioTemporalGeom\n 2 - Secondo");
        System.out.println("Usage (metrics): -> -m <wkt1> <wkt2> <metric number> <interpolation method number> <file to save (optional)>");
        System.out.println("Available similarity metrics are: \n 0 - Jaccard Index\n 1 - Hausdorff distance");
        System.out.println("If no file to save results is provided, then result will be printed.");
        System.out.println("-h: shows this help message.\n-c: Shows necessary installations needed to use the diferent interpolation and similarity metrics libraries");
    }
    
    /**
     * Sets the look and feel, using user-defined LAF if
     * provided as a system property.
     *
     * e.g. Metal: -Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel
     *
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    private static void setLookAndFeel() throws InterruptedException, InvocationTargetException
    {
        /**
         * Invoke on Swing thread to pass Java security requirements
         */
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run()
            {
                try {
                    String laf = System.getProperty(PROP_SWING_DEFAULTLAF);
                    if (laf == null) {
                        laf = UIManager.getSystemLookAndFeelClassName();
                    }
                    javax.swing.UIManager.setLookAndFeel(laf);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    public static InterpolationMethodEnum getMethodEnum(int methodInt){
        switch (methodInt){
            case 0:
                return InterpolationMethodEnum.SPTMesh;
            case 1:
                return InterpolationMethodEnum.PySpatioTemporalGeom;
            case 2:
                return InterpolationMethodEnum.Secondo;
            default:
                return InterpolationMethodEnum.PySpatioTemporalGeom;
        }
    }
    
}