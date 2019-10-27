package ua.ieeta.sptdatalab.app;

/* This file is part of SPT Data Lab.
*
* Copyright (C) 2019, University of Aveiro,
* DETI - Departament of Electronic, Telecommunications and Informatics.
*
* SPT Data Lab is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* SPT Data Lab is distributed "AS IS" in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with SPT Data Lab; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.MultiPolygon;
import ua.ieeta.sptdatalab.geom.GeometryLocation;
import ua.ieeta.sptdatalab.util.io.CorrToGeometryUtils;
import ua.ieeta.sptdatalab.util.io.GeometryWriter;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import ua.ieeta.sptdatalab.model.GeometryEditModel;
import ua.ieeta.sptdatalab.model.GeometryType;
import ua.ieeta.sptdatalab.util.CoordinateUtils;
import ua.ieeta.sptdatalab.util.GeometryObservation;

/**
 *
 * This class saves in memory the pixel coordinates of all geometries of the current loaded dataset, as well as managing
 * necessary transformations and changes to the current shown set of source-target geometry.
 * The coordinates in the files consider the original image size, but in the GUI it is likely that the image's dimensions
 * change, therefore, a transformation to this coordinates needs to be made so that the geometry represented by the coordinates
 * align correctly with the image. To do so, we transform the coordinates each time a source-target pair is selected in the gui
 * considering the currect size of the image.
 * We also keep the original coordinates in memory and update this coordinates 
 * for every update the user makes to the geometries.
 * 
 */

public class AppCorrGeometries implements PropertyChangeListener{
    
    //stores the original coordinates read from the corr file
    private Map<Integer, List<Coordinate>> originalGeometriesNotEdited;//original geometries read from file, not edited by the user
    
    private List<GeometryObservation> geometriesInPanel;//to save all geometries (may have been edited by user or not).
    
    private List<GeometryObservation> geometriesInPanelOriginalScale;//geometries with original size (may have been edited by the user)
    
    private Map<Integer, Boolean> observationsEdited; //indicates if geometry in index x of the previous list has been edited or not
    
    private Map<Integer, File> fileObservations; //the file that contains each observation

    private List<String> geoDates = new ArrayList<>();
    
    private List<Coordinate> drawnPoints = new ArrayList<>();
    
    private Map<BufferedImage, Coordinate> images = new HashMap<>();//used to draw red points indicating the correspondences between source and target
    
    private boolean isEdited;
    
    private int currentObservationNumber;//used to identify current source geometry number and target number (equal to source+1)
    
    private File corrDir;
    
    private static final int IMAGE_HEIGHT = 10;
    
    private static final int IMAGE_WIDTH = 10;
    
    private static final int MAX_POINTS_STORED = 2;
    
    private SPTDataLabBuilderFrame frame;
    
    private int editIndex = -1;
    
    private static AppCorrGeometries instance;
    
    private PropertyChangeSupport support;
    
    private AppCorrGeometries() {
        this.isEdited = false;
        currentObservationNumber = 0;
        geometriesInPanel = new ArrayList<>();
        geometriesInPanelOriginalScale = new ArrayList<>();
        observationsEdited = new HashMap<>();
        originalGeometriesNotEdited = new HashMap<>();
        fileObservations = new HashMap<>();
        support = new PropertyChangeSupport(this);
        AppImage.getInstance().addPropertyChangeListener(this);
    }
    
    public static AppCorrGeometries getInstance(){
        if(instance == null){
            instance = new AppCorrGeometries();
        }
        return instance;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
 
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
    
    //Load a new dataset, with new geometries, clear all
    public void setNewCoordinatesDataset(File corrDir){
        this.corrDir = corrDir;
        this.currentObservationNumber = 0;
        this.observationsEdited.clear();
        this.geometriesInPanel.clear();
        this.geometriesInPanelOriginalScale.clear();
        this.originalGeometriesNotEdited.clear();
        this.isEdited = false;
        loadCoordinatesFiles(corrDir.listFiles());
    }
    
    /**
     * Arrange in a map the file in the supplied directory that corresponds to the number of the observation. File name should follow a convention
     * in order to identify the number of source and target represented in the file.
     * it is assumed that the corr files have a <number>_<number>_<number> in the name, with the number
     * in the middle indicating the corresponding source number (equal to current observation number), 
     * and the number in left the target number.
     * Example: observation number 1 should be in a file named ....1_2.extension
     * @param files - list of files with coordinates in directory
     */
    private void loadCoordinatesFiles(File[] files){
        fileObservations.clear();
        List<File> filesArray = new ArrayList<>(Arrays.asList(files));
        for (int i = 0; i < files.length; i++){
            Pattern pattern = Pattern.compile("[_\\d+]*_"+i+"_\\d+");
            for (File corrFile : filesArray){
                Matcher matcher = pattern.matcher(corrFile.getName());
                if (matcher.find()){
                    this.fileObservations.put(i, corrFile);
                    loadOriginalObservationFromFile(i, corrFile);
                    filesArray.remove(corrFile);
                    break;
                }
            }
        }
    }
    
    private void loadOriginalObservationFromFile(int observationNumber, File f){
        //load new observation to memory
        CorrToGeometryUtils corrToGeomUtils = new CorrToGeometryUtils(f);
        
        //load source and target from file
        List<Coordinate> sourceCoordsFromFile = corrToGeomUtils.getSourceCoordsFromFile();
        List<Coordinate> targetCoordsFromFile = corrToGeomUtils.getTargetCoordsFromFile();
        //store original coordinates of the geometries from this observation
        updateOriginalGeometries(sourceCoordsFromFile, targetCoordsFromFile, observationNumber);
    }
    
    
    /**
     * Return a file with the specified observation number. (null if there is no file).
     * @param nObservation - observation number (equal to source number)
     * @return File with the observation number. Null if there is no file.
     */
    private File getObservationFile(int nObservation){
        return this.fileObservations.get(nObservation);
    }
    
    /**Returns the coordinates of a geometry for one of the panels, with coordinates rearranged to fit the panel.
    * This is called by the geometry edit panel (the left and right), when they are loaded and need to draw or redraw the 
    * currently selected geometry.
    */
    public List<Coordinate> getSourceTargetCoordsFixedForScreen(GeometryEditPanel editPanel){
        if (editPanel.isSecondPanel()){//target
            updateAndLoadObservation(false);
            return this.getCurrentTarget();
        }
        else{//source
            updateAndLoadObservation(true);
            return this.getCurrentSource();
        }
    }
    
    /**
     * (Re)loads geometries in panel by (re)applying transformation to fit the screen.
     * 
     * @param isSource true - is source geometry, false otherwise
     */
    private void updateAndLoadObservation(boolean isSource){
        int index = geometryIndexExists(this.currentObservationNumber);
        if (index <= -1 ){
            //if transformed coordinates hasnt been loaded yet or not edited, use the original geometries and transform again
            if (isSource){
                List<Coordinate> originalSource = this.originalGeometriesNotEdited.get(this.currentObservationNumber);
                geometriesInPanel.add(new GeometryObservation(currentObservationNumber, correctCoordinates(originalSource, isSource), 
                    new ArrayList<>()));//new entry, empty target will be updated soon (source panel will call this method again, and update with source)
            }
            else{
                List<Coordinate> originalTarget = this.originalGeometriesNotEdited.get(this.currentObservationNumber+1);
                geometriesInPanel.add(new GeometryObservation(currentObservationNumber, new ArrayList<>(), 
                    correctCoordinates(originalTarget, isSource)));//new entry, empty source will be updated soon (target panel will call this method again, and update with target)
            }
            observationsEdited.put(this.currentObservationNumber, false);
        }
        else{
            if (this.observationsEdited.get(currentObservationNumber) == false){
                //observation loaded (or partially loaded), not yet edited, update geometry
                updateInfoGeometries(isSource, false);
                updateInfoGeometries(isSource, false);
            }
            else{
                //if the observation is already loaded but edited, transform to original coordinates, then retransform to 
                //fit the panels, this way we dont loose the edition made by the user
                GeometryObservation g = this.getCurrentObservation();
                if (isSource){
                   // updateInfoGeometriesOriginalScale(source, isSource, true);
                    updateInfoGeometries(true, observationsEdited.get(currentObservationNumber));
                }
                else{
                   // updateInfoGeometriesOriginalScale(target, isSource, true);
                    updateInfoGeometries(false, observationsEdited.get(currentObservationNumber));
                }
            }
        }
    }
    
    /**
    * Updtade the coordinates of the geometries currently shown in the panels.
    * The coordinates are changed to fit inside the panel and so that no misalignment occurs.
    * This is useful when the size of the window changes and user changes observations
     * Also indicates whether a coordinate list has been edited or not by the user.
     * Source edit panel and target edit panel will indirectly call this method.
     * @param coordsList - already transformed to fit the screen
     * @param isSource
     * @param edited 
     */
    private void updateInfoGeometries(boolean isSource, boolean edited){
        int indexInList = geometryIndexExists(this.currentObservationNumber);
        if (indexInList >= 0){
            GeometryObservation g = geometriesInPanel.get(indexInList);
            GeometryObservation gOriginal = this.getGeometryObservationInPanelOriginal(currentObservationNumber);
            if (g.getObservationNumber() == currentObservationNumber){
                if (isSource){
                    g.setSource(correctCoordinates(gOriginal.getSource(), isSource));
                }
                else{
                    g.setTarget(correctCoordinates(gOriginal.getTarget(), isSource));
                }
            }
            geometriesInPanel.set(indexInList, g);
            observationsEdited.put(this.currentObservationNumber, edited);
        }
    }
    
        void updateInfoGeometriesOriginalScale(List<Coordinate> coords, boolean isSource, boolean isOriginalScale){
        //first validate that last coordinate equals first coordinate
        if (!coords.get(0).equals(coords.get(coords.size()-1))){
            coords.set(coords.size()-1, coords.get(0));
        }
        if (!isOriginalScale)
            coords = transformToOriginalCoordinates(coords, isSource);
        int indexInList = geometryIndexExists(this.currentObservationNumber);
        if (indexInList > -1){
            GeometryObservation g = this.geometriesInPanelOriginalScale.get(indexInList);
            g.setGeometryCoordinates(coords, isSource);
        }
        //this.updateInfoGeometries(isSource, isEdited);
    }

    /**
     * Returns the geometry observation (which includes source and target) with original scale of the current observation in panel.
     * 
     * @param observationNumber
     * @return Current Geometry observation with original scale coordinates
     */
    public GeometryObservation getGeometryObservationInPanelOriginal(int observationNumber){
        for (GeometryObservation g : geometriesInPanelOriginalScale){
            if (g.getObservationNumber() == observationNumber){
                return g;
            }
        }
        return null;
    }
    
    public void updateGeometriesFromWKTPanel(String sourceWkt, String targetWkt){
        try {
            WKTReader wktReader = new WKTReader();
            Geometry sourceWKTPanel = wktReader.read(sourceWkt);
            Geometry targetWKTPanel = wktReader.read(targetWkt);
            
            String sourceWKT = convertCoordinatesToWKT(getCurrentSource());
            String targetWKT = convertCoordinatesToWKT(getCurrentTarget());
            //compare the wkt in panel (that user loaded) with whats on screen.
            //If there are no changes to source and/or target, do nothing
            if (!WKTTextsEqual(sourceWKTPanel.toText(), sourceWKT)){
                //update source geometry with the geometry on wkt panel
                this.isEdited = true;
                this.updateInfoGeometriesOriginalScale(new ArrayList<>(Arrays.asList(sourceWKTPanel.getCoordinates())), true, true);
                this.updateInfoGeometries(true, true);
            }
            if(!WKTTextsEqual(targetWKTPanel.toText(), targetWKT)){
                this.isEdited = true;
                this.updateInfoGeometriesOriginalScale(new ArrayList<>(Arrays.asList(targetWKTPanel.getCoordinates())), false, true);
                this.updateInfoGeometries(false, true);
            }
        } catch (ParseException ex) {
            Logger.getLogger(AppCorrGeometries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns the corresponding list of coordinates in WKT format (as a Polygon).
     * @param coords 
     * @return  - wkt text format of the geometry represented by the coordinates
     */
    public String convertCoordinatesToWKT(List<Coordinate> coords){
        Coordinate[] coordArr = new Coordinate[coords.size()];
        coordArr = coords.toArray(coordArr);

        return new GeometryFactory().createPolygon(coordArr).toText();
    }
    
    /**
     * Compares two wkt texts and returns true of both are equal (have the same coordinates, regardless of order in text)
     * and false otherwise.
     * @param wkt1
     * @param wkt2
     * @return true if both wkt texts are equal, false otherwise
     */
    private boolean WKTTextsEqual(String wkt1, String wkt2){
        char[] firstWKT = wkt1.toCharArray();
        char[] secondWKT = wkt2.toCharArray();
        Arrays.sort(firstWKT);
        Arrays.sort(secondWKT);
        return Arrays.equals(firstWKT, secondWKT);
    }
    
    //returns the index in the list of observations if found, -1 if not
    private int geometryIndexExists(int index){
        for (int i = 0; i <  geometriesInPanel.size(); i++){
            if (geometriesInPanel.get(i).getObservationNumber() == index)
                return i;
        }
        return -1;
    }
    
    /**
     * Updates the geometries with the coordinates of the original pixels.
     * @param coordsListSource
     * @param coordsListTarget
     * @param observationNumber 
     */
    private void updateOriginalGeometries(List<Coordinate> coordsListSource, List<Coordinate> coordsListTarget, int observationNumber){
        this.originalGeometriesNotEdited.put(observationNumber, coordsListSource);
        this.originalGeometriesNotEdited.put(observationNumber+1, coordsListTarget);
        this.geometriesInPanelOriginalScale.add(new GeometryObservation(observationNumber, coordsListSource, coordsListTarget));
    }
    
    // getters for the geometries and coordinates list
    public GeometryObservation getCurrentObservation(){
        return getObservation(this.currentObservationNumber);
    }
    
    public GeometryObservation getObservation(int index){
        for (GeometryObservation g : geometriesInPanel){
            if (g.getObservationNumber() == index)
                return g;
        }
        return null;
    }
    
    public GeometryObservation getOriginalScaleObservation(int index){
        for (GeometryObservation g : geometriesInPanelOriginalScale){
            if (g.getObservationNumber() == index)
                return g;
        }
        return null;
    }
    
    public GeometryObservation getCurrentOriginalScaleObservation(){
        return getOriginalScaleObservation(this.currentObservationNumber);
    }
    
    
    public List<Coordinate> getCurrentSource(){
        GeometryObservation g = getCurrentObservation();
        if (g != null)
            return g.getSource();
        return null;
    }
    
    public List<Coordinate> getCurrentTarget(){
        GeometryObservation g = getCurrentObservation();
        if (g != null)
            return g.getTarget();
        return null;
    }
    
    public List<Coordinate> getOriginalScaleSource(int index){
        GeometryObservation g = getOriginalScaleObservation(index);
        if (g != null)
            return g.getSource();
        return null;
    }
    
    public List<Coordinate> getOriginalScaleTarget(int index){
        GeometryObservation g = getOriginalScaleObservation(index);
        if (g != null)
            return g.getTarget();
        return null;
    }
    
    public List<Coordinate> getCurrentSourceOriginalNotEdited(){
        return this.originalGeometriesNotEdited.get(this.currentObservationNumber);
    }
    
    public List<Coordinate> getCurrentTargetOriginalNotEdited(){
        return this.originalGeometriesNotEdited.get(this.currentObservationNumber+1);
    }
    
    /**corrects the given coordinatesaccording to the size of the panel, so that they align with the image in display
     * @param coord
     * @param editPanel
     * @return 
     */
    public List<Coordinate> correctCoordinates(List<Coordinate> coord, GeometryEditPanel editPanel){
        AppImage appImage = AppImage.getInstance();

        double currImageHeightInPanel = appImage.getImageHeightInPanel(editPanel.isSecondPanel());
        double currImageWidthInPanel = appImage.getImageWidthInPanel(editPanel.isSecondPanel());
        List<Coordinate> transformedCoords = new ArrayList<>();
        CoordinateUtils coordUtils;
        for (Coordinate c : coord){
            coordUtils = new CoordinateUtils(c.getX(), c.getY() );
            coordUtils.transformCoords(appImage.getImageOriginalWidth(editPanel.isSecondPanel()),
                    appImage.getImageOriginalHeight(editPanel.isSecondPanel()), currImageWidthInPanel, currImageHeightInPanel);
            
            //coordUtils.translate(new CoordinateUtils(0, -(editPanel.getSize().getHeight() - currImageHeightInPanel)));
            transformedCoords.add(coordUtils);
        }
        return transformedCoords;
    }
    
    private List<Coordinate> correctCoordinates(List<Coordinate> coord, boolean isSource){
        if (isSource)
            return correctCoordinates(coord, SPTDataLabBuilderFrame.getGeometryEditPanel());
        return correctCoordinates(coord, SPTDataLabBuilderFrame.getGeometryEditPanel2());
    }
    
    public MultiPolygon makePolygonFitComponent(MultiPolygon multiPol, JComponent comp){
        Polygon[] pol = new Polygon[multiPol.getNumGeometries()];
        for (int i = 0; i <  multiPol.getNumGeometries(); i++){
            Polygon p = (Polygon) multiPol.getGeometryN(i);
            Coordinate[] transformedCoords = correctCoordinates(p.getCoordinates(), comp);
            pol[i] = new GeometryFactory().createPolygon(transformedCoords);
        }
        return new GeometryFactory().createMultiPolygon(pol);
    }
    
    public Polygon makePolygonFitComponent(Geometry geo, JComponent comp){
        Coordinate[] transformedCoords = correctCoordinates(geo.getCoordinates(), comp);
        return new GeometryFactory().createPolygon(transformedCoords);
    }
    
    public Polygon makePolygonFitComponent(Polygon pol, JComponent comp){
        Coordinate[] transformedCoords = correctCoordinates(pol.getCoordinates(), comp);
        return new GeometryFactory().createPolygon(transformedCoords);
    }
    
    /**
     * Transform the coordinates of a geometry to fit inside a JPanel. Mainly used to adapt the cordinates of geometries
     * to fit inside the window in the morphing geometry animation window.
     * @param coord - the list of coordinates to transform
     * @param comp - the Java swing component
     * @return list of coordinates transformed to fits the Java swing component
     */
    public Coordinate[] correctCoordinates(Coordinate[] coord, JComponent comp){
        
        Coordinate[] transformedCoords = new Coordinate[coord.length];
        CoordinateUtils coordUtils;
        //first pass to see maximum x and y coordinates
        double maxX = 0;
        double maxY = 0;
        for (Coordinate c : coord){
            if (c.x > maxX){
                maxX = c.x;
            }
            if (c.y > maxY){
                maxY = c.y;
            }
        }
        int i = 0;
        int componentHeight = comp.getHeight();
        int componentWidth = comp.getWidth();
        for (Coordinate c : coord){
            coordUtils = new CoordinateUtils(c.getX(), c.getY() );
            
            coordUtils.transformCoords(maxX, maxY, componentWidth, componentHeight);
            coordUtils.translate(new CoordinateUtils(-(componentWidth/2), componentHeight/2));
            transformedCoords[i] = coordUtils;
            i++;
        }
        return transformedCoords;
    }
    
    /**returns the index of the coordinates in the array or -1 if it doesnt exist. 
     * The corresponding coordinate in the other geometry
    *on the other panel will be on the other list in the same index
    */
    public int getCordIndex(Coordinate c, boolean isSecondPanel){
        List<Coordinate> listToSearch;
        if(isSecondPanel){
            listToSearch = getCurrentTarget();
        }
        else{
            listToSearch = getCurrentSource();
        }
        for (int i = 0; i < listToSearch.size(); i++){
            Coordinate coordInPanel = listToSearch.get(i);
            //eliminate precision errors...
            if (almostEqual(c.x, coordInPanel.getX(), AppConstants.COORDINATE_ERROR_MAX)
                    && almostEqual(c.y, coordInPanel.getY(), AppConstants.COORDINATE_ERROR_MAX)){
                return i;
            }
        }
        //does not exist
        return -1;
    }
    
    public boolean almostEqual(double a, double b, double eps){
      return Math.abs(a-b) < eps;
    }
    
    /**
     * 
     * @param a List of Coordinates to search
     * @param cb Coordinate to compare
     * @param eps max difference in which to consider 2 coordinates "equal"
     * @return The index of the coordinate in the list. -1 if no coordinate is eps close to cb. 
     */
    public int getAlmostEqualPointIndex(List<Coordinate> a, Coordinate cb, double eps){
        for (int i = 0; i < a.size(); i++){
            if (almostEqual(a.get(i).x, cb.x, eps) && almostEqual(a.get(i).y, cb.y, eps)){
                return i;
            }
        }
        return -1;
    }
    
    public boolean collinearExists(List<Coordinate> a){
        for (int i = 0; i < a.size()-1; i++){
            for (int j = 0; j < a.size()-1; j++){
                if ( i != j && almostEqual(a.get(i).x, a.get(j).x, AppConstants.COORDINATE_ERROR_MAX) &&
                        almostEqual(a.get(i).y, a.get(j).y, AppConstants.COORDINATE_ERROR_MAX)){
                    System.out.println("i: "+i+" -> "+a.get(i) +"; j: "+j+" -> "+a.get(j));
                    return true;
                }
            }
        }
        return false;
    }
    
    //returns the matching coordinates of the other list of coordinates.
    //They have the same indexes in both lists
    public Coordinate getCorrespondingCoordinate(Coordinate c, boolean isSecondPanel){
        List<Coordinate> correspondingCoordinates;
        if (isSecondPanel){
            //we want the other list of coordinates!
            correspondingCoordinates = this.getCurrentSource();
        }
        else{
            correspondingCoordinates = this.getCurrentTarget();
        }
        int index = getCordIndex(c, isSecondPanel);
        if (index == -1){
            //not found
            return null;
        }
        return correspondingCoordinates.get(index);
    }
    
    //returns the matching coordinates of the other list of coordinates.
    //They have the same indexes in both lists
    public Coordinate getCorrespondingCoordinate(int index, boolean isSecondPanel){
        List<Coordinate> correspondingCoordinates;
        if (isSecondPanel){
            //we want the other list of coordinates!
            correspondingCoordinates = this.getCurrentSource();
        }
        else{
            correspondingCoordinates = this.getCurrentTarget();
        }
        if (index == -1){
            //not found
            return null;
        }
        return correspondingCoordinates.get(index);
    }
    
    //store the point(s) the cursor is in. call a method to higlight the point in the panel
    //that the cursor IS NOT IN
    public void higlightCorrespondingPointInPanel(Collection<Coordinate> coords, boolean isSecondPanel){
        for (Coordinate coord : coords){
            Coordinate c = getCorrespondingCoordinate(coord, isSecondPanel);
            if (c != null || drawnPoints.size() < MAX_POINTS_STORED){
                if (drawnPoints.contains(c)){
                    //this point is already marked
                    continue;
                }
                drawnPoints.add(c);
            }
        }
        //editPanel.setPointsDrawn(true);
        GeometryEditPanel editPanel;
        if(isSecondPanel){
            //we want to mark on the other panel
            editPanel = SPTDataLabBuilderFrame.getGeometryEditPanel();
        }
        else{
            editPanel = SPTDataLabBuilderFrame.getGeometryEditPanel2();
        }
        drawPoints(editPanel);
    }
    
    //draws a red dot on all points in the list of edited points in the panel the user is not interacting
    //This function is called on the render manager of the panel to be drawn the dots, after a
    //"force repaint" happens, because this method is asynchronous and this way the dots are not deleted.
    public void drawPoints(GeometryEditPanel editPanel){
        Graphics2D g2 = (Graphics2D) editPanel.getGraphics();
        g2.setStroke(new BasicStroke(6));
        g2.setColor(Color.red);
        Point2D point;
        BufferedImage bi;
        //draw the images to cover the red points previously drawn
        for (Map.Entry<BufferedImage, Coordinate> image : images.entrySet()){
            try{
                Point2D p = editPanel.getViewport().toView(image.getValue());
                g2.drawImage(image.getKey(), (int)p.getX()-IMAGE_WIDTH/2, (int)p.getY()-IMAGE_HEIGHT/2, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            } catch (java.awt.image.RasterFormatException e) {}
            
        }
        images.clear();
        //get the images cropped from the panel
        for (Coordinate c : drawnPoints){
            if (c!= null){
                point = editPanel.getViewport().toView(c);
                BufferedImage originalImage = (BufferedImage) editPanel.getRenderMgr().getImage();
                try{
                    bi = originalImage.getSubimage((int)point.getX()-IMAGE_WIDTH/2, (int)point.getY()-IMAGE_HEIGHT/2, IMAGE_WIDTH, IMAGE_HEIGHT);
                    this.images.put(bi, c);
                } catch (java.awt.image.RasterFormatException e) {}
                g2.drawLine((int)point.getX(), (int)point.getY(), (int)point.getX(), (int)point.getY());
            }
        }
        drawnPoints.clear();
    }
    
    //indicate that the user is editing a point (used when the user presses the mouse and is moving a point)
    public void savePointIfExistInCorrGeometry(Coordinate c, boolean isSecondPanel){
        int index = getCordIndex(c, isSecondPanel);
        if (index > -1){
            editIndex = index;
        }
    }
    
    //if a point from the corr geometry is moved, update the moved coordinate in the index
    //of the array with the edited corrGeometry
    public void editPointIfExistInCorrGeometry(double newX, double newY, boolean isSecondPanel){
        List <Coordinate> currentSourceGeometry = this.getCurrentSource();
        List <Coordinate> currentTargetGeometry = this.getCurrentTarget();
        //System.out.println("size -> "+currentSourceGeometry.size());
        //System.out.println("edit index -> "+editIndex);
        
        if (editIndex > -1){
            //System.out.println("source: before -> "+this.getOriginalScaleSource(this.currentObservationNumber).get(editIndex));
            Coordinate newC = new Coordinate(newX, newY);
            if (isSecondPanel){
                currentTargetGeometry.set(editIndex, newC);
                updateInfoGeometriesOriginalScale(currentTargetGeometry, false, false);
                updateInfoGeometries(false, true);
            }
            else{
                currentSourceGeometry.set(editIndex, newC);
                updateInfoGeometriesOriginalScale(currentSourceGeometry, true, false);
                updateInfoGeometries(true, true);
            }
            //there is no longer a point edited
            editIndex = -1;
            frame.reloadBothPanels();
            this.isEdited = true;
        }
    }
    
    //given a coordinate, if it is exists on one of the corr geometries, it is deleted from both geometries
    public void deletePointInBothCorrGeometries(Coordinate c){
        List <Coordinate> currentSourceGeometry = this.getCurrentSource();
        List <Coordinate> currentTargetGeometry = this.getCurrentTarget();
        int index = -1;
        int indexS = getAlmostEqualPointIndex(currentSourceGeometry, c, AppConstants.COORDINATE_ERROR_MAX);
        int indexT = getAlmostEqualPointIndex(currentTargetGeometry, c, AppConstants.COORDINATE_ERROR_MAX);
        if ( indexS > -1){
            index = indexS;
        }
        else if ( indexT > -1){
            index = indexT;
        }
        else{
            return;
        }
        if (index < 0)
            return;
        currentTargetGeometry.remove(index);
        currentSourceGeometry.remove(index);
        if (index == 0){
            //if first coordinate was removed, update last coordinate in the array, because they must be equals
            currentTargetGeometry.set(currentTargetGeometry.size()-1, currentTargetGeometry.get(0));
            currentSourceGeometry.set(currentSourceGeometry.size()-1, currentSourceGeometry.get(0));
        }
        this.updateInfoGeometriesOriginalScale(currentTargetGeometry, false, false);
        this.updateInfoGeometriesOriginalScale(currentSourceGeometry, true, false);
        updateInfoGeometries(true, true);
        updateInfoGeometries(false, true);
        frame.reloadBothPanels();
        this.isEdited = true;
    }
    
    // given a list of coordinates removed by the user in one of the geometries in one panel, if they belong
    //to a corr geometry, delete that coordinate from both corr geometries
    public Coordinate[] deleteListOfPointsInBothCorrGeometries(List<Coordinate> coords, boolean isSecondPanel){
        List<Coordinate> interactedPanel;
        List<Coordinate> otherPanel;
        if (isSecondPanel){
            interactedPanel = this.getCurrentTarget();
            otherPanel = this.getCurrentSource();
        }
        else{
            interactedPanel = this.getCurrentSource();
            otherPanel = this.getCurrentTarget();
        }
        
        for(Coordinate c : coords){
            int index = getAlmostEqualPointIndex (interactedPanel, c, AppConstants.COORDINATE_ERROR_MAX);
            if (index > -1){
                //remove, this point was deleted
                interactedPanel.remove(index);
                otherPanel.remove(index);
            }
        }
        //update the geometries in list
        this.setCorrGeometry(interactedPanel, isSecondPanel);
        this.setCorrGeometry(otherPanel, isSecondPanel);
        return otherPanel.toArray(new Coordinate[otherPanel.size()]);
    }
    
    //given a new coordinate for a panel, add that coordinate to the list (between the 2 points)
    //and find a corresponding coordinate to add in the other panel. Returns this corresponding coordinate
    public Coordinate addPointToCorrGeometries(GeometryLocation loc, boolean isSecondPanel){
        List<Coordinate> interactedPanelCoords;
        List<Coordinate> otherPanelCoords;
        Coordinate newCoordinate = loc.getCoordinate();
        List<Coordinate> nearbyCoords = loc.get2CoordsInSegment();//size 2
        if (isSecondPanel){
            interactedPanelCoords = this.getCurrentTarget();
            otherPanelCoords = this.getCurrentSource();
        }
        else{
            interactedPanelCoords = this.getCurrentSource();
            otherPanelCoords = this.getCurrentTarget();
        }
        //System.out.println("nearby coords: "+nearbyCoords);
        //add coordinate in both panels, but first, find the points closest to the new point in a line segment
        int index1 = this.getCordIndex(nearbyCoords.get(0), isSecondPanel);
        int index2 = this.getCordIndex(nearbyCoords.get(1), isSecondPanel);
        if (index1 > -1 && index2 > -1){//they exist in the geometry
            int [] indexes = new int[]{index1, index2};
            List indexesList = Arrays.asList(ArrayUtils.toObject(indexes));
            //the new coordinate will now be on the position of the coordinate with the biggest index
            int indexForNewCoord = Integer.parseInt(Collections.max(indexesList).toString());
            int minIndex = Integer.parseInt(Collections.min(indexesList).toString());
            if (indexForNewCoord == interactedPanelCoords.size()-1 && minIndex == 0){
                //special case: simply add to the last position the new coordinate
                indexForNewCoord = interactedPanelCoords.size();
            }
            interactedPanelCoords.add(indexForNewCoord, newCoordinate);
            Coordinate newCoordinateOtherPanel = findPointInLine(interactedPanelCoords, otherPanelCoords,
                    newCoordinate, indexForNewCoord, isSecondPanel);
            //add the new coordinate found in the respective line segment for the other geometry
            otherPanelCoords.add(indexForNewCoord, newCoordinateOtherPanel);
            
            if (isSecondPanel){
                this.updateInfoGeometriesOriginalScale(otherPanelCoords, true, false);
                this.updateInfoGeometriesOriginalScale(interactedPanelCoords, false, false);
                updateInfoGeometries(true, true); //source
                updateInfoGeometries(false, true);//target
            }
            else{
                this.updateInfoGeometriesOriginalScale(otherPanelCoords, false, false);
                this.updateInfoGeometriesOriginalScale(interactedPanelCoords, true, false);
                updateInfoGeometries(true, true); //source
                updateInfoGeometries(false, true);//target
            }
            this.isEdited = true;
            return newCoordinateOtherPanel;
        }
        return null;
    }
    
    //find a point in the respective line segment on the other panel to add a point
    private Coordinate findPointInLine(List<Coordinate> referenceCoords, List<Coordinate> coordsToAddNewPoint,
            Coordinate newCoord, int newPointNumber, boolean isSecondPanel){
        Coordinate c1 = referenceCoords.get(newPointNumber-1);
        Coordinate c2 = referenceCoords.get(newPointNumber+1);
        double distanceC1_newCoord = c1.distance(newCoord);
        //total distance between the points that a coordinate was added between in the original panel
        double totalDistance = c1.distance(c2);
        double c1_newCoordRatio = (100.0*distanceC1_newCoord)/totalDistance;
        
        Coordinate c1Target = coordsToAddNewPoint.get(newPointNumber-1);
        Coordinate c2Target = coordsToAddNewPoint.get(newPointNumber);//point hasnt been added yet!
        
        LineSegment l = new LineSegment();
        l.setCoordinates(c1Target, c2Target);
        
        Coordinate aproxCoordOtherPanel;
        if(c1_newCoordRatio > 60){
            //the new point is closer do point c2
            LineSegment midLine = new LineSegment();
            midLine.setCoordinates(c2, l.midPoint());
            aproxCoordOtherPanel = midLine.midPoint();
        }
        else if(c1_newCoordRatio < 40){
            //the new point is closer do point c1
            LineSegment midLine = new LineSegment();
            midLine.setCoordinates(c1, l.midPoint());
            aproxCoordOtherPanel = midLine.midPoint();
        }
        else{
            //its more or less in the middle (40 to 60% closer to c1)
            aproxCoordOtherPanel = l.midPoint();
        }
        
        Point p = new GeometryFactory().createPoint(aproxCoordOtherPanel);
        
        Polygon corrGeometryInOtherPanel = (Polygon) getGeometryInPanel(!isSecondPanel);//get the other panel
        DistanceOp dOP = new DistanceOp(corrGeometryInOtherPanel, p);
        
        return dOP.nearestPoints()[0];
    }
    
    //returns the polygon read from the corr file in the panel
    public Geometry getGeometryInPanel(boolean isSecondPanel){
        GeometryFactory gf = new GeometryFactory();
        if (isSecondPanel){
            return gf.createPolygon(this.getCurrentTarget().toArray(new Coordinate[this.getCurrentTarget().size()]));
        }
        else{
            return gf.createPolygon(this.getCurrentSource().toArray(new Coordinate[this.getCurrentSource().size()]));
        }
    }
    
    /** * Returns the Geometry objects of either the source or target of the current observation in panel.The points of the geometry have the original scale
     * @param isSource - true, will return the source, false the target
     * @return Geometry object with the coordinates with the original scale.
    **/
    public Geometry getGeometryInPanelOriginal(boolean isSource){
        GeometryFactory gf = new GeometryFactory();
        GeometryObservation g = getGeometryObservationInPanelOriginal(this.currentObservationNumber);
        if (isSource){
            return gf.createPolygon(g.getSource().toArray(new Coordinate[g.getSource().size()]));
        }
        else{
            //target
            return gf.createPolygon(g.getTarget().toArray(new Coordinate[g.getTarget().size()]));
        }
    }
    
    //returns an array with the wkt of the corr geometries in both panels (with original coords from the corr file, wihtout
    //correction to fit the screen)
    //index 0 contains the wkt string of the corr geometry in the left (first) panel
    //index 1 contains the wkt string of the corr geometry in the right (second) panel
    public String[] getOriginalWKTFromGeometriesInPanels(){
        //get geometry in the left panel (source)
        String wkt1 = getGeometryInPanelOriginal(true).toText();
        
        //get geometry in the right panel (target)
        String wkt2 = getGeometryInPanelOriginal(false).toText();
        
        return new String[] {wkt1, wkt2};
    }
    
    //returns an array with the wkt of the corr geometries in both panels (with corrected coords to fit the screen)
    //index 0 contains the wkt string of the corr geometry in the left (first) panel
    //index 1 contains the wkt string of the corr geometry in the right (second) panel
    public String[] getWKTextFromGeometriesInPanelsScreenCoordinates(){
        //get geometry in the left panel
        Geometry g1 = getGeometryInPanel(false);
        String wkt1 = GeometryEditModel.getText(g1, GeometryType.WELLKNOWNTEXT);
        
        //get geometry in the right panel
        Geometry g2 = getGeometryInPanel(true);
        String wkt2 = GeometryEditModel.getText(g2, GeometryType.WELLKNOWNTEXT);
        
        return new String[] {wkt1, wkt2};
    }
    
    public Geometry WktToGeometry(String wkt){
        WKTReader reader = new WKTReader();
        Geometry g = null;
        try {
            g = reader.read(wkt);
        } catch (ParseException ex) {
            Logger.getLogger(AppCorrGeometries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return g;
    }
    
    public void setCorrGeometry(List<Coordinate> corrGeometry, boolean isSecondPanel) {
        this.updateInfoGeometriesOriginalScale(corrGeometry, !isSecondPanel, false);
        this.updateInfoGeometries(!isSecondPanel, true);
    }
    
    public void setFrame(SPTDataLabBuilderFrame frame){
        this.frame = frame;
    }
    
    public SPTDataLabBuilderFrame getFrame() {
        return frame;
    }
    
    public List<String> getGeoDates() {
        return geoDates;
    }
    
    public File getCurrentCorrFile(){
        return this.fileObservations.get(this.currentObservationNumber);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //monitor when the observation changes
        switch(evt.getPropertyName()){
            case AppConstants.IMAGE_LEFT_PANEL_PROPERTY:
                //images changed, and new geometries will be loaded.
                //save the 
                currentObservationNumber = Integer.parseInt(evt.getNewValue().toString());//current file index with coordinates is equal to current image index in the left panel
                break;
        }
    }
    
    public int getNumberOfEditedGeometries(){
        int count = 0;
        for (boolean b : observationsEdited.values()){
            if ( b == true )
                count++;
        }
        return count;
    }
    
    public boolean saveAllGeometriesAs(String directory, String extension){
        boolean success = false;
        for (int i = 0; i < geometriesInPanel.size(); i++){
            //i-1 -> source, i -> target
            if ( observationsEdited.get(i) == true){//save only edited observations (if either source or target is edited, the observation is saved)
                File f = new File(directory + File.separator+"img_"+(i)+"_"+(i+1) + extension);
                success = saveGeometryToFile(f, getOriginalScaleSource(i), getOriginalScaleTarget(i));
                if (!success)
                    return false;
                observationsEdited.put(i, false);//saved, no longer edited
            }
        }
        if(success)
            isEdited = false;
        return success;
    }
    
    public boolean saveAllGeometries(){
        boolean success = false;
        for (int i = 0; i < geometriesInPanel.size(); i++){
            //i-1 -> source, i -> target
            if ( observationsEdited.get(i) == true){ //save only edited observations (if either source or target is edited, the observation is saved)
                File f = this.getObservationFile(this.currentObservationNumber);
                if (f != null){
                    success = saveGeometryToFile(f, getOriginalScaleSource(i), getOriginalScaleTarget(i));
                    if (!success)
                        return false;
                    observationsEdited.put(i, false);//saved, no longer edited
                }
            }
        }
        if(success)
            isEdited = false;
        return success;
    }
    
    //saves the geometries in both panels in the current corr file
    public boolean saveGeometryToFile(){
        return saveGeometryToFile(this.getCurrentCorrFile());
    }
    
    //saves the geometries in both panels in a file specified by the user
    public boolean saveGeometryToFile(File file){
        boolean success = saveGeometryToFile(file, getOriginalScaleSource(currentObservationNumber), getOriginalScaleTarget(currentObservationNumber));
        
        if (success){
            //mark the current observation as not edited, because it was saved
            observationsEdited.put(this.currentObservationNumber, false);//saved, no longer edited
            if (!observationsEdited())
                isEdited = false;
        }
        return success;
    }
    
    /**
     * Saves the list of coordinates source and target (with original scale) int he given file
     *
     * @param file - file to save coordinates
     * @param sourceToSave - source coordinates list (original scale) to save
     * @param targetToSave - target coordinates list (original scale) to save
     * @return  **/
    public boolean saveGeometryToFile(File file, List<Coordinate> sourceToSave, List<Coordinate> targetToSave){
        GeometryWriter geoWriter = new GeometryWriter(file);
        return geoWriter.writeGeometriesToFile(sourceToSave, targetToSave);
    }
    
    /**
     * Original Image dimension changes inside the panel, therefore, the coordinates of the geometries need to be transformed
     * to be aligned with the image.This method returns the original coordinates before tranformation according to 
 a specified panel dimension.
     * @param coordinates
     * @param editPanel
     * @param imageWidthInPanel - image width inside the panel
     * @param imageHeightInPanel - image height inside the panel
     * @return 
     */
    public List<Coordinate> transformToOriginalCoordinates(List<Coordinate> coordinates, GeometryEditPanel editPanel,
                                                            double imageWidthInPanel, double imageHeightInPanel){
        List<Coordinate> transformedCoords = new ArrayList<>();
        AppImage appImage = AppImage.getInstance();
        CoordinateUtils coordUtils;
        for (Coordinate c : coordinates){
            coordUtils = new CoordinateUtils(c.getX(), c.getY() );
            
            coordUtils.transformOriginal(appImage.getImageOriginalWidth(editPanel.isSecondPanel()),
                    appImage.getImageOriginalHeight(editPanel.isSecondPanel()), imageWidthInPanel, imageHeightInPanel);
            
            transformedCoords.add(coordUtils);
        }
        return transformedCoords;
    }
    
    public CoordinateUtils transformPanelToOriginalCoordinates(Double x, Double y, GeometryEditPanel editPanel,
                                                            double imageWidthInPanel, double imageHeightInPanel){
        AppImage appImage = AppImage.getInstance();
        CoordinateUtils coordUtils;
        coordUtils = new CoordinateUtils(x, y);
        coordUtils.transformOriginalNoChangeAxis(appImage.getImageOriginalWidth(editPanel.isSecondPanel()),
                    appImage.getImageOriginalHeight(editPanel.isSecondPanel()), imageWidthInPanel, imageHeightInPanel);
        return coordUtils;
    }
    
    /**
     * Original Image dimension changes inside the panel, therefore, the coordinates of the geometries need to be transformed
     * to be aligned with the image. This method returns the original coordinates before tranformation according to 
     * CURRENT panel dimension
     * @param coordinates
     * @param editPanel
     * @return 
     */
    public List<Coordinate> transformToOriginalCoordinates(List<Coordinate> coordinates, GeometryEditPanel editPanel){
        AppImage appImage = AppImage.getInstance();
        double currImageWidthInPanel = appImage.getImageWidthInPanel(editPanel.isSecondPanel());
        double currImageHeightInPanel = appImage.getImageHeightInPanel(editPanel.isSecondPanel());
        return transformToOriginalCoordinates(coordinates, editPanel, currImageWidthInPanel, currImageHeightInPanel);
    }
    
    public CoordinateUtils transformPanelToOriginalCoordinates(Double x, Double y, GeometryEditPanel editPanel){
        AppImage appImage = AppImage.getInstance();
        double currImageWidthInPanel = appImage.getImageWidthInPanel(editPanel.isSecondPanel());
        double currImageHeightInPanel = appImage.getImageHeightInPanel(editPanel.isSecondPanel());
        return transformPanelToOriginalCoordinates(x, y, editPanel, currImageWidthInPanel, currImageHeightInPanel);
    }
    /**
     * Original Image dimension changes inside the panel, therefore, the coordinates of the geometries need to be transformed
     * to be aligned with the image. This method returns the original coordinates before tranformation according to 
     * CURRENT panel dimension
     * @param coordinates
     * @param editPanel
     * @return 
     */
    public List<Coordinate> transformToOriginalCoordinates(List<Coordinate> coordinates, boolean isSource){
        if (isSource)
            return transformToOriginalCoordinates(coordinates, SPTDataLabBuilderFrame.getGeometryEditPanel());
        return transformToOriginalCoordinates(coordinates, SPTDataLabBuilderFrame.getGeometryEditPanel2());
    }
    
    /**
     * Return true if any changes was made to ANY geometry in the dataset (change point, add/remove point, drawing
     * @return 
     */
    public boolean changesMade(){
        return isEdited;
    }
    
    //returns true if there is any observations edited, false otherwise
    public boolean observationsEdited(){
        for (boolean edited : this.observationsEdited.values()){
            if (edited)
                return true;
        }
        return false;
    }
    
}
