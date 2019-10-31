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

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;

import org.locationtech.jts.awt.FontGlyphReader;
import ua.ieeta.sptdatalab.model.GeometryDepiction;
import ua.ieeta.sptdatalab.ui.ColorUtil;


public class AppConstants 
{
    public static final int POINT_SIZE = 5;
    public static final int VERTEX_SIZE = 4;
    public static double HIGHLIGHT_SIZE = 50.0;
    public static double VERTEX_SHADOW_SIZE = 100;
    
    
    public static double TOPO_STRETCH_VIEW_DIST = 5;

    public static double  MASK_WIDTH_FRAC = 0.3333;
    // a very light gray
    public static final Color MASK_CLR = new Color(230, 230, 230);
	
    public static final Color VERTEX_SHADOW_CLR = new Color(180,180,180);
    public static final Color VERTEX_HIGHLIGHT_CLR = new Color(255, 255, 0);

    public static final Color HIGHLIGHT_CLR = new Color(255, 192, 0, 150);
    public static final Color HIGHLIGHT_FILL_CLR = new Color(255, 240, 192, 200);
  
    public static final Color BAND_CLR = new Color(255, 0, 0, 255);
    public static final Color INDICATOR_FILL_CLR = GeometryDepiction.GEOM_RESULT_FILL_CLR;
    //public static final Color INDICATOR_LINE_COLOR = new Color(255, 0, 0, 255);
    //public static final Color INDICATOR_FILL_COLOR = new Color(255, 200, 200, 200);
    public static final Color INDICATOR_LINE_CLR = GeometryDepiction.GEOM_RESULT_LINE_CLR;

    public static final int AXIS_WIDTH = 3;
    public static final Color AXIS_CLR = Color.lightGray;

    public static final Color GRID_CLR = Color.lightGray;

    public static final Color GRID_MAJOR_CLR = ColorUtil.gray(240);
    public static final Color GRID_MINOR_CLR = ColorUtil.gray(190);
    public static int TOLERANCE_PIXELS = 5;

  
    public final static Font FONT_LABEL = new Font(FontGlyphReader.FONT_SANSSERIF, Font.BOLD, 12);

    //--------------- name of property changes --------------------
    public final static String INTERPOLATION_ANIMATION_FRAME_PROPERTY = "animationFrame";
    
    public final static String IMAGE_LEFT_PANEL_PROPERTY = "leftPanelImage";
    
    public final static String IMAGE_RIGHT_PANEL_PROPERTY = "rightPanelImage";
    
    public final static String GEOMETRY_FILE_CHANGE_PROPERTY = "geometryFile";
    
    public final static String COORD_LEFT_PANEL_PROPERTY = "leftPanelCoords";
    
    public final static String COORD_RIGHT_PANEL_PROPERTY = "rightPanelCoords";
    
    
    //---------------- headers to parse secondo file credential data
    public final static String SECONDO_HOST_HEADER = "host";
    public final static String SECONDO_PORT_HEADER = "port";
    public final static String SECONDO_DBNAME_HEADER = "database_name";
    public final static String SECONDO_OBJNAME_HEADER = "object_name";
    public final static String SECONDO_CREDENTIALS_FILENAME = "secondo_config.txt";
    
    //------------ cache file name where last opened data set is (first line: image directory
    //------------ second line: coordinates directory)
    public static final String CACHE_FILE = "last_dir_cache.txt";
    
    public static final double COORDINATE_ERROR_MAX = 0.005;
    
    //----------- File types accepted for coordinates and images
    public static final String[] COORDINATE_FILE_TYPES = {"wkt"};
    
    public static final String[] IMAGE_FILE_TYPES = {"png", "jpg", "jpeg", "bpm", "tiff"};
    
    //----------- Confirmation string used in some operations to confirm that it succeeded
    public static final String CONFIRMATION_STRING = "success";
    
    //------------- Error messages
    public static final String DATASET_LOADING_GENERAL_ERROR = "An error occurred while starting SPTDataLab. \n Please, " +
                               "make sure that the dataset you are using is according to the specifications in the project's wiki.";
    
    public static final String DATASET_LOADING_IMAGES_ERROR = "Images directory is either empty or contains files that are not supported. "
                    + "\nPlease specify a directory with valid image file formats.";
    
    public static final String DATASET_LOADING_COORDINATES_ERROR = "Coordinate files directory is either empty or contains files that are not supported. "
                    + "\nPlease specify a directory with valid coordinate file formats.";
    
    // number of max decimal places for the coordinates
    public static final int COORDINATE_MAX_DECIMAL_PLACE = 3;
    
    public static int getMaxNumberDecimalPlacesConverter(){
        String n = "1";
        for (int i = 0; i < COORDINATE_MAX_DECIMAL_PLACE; i++){
            n += "0";
        }
        return Integer.parseInt(n);
    }
    
    public static double limitMaxNumberOfDecimalPlaces(double d){
        int decimalPlaceConverter = getMaxNumberDecimalPlacesConverter();
        return Math.floor(d * decimalPlaceConverter) / decimalPlaceConverter;
    }
    
}
