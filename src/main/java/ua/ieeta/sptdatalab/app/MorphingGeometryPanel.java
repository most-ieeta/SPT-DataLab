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
package ua.ieeta.sptdatalab.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.geotools.geometry.jts.LiteShape;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import ua.ieeta.sptdatalab.util.GifSequenceWriter;

/**
 *
 * Panel that handles the animation of the geometry.
 */
public class MorphingGeometryPanel extends JPanel{
    private int currentGeometryNumber = 0;
    private int nGeometries = 0;
    private static int biggestNGeometries = 0; //stores the panel with the biggest number of geometries
    private MultiPolygon multiPolygon = null;
    private MultiPolygon[] multiPolygonList;
    private Polygon[] polygonList;
    private Timer timer = null;
    //list of all instances of open panels
    private static List<MorphingGeometryPanel> morphingGeometryPanels = new ArrayList<>();
    private MorphingGeometryViewerFrame viewerFrame;
    private static int callerPanelNGeometries;
    private double stepCounter = 0;
    private int delay;
    private boolean drawAnimation = false;
    

    //for a multipolygon, each polygon in a certain instant
    public MorphingGeometryPanel(MultiPolygon mPolygon, MorphingGeometryViewerFrame viewerFrame){
        multiPolygon = mPolygon;
        morphingGeometryPanels.add(this);
        this.viewerFrame = viewerFrame;
        nGeometries = multiPolygon.getNumGeometries();
        if (nGeometries > biggestNGeometries)
            biggestNGeometries = nGeometries;
        callerPanelNGeometries = nGeometries;
        setDelay();
        super.setBackground(Color.white);
        startAnimation();
    }
     
     //for a list of mesh of triangules
     public MorphingGeometryPanel(MultiPolygon[] geometryList, MorphingGeometryViewerFrame viewerFrame){
        this.multiPolygonList = geometryList;
        morphingGeometryPanels.add(this);
        this.viewerFrame = viewerFrame;
        this.nGeometries = multiPolygonList.length;
        if (nGeometries > biggestNGeometries)
            biggestNGeometries = nGeometries;
        callerPanelNGeometries = nGeometries;
        setDelay();
        super.setBackground(Color.white);
        startAnimation();
    }
     
    //for a list of polygons
    public MorphingGeometryPanel(Polygon[] geometryList, MorphingGeometryViewerFrame viewerFrame){
       this.polygonList = geometryList;
       morphingGeometryPanels.add(this);
       this.viewerFrame = viewerFrame;
       this.nGeometries = polygonList.length;
       if (nGeometries > biggestNGeometries)
            biggestNGeometries = nGeometries;
       callerPanelNGeometries = nGeometries;
       setDelay();
       super.setBackground(Color.white);
       startAnimation();
    }
    
    private void startAnimation(){
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawAnimation = true;
                repaint();
            }
        });
        //slight bug on slider if a 2 or more windows restart timer
        if (morphingGeometryPanels.size() == 1){
            drawAnimation = true;
            timer.start();
        }
        else
            playAllFromGeometry(0);
    }
    
    
    public void pauseAll(){
        for (MorphingGeometryPanel mp : morphingGeometryPanels)
            mp.pause();
    }
    
    public void pause(){
        if(timer.isRunning()){
            timer.stop();
        }
    }
    
    public void playAll(){
        //pause();
        for (MorphingGeometryPanel mp : morphingGeometryPanels)
            mp.play();
    }
    
    //start the animation from the current selected geometry number (or frame)
    public void play(){
        if(!timer.isRunning()){
            timer.start();
        }
    }
    
    public void playAllFromGeometry(int n){
        callerPanelNGeometries = nGeometries;
        //ensure this variable is not incremented for other instances
        this.currentGeometryNumber = n;
        //int index = morphingGeometryPanels.indexOf(this);
        //morphingGeometryPanels.add(0, morphingGeometryPanels.remove(index));
        for (MorphingGeometryPanel mp : morphingGeometryPanels)
            mp.playFromGeometry(n);
    }
    
    public void playFromGeometry(int n){
        this.currentGeometryNumber = n;
        if(!timer.isRunning()){
            timer.start();
        }
    }
    
    public void paintAllAtInstant(int n){
        callerPanelNGeometries = nGeometries;
        int conversion = 0;
        for (MorphingGeometryPanel mp : morphingGeometryPanels){
            conversion = mp.convertSampleNumber(n);
            mp.paintAtInstant(conversion);
        }
    }
    
    public int convertSampleNumber(int n){
        if (morphingGeometryPanels.size() == 1 || this.nGeometries == callerPanelNGeometries)
            return n;
        if (biggestNGeometries != nGeometries){
            //user is changing the slider on the window that is not higgest number of samples. Make a conversion to show 
            //the correspondent sample on this window
            n = (int) Math.round (n * nGeometries / biggestNGeometries);
        }
        else{
            if (callerPanelNGeometries < biggestNGeometries){
                n = (int) Math.round(n * biggestNGeometries / callerPanelNGeometries) ;
            }
        }
        return n;
    }

    public void paintAtInstant(int n){    
        //if animation is running stop it
        pause();
        this.currentGeometryNumber = n;
        drawAnimation = true;
        repaint();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gr = (Graphics2D) g.create();
        gr = drawGeometryFrame(gr, currentGeometryNumber);
        
        //to update the slider in the ui of every interpolation animation windows
        viewerFrame.updateSlider(currentGeometryNumber);
        gr.dispose();
        if (!drawAnimation) //could be called because window was resized, for example. In this case, dont animate
            return;
        incrementGeometryNumber();
        
        if(currentGeometryNumber >= nGeometries) {
            timer.stop();
            //reset geom number
            currentGeometryNumber = 0;
        }
        drawAnimation = false;
    }
    
    private Graphics2D drawGeometryFrame(Graphics2D gr, int geoNumber){
        //draw geometry 
        //Graphics2D gr = (Graphics2D) g;
        gr.setColor(Color.blue);

        MultiPolygon mGeometry = null;
        Polygon pGeometry = null;
        
        AffineTransform at = new AffineTransform();
        //at.translate(100, 400);
        //at.scale(20, -20);
        double panelHeight = this.getHeight();
        double panelWidth = this.getWidth();
        LiteShape lt = null;
        if (multiPolygonList != null && geoNumber < multiPolygonList.length){
            mGeometry = multiPolygonList[geoNumber];
            Point p = mGeometry.getCentroid();
            at.translate(panelWidth/2 - p.getX(), panelHeight/2 - p.getY());//place center of the geometry in the midle of the panel
            
            lt = new LiteShape(mGeometry, at, false);
            
            //check if this geometry is too big for the panel. If so, subtract to every coordinate the diferenc between
            //the size of the shape and the panel
           AffineTransform atScaled =  scaleShapeToFitScreen(lt, p);
            if (atScaled != null)
                lt = new LiteShape(mGeometry, atScaled, false);
            else
                lt = new LiteShape(mGeometry, at, false);
            System.out.println(lt.getGeometry());
            gr.draw(lt);

        }
        else if (polygonList != null && geoNumber < polygonList.length){
            pGeometry = polygonList[geoNumber];
            //pGeometry = AppCorrGeometries.getInstance().makePolygonFitComponent(pGeometry, this);
            Point p = pGeometry.getCentroid();
            at.translate(panelWidth/2 - p.getX(), panelHeight/2 - p.getY());//place center of the geometry in the midle of the panel
            
            //draw on the jpanel
            lt = new LiteShape(pGeometry, at, false);
            AffineTransform atScaled =  scaleShapeToFitScreen(lt, p);
            if (atScaled != null)
                lt = new LiteShape(pGeometry, atScaled, false);
            else
                lt = new LiteShape(pGeometry, at, false);
            gr.fill(lt);
            gr.draw(lt);
            
        }
        return gr;
    }
    
    private AffineTransform scaleShapeToFitScreen(Shape s, Point centerPoint){
        AffineTransform at = new AffineTransform();
        double panelHeight = this.getHeight();
        double panelWidth = this.getWidth();
        int shapeWidth = s.getBounds().getSize().width;
        int shapeHeight = s.getBounds().getSize().height;
        double diferenceWidth = 0;
        double diferenceHeight = 0;
        
        //apply some scale to reduce the size of the shape, until it fits the screen
            /*double scaleDecr = 0.1
            while (diferenceWidth > 0 || diferenceHeight > 0){
                System.out.println("scale: "+scale);
                at = new AffineTransform();
                //translate to the center, considering the scale that is going to be applied
                at.translate( (panelWidth/2) - (p.getX()*(scale)), (panelHeight/2) - (p.getY()*(scale)));
                at.scale(scale, scale);
                //create new shape with the new scale
                lt = new LiteShape(mGeometry, at, false);
                shapeWidth = lt.getBounds().getSize().width;
                shapeHeight = lt.getBounds().getSize().height;
                diferenceWidth = shapeWidth-panelWidth;
                diferenceHeight = shapeHeight - panelHeight;
                scale = scale - scaleDecr;
                if (scale <= 0.1){
                    break;
                }
            }*/
        if (shapeWidth > panelWidth){
            diferenceWidth = shapeWidth-panelWidth;
        }
        if (shapeHeight > panelHeight){
            diferenceHeight = shapeHeight - panelHeight;
        }
        if (diferenceWidth > 0 || diferenceHeight > 0){
            //reduce scale of shape to fit the screen
            double scale = 0.7;
            //translate to the center, considering the scale that is going to be applied
            at.translate( (panelWidth/2) - (centerPoint.getX()*(scale)), (panelHeight/2) - (centerPoint.getY()*(scale)));
            at.scale(scale, scale);
            //create new affine transform with the new scale
            return at;
        }
        return null;
    }
    
    private void saveFramesAsImage(String animationFrameDirSave){
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D ig2 = image.createGraphics();
        ig2.setBackground(Color.white);
        int animationFrameSaveNumber = 0;
        for (int i=0; i < this.nGeometries; i++){
            ig2.clearRect(0, 0, getWidth(), getHeight());
            ig2 = drawGeometryFrame(ig2, i);
            try { 
                ImageIO.write(image, "png", new File(animationFrameDirSave + File.separator + animationFrameSaveNumber + ".png"));
                animationFrameSaveNumber++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void saveAnimationToImages(boolean saveAsGif){
        String saveDir = "animation_images";
        JFrame parentFrame = new JFrame();
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(AppStrings.SAVE_SET_IMAGES_CHOOSER_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            //get selected dir, create new folder to save the images
            String animationFrameDirSave = fileToSave.getAbsolutePath() + File.separator + saveDir;
            new File(animationFrameDirSave).mkdirs();
            saveFramesAsImage(animationFrameDirSave);
            if (saveAsGif){
                //TODO: Make sure images are selected to be used as gifs in alphabetic order! (currently, it is not)
                GifSequenceWriter.createGIFAndSave(new File(animationFrameDirSave).listFiles(), 
                        new File(animationFrameDirSave + File.pathSeparator + "mygif.gif"));
            }
        }
        else {
            JOptionPane.showMessageDialog(new JFrame(),
                    AppStrings.NO_FILE_SELECTED_SET_IMAGE_SAVE, AppStrings.NO_FILE_SELECTED_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    //for every polygon in a multipolygon, changes the coordinates of the polygon to fit the screen
    private MultiPolygon resizeMultiPolygonToFitComponent(MultiPolygon multiPol, double diferenceWidth, double diferentHeight){
        Polygon[] pol = new Polygon[multiPol.getNumGeometries()];
        for (int i = 0; i <  multiPol.getNumGeometries(); i++){
            Polygon p = (Polygon) multiPol.getGeometryN(i);
            Coordinate[] polyCoords = p.getCoordinates();
            Coordinate[] resizedCoords = new Coordinate[polyCoords.length];
            //resize coordinates of this polygon
            for (int j = 0; j < polyCoords.length ; j++){
                Coordinate c = new Coordinate(polyCoords[j].getX()-(diferenceWidth*2), polyCoords[j].getY()-(diferentHeight*2));
                resizedCoords[j] = c;
            }
            //add the resized coordinates and make a new polygon
            pol[i] = new GeometryFactory().createPolygon(resizedCoords);
        }
        //create new multipolygon from the polygons with resized coordinates
        return new GeometryFactory().createMultiPolygon(pol);
    }
    public void removePanel(){
        morphingGeometryPanels.remove(this);
        recalculateBiggerGeometriesNumber();
    }
    
    //if one panel was closed, the highest number of samples in open panels may not be updated.
    public void recalculateBiggerGeometriesNumber(){
        int n = 0;
        for (MorphingGeometryPanel mp : morphingGeometryPanels){
            if (mp.getNGeometries() > n)
                n = mp.getNGeometries();
        }
        biggestNGeometries = n;
    }
    
    public void incrementGeometryNumber(){
        if (this.nGeometries == biggestNGeometries)
            currentGeometryNumber++;
        else{
            double diff = biggestNGeometries/nGeometries;
            stepCounter ++;
            if (stepCounter >= diff){
                currentGeometryNumber++;
                stepCounter = 0;
            }
        }
    }
    
    public int getCurrentGeometryNumber() {
        return currentGeometryNumber;
    }
    
    public int getNGeometries(){
        return this.nGeometries;
    }
    
    private void setDelay(){
        if (nGeometries >= 100){
            delay = 10;
        }
        else if (nGeometries >= 10 && nGeometries < 100) {
            delay = 100;
        }
        else{
            delay = 500;
        }
    }
    
}
