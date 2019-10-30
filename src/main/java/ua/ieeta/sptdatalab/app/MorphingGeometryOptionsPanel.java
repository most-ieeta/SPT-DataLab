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

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import ua.ieeta.sptdatalab.morphing.TriangulationMethod;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import static ua.ieeta.sptdatalab.app.AppStrings.POLY_STRING;
import ua.ieeta.sptdatalab.morphing.InterpolationMethodEnum;
import ua.ieeta.sptdatalab.morphing.InterpolationMethodFacade;

/**
 * Panel to select type of interpolation method and parameters.
 */
public class MorphingGeometryOptionsPanel extends javax.swing.JPanel{
     
    private int beginTime = 1000;
    private int endTime = 2000;
    private JDialog modalDialog;
    
    public MorphingGeometryOptionsPanel() {
        initComponents();

        //set text for labels
        this.playBtn.setText(AppStrings.START_MORPHING_BTN_STRING);
        this.timeLabel.setText(AppStrings.INSTANT_LABEL_STRING);
        this.meshOrPolygonLabel.setText(AppStrings.GEOMETRY_TYPE_LABEL_STRING);
        this.timeOrInstantLabel.setText(AppStrings.TIME_LABEL_STRING);
        this.colinearThresholdLabel.setText(AppStrings.COLINEAR_THRESHOLD_STRING);
        this.triangulationLabel.setText(AppStrings.TRIANGULATION_LABEL_STRING);
        this.orientationLabel.setText(AppStrings.VERTICE_ORIENTATION_LABEL_STRING);
        this.methodSelection.setText(AppStrings.METHOD_SELECTION_LABEL_STRING);
        this.numSamplesLabel.setText(AppStrings.NUM_SAMPLES_LABEL);
        
        this.initialTimeSpinner.setModel(new SpinnerNumberModel(beginTime+10, beginTime+10, endTime-10, 10));//set default, min max and increment value
        this.numSamplesSpinner.setModel(new SpinnerNumberModel(endTime-beginTime, 3, endTime-beginTime, 1));//set default, min max and increment value
        numSamplesSpinner.setEnabled(false); //initially inactive
        
        this.colinearThresholdSpinner.setModel(new SpinnerNumberModel(0.5, 0, 1, 0.1));
        
        //set the text for the combo boxes
        this.instantOrPeriodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(AppStrings.INSTANT_OR_PERIOD_STRINGS));
        this.meshOrPolygonComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(AppStrings.MESH_OR_POLY_STRINGS));
        this.triangulationMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(AppStrings.TRIANGULATION_METHOD_STRINGS));
        this.verticeOrientationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(AppStrings.VERTICE_ORIENTATION_STRINGS));
        this.methodSelectionComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(AppStrings.MORPHING_METHODS));
        
        this.methodSelectionComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                //if user wants to get the morphing at an instant, hide the spinner for the end time and num samples
                if(methodSelectionComboBox.getSelectedItem().toString().equals(InterpolationMethodEnum.SPTMesh.toString())){
                    //SPTMesh selected. Enable options available only for SPTMesh library
                    meshOrPolygonComboBox.setEnabled(true);
                    triangulationMethodComboBox.setEnabled(true);
                    verticeOrientationComboBox.setEnabled(true);
                    colinearThresholdSpinner.setEnabled(true);
                }
                else{
                    //SPTMesh not selected, disable options available only for SPTMesh library
                    meshOrPolygonComboBox.setSelectedItem(POLY_STRING);
                    meshOrPolygonComboBox.setEnabled(false);
                    triangulationMethodComboBox.setEnabled(false);
                    verticeOrientationComboBox.setEnabled(false);
                    colinearThresholdSpinner.setEnabled(false);
                }
            }
        });
        
        this.instantOrPeriodComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                //if user wants to get the morphing at an instant, hide the spinner for the end time and num samples
                if(instantOrPeriodComboBox.getSelectedItem().toString().equals(AppStrings.AT_INSTANT_METHOD_STRING)){
                    timeLabel.setText(AppStrings.INSTANT_LABEL_STRING);
                    initialTimeSpinner.setEnabled(true);
                    numSamplesSpinner.setEnabled(false);
                }
                else{
                    //if user wants the morphing for period of time show the end time spinner in order for him to select the end period
                    timeLabel.setText(AppStrings.DURING_PERIOD_METHOD_STRING);
                    //enable the spinners for end period and number of samples
                    initialTimeSpinner.setEnabled(false);
                    numSamplesSpinner.setEnabled(true);
                }
            }
        });
    }
       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        playBtn = new javax.swing.JButton();
        timeLabel = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        instantOrPeriodComboBox = new javax.swing.JComboBox<>();
        meshOrPolygonComboBox = new javax.swing.JComboBox<>();
        initialTimeSpinner = new javax.swing.JSpinner();
        meshOrPolygonLabel = new javax.swing.JLabel();
        timeOrInstantLabel = new javax.swing.JLabel();
        triangulationMethodComboBox = new javax.swing.JComboBox<>();
        colinearThresholdLabel = new javax.swing.JLabel();
        colinearThresholdSpinner = new javax.swing.JSpinner();
        verticeOrientationComboBox = new javax.swing.JComboBox<>();
        orientationLabel = new javax.swing.JLabel();
        triangulationLabel = new javax.swing.JLabel();
        methodSelection = new javax.swing.JLabel();
        methodSelectionComboBox = new javax.swing.JComboBox<>();
        numSamplesLabel = new javax.swing.JLabel();
        numSamplesSpinner = new javax.swing.JSpinner();

        setFocusTraversalPolicyProvider(true);
        setLayout(new java.awt.GridBagLayout());

        playBtn.setText("Play");
        playBtn.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                playBtnCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        playBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startMorphing(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(playBtn, gridBagConstraints);

        timeLabel.setText("time");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        add(timeLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        add(filler2, gridBagConstraints);

        instantOrPeriodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(instantOrPeriodComboBox, gridBagConstraints);

        meshOrPolygonComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(meshOrPolygonComboBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(initialTimeSpinner, gridBagConstraints);

        meshOrPolygonLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(meshOrPolygonLabel, gridBagConstraints);

        timeOrInstantLabel.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(timeOrInstantLabel, gridBagConstraints);

        triangulationMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(triangulationMethodComboBox, gridBagConstraints);

        colinearThresholdLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(colinearThresholdLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(colinearThresholdSpinner, gridBagConstraints);

        verticeOrientationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(verticeOrientationComboBox, gridBagConstraints);

        orientationLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(orientationLabel, gridBagConstraints);

        triangulationLabel.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(triangulationLabel, gridBagConstraints);

        methodSelection.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(methodSelection, gridBagConstraints);

        methodSelectionComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(methodSelectionComboBox, gridBagConstraints);

        numSamplesLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        add(numSamplesLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(numSamplesSpinner, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /** Calls the appropriate interpolation methods, parse and validate result and call a new window to visualize the result if
     * during period was selected, or draw in the left panel if at instant was selected
    */
    private void startMorphing(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMorphing
        // get wkt of the corr geometries in both panels.
        String[] wkts;//first element is source, second is target geometry
        wkts = AppCorrGeometries.getInstance().getOriginalWKTFromGeometriesInPanels();
        
        Geometry geom1;        
        Geometry geom2;        
        
        WKTReader reader = new WKTReader();
        try {
            geom1 = reader.read(wkts[0]);
        } catch (ParseException ex) {
             Logger.getLogger(MorphingGeometryOptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
             return;
        }
        if (!geom1.isValid())
        {    
            JOptionPane.showMessageDialog(null, "The first geometry is not valid according to OGC SFS specification. Please correct the geometry.", "Invalid geometry", JOptionPane.OK_OPTION);
            return;
        }
        try {
            geom2 = reader.read(wkts[1]);
        } catch (ParseException ex) {
             Logger.getLogger(MorphingGeometryOptionsPanel.class.getName()).log(Level.SEVERE, null, ex);
             return;
        }
        if (!geom2.isValid())
        {    
            JOptionPane.showMessageDialog(null, "The second geometry is not valid according to OGC SFS specification. Please correct the geometry.", "Invalid geometry", JOptionPane.OK_OPTION);
            return;
        }
        
        String[] result = new String[1];
        
        //get user selections (method and period of observations )
        String selectedTime = this.instantOrPeriodComboBox.getSelectedItem().toString();
        String selectedGeomType = this.meshOrPolygonComboBox.getSelectedItem().toString();
        boolean duringPeriod = false;
        boolean isPolygon = false;
        int[] periods = new int[0];
        
        //obtain user inserted data from combo boxes and other fields
        int triangulationMethod = TriangulationMethod.valueOf(triangulationMethodComboBox.getSelectedItem().toString()).get_value();
         
        boolean cw = verticeOrientationComboBox.getSelectedItem().toString().equals(AppStrings.CLOCK_WISE_STRING);
        
        double selectedInstant = Double.parseDouble(initialTimeSpinner.getValue().toString());
        double threshold = Double.parseDouble(colinearThresholdSpinner.getValue().toString());
        InterpolationMethodEnum morphingMethod = InterpolationMethodEnum.valueOf(this.methodSelectionComboBox.getSelectedItem().toString());
        int numSamples = -1;
        
        
        if ((morphingMethod == InterpolationMethodEnum.SPTMesh) && (!(geom1.getCoordinates().length == geom2.getCoordinates().length)))
        {    
            JOptionPane.showMessageDialog(null, "The SPTMesh method requires the geometries to be compatible, but source and target geometries does not have the same number of points. Please correct the geometries and try again.", "Invalid geometries", JOptionPane.OK_OPTION);
            return;
        }
       
        
        //initiate interpolation facade with parameters common to all diferent interpolation methods
        InterpolationMethodFacade interpolation = new InterpolationMethodFacade(wkts[0], wkts[1], beginTime, endTime);
        /*System.out.println(AppCorrGeometries.getInstance().getCurrentSource().size()+", "+wkts[0]);
        System.out.println(AppCorrGeometries.getInstance().getCurrentTarget().size()+", "+wkts[1]);
        System.out.println("Collinear exists in source: "+ AppCorrGeometries.getInstance().collinearExists(AppCorrGeometries.getInstance().getCurrentSource()));
        System.out.println("Collinear exists in target: "+ AppCorrGeometries.getInstance().collinearExists(AppCorrGeometries.getInstance().getCurrentTarget()));
           */     
        
        //Beginning of interpolation, show wait dialog
        this.openWaitingDialog();
        //at instant
        if (selectedTime.equals(AppStrings.AT_INSTANT_METHOD_STRING)){
            duringPeriod = false;
            periods = new int[3];//there will only be 3 instants: source, selected instant, target
            periods[0] = 1000;
            periods[1] = (int) Math.round(selectedInstant);
            periods[2] = 2000;
            //polygon, at instant
            if(selectedGeomType.equals(AppStrings.POLY_STRING)){
                isPolygon = true;
                //call interpolation facade to select appropriate interpolation method
                result[0] = interpolation.interpolationAtInstant(selectedInstant, morphingMethod, triangulationMethod, cw, threshold);
            }
            //mesh, at instant
            else if(selectedGeomType.equals(AppStrings.MESH_STRING)){
                isPolygon = false;
                result = new String[3];
                //We do 3 interpolations to get the source and target geometry representation as a mesh
                result[0] = interpolation.interpolationAtInstantMesh(beginTime, morphingMethod, triangulationMethod, cw, threshold);
                result[1] = interpolation.interpolationAtInstantMesh(selectedInstant, morphingMethod, triangulationMethod, cw, threshold);
                result[2] = interpolation.interpolationAtInstantMesh(endTime, morphingMethod, triangulationMethod, cw, threshold);
            }
        }
        //during period
        else if (selectedTime.equals(AppStrings.DURING_PERIOD_METHOD_STRING)){
            duringPeriod = true;
            numSamples = Integer.parseInt(numSamplesSpinner.getValue().toString());
            periods = new int[numSamples];
            int nSamples = numSamples - 1;
            int dt = endTime - beginTime;
            for (int i = 0; i < numSamples; i++){
                double instant = ( (double) i / (double) nSamples) * dt + beginTime;
                periods[i] = (int) Math.round(instant);
            }
            
            if(selectedGeomType.equals(AppStrings.POLY_STRING)){
                //poly, during period
                isPolygon = true;
                result = interpolation.interpolationDuringPeriod(selectedInstant, endTime, numSamples, morphingMethod, triangulationMethod, cw, threshold);
            }
            else if(selectedGeomType.equals(AppStrings.MESH_STRING)){
                //mesh, during period
                isPolygon = false;
                result = interpolation.interpolationDuringPeriodMesh(selectedInstant, endTime, numSamples, morphingMethod, triangulationMethod, cw, threshold);
            }
        }
        //end of interpolation, close wait dialog
        this.closeWaitingDialog();
        if (result == null || result.length == 0){
            //an error happened during interpolation, cancel and inform user
            JOptionPane.showMessageDialog(null, "It was not possible to apply the interpolation method. \nCheck input parameters and try again.",
            "Interpolation failed",
            JOptionPane.WARNING_MESSAGE);
        }
        String res = Arrays.toString(result);
        if (!res.contains(AppStrings.MORPHING_ERR_STRING)){
            //morphing was succesfull
            //draw the result of the morphing geometry in the left panel (1st panel) if is at instant
            // open new window with animation of morphing if is 2nd 
            drawAndShowMorphingGeometry(result, duringPeriod, isPolygon, periods, morphingMethod);
        }
        else{
            //open error dialog box
            JOptionPane.showMessageDialog(new JFrame(), "An error occurred during morphing operation.",
            "Error on Morphing", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_startMorphing

    private void playBtnCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_playBtnCaretPositionChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_playBtnCaretPositionChanged

    //draw in the left panel (1st panel) the result of the morphing of a geometry if at instant
    //or open a new window with full animation of the morphing during period
    //This function receives the wkt of the resulting morph and costructs a geometry or list of geometries
    //according to the options selected by the user
    public void drawAndShowMorphingGeometry(String[] wktGeometryInterpolated, boolean duringPeriod, boolean isPolygon, 
        int[] periods, InterpolationMethodEnum morphingMethod){
        WKTReader reader = new WKTReader();
        //set of geometries for each instant, showed in a new window
        if (duringPeriod){
            //several polygons for a certain interval of time

            if(wktGeometryInterpolated.length == 1){
                //a multipolygon, each polygon representing one instant
                MultiPolygon mPolygon = null;

                try {
                    //array has length 1, with the multipolygon
                    mPolygon = (MultiPolygon) reader.read(wktGeometryInterpolated[0]);        
                }catch(Exception e) {   }
                MorphingGeometryViewerFrame mframe = new MorphingGeometryViewerFrame(isPolygon, morphingMethod, periods, mPolygon);
                openMorphingGeometryFrame(mframe);
            }
            else{
                
                if(isPolygon){
                    //a list of polygons, each representing one instant of time
                    Polygon[] pList = new Polygon[wktGeometryInterpolated.length];
                    for (int i = 0; i < wktGeometryInterpolated.length; i++){
                        try { 
                            Polygon p = (Polygon) reader.read(wktGeometryInterpolated[i]);
                            pList[i] = p;
                        } catch (ParseException ex) {
                            Logger.getLogger(AppCorrGeometries.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                    MorphingGeometryViewerFrame mframe = new MorphingGeometryViewerFrame(isPolygon, morphingMethod, periods, pList);
                    openMorphingGeometryFrame(mframe);
                }
                else{
                    //a list of multipolygons, each multypoligon representing a mesh of triangules in a period of time
                    MultiPolygon[] mpList = new MultiPolygon[wktGeometryInterpolated.length];
                    for (int i = 0; i < wktGeometryInterpolated.length; i++){
                        try { 
                            MultiPolygon mp = (MultiPolygon) reader.read(wktGeometryInterpolated[i]);
                            mpList[i] = mp;
                        } catch (ParseException ex) {
                            Logger.getLogger(AppCorrGeometries.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    MorphingGeometryViewerFrame mframe = new MorphingGeometryViewerFrame(isPolygon, morphingMethod, periods, mpList);
                    openMorphingGeometryFrame(mframe);
                }
            }
        }
        //at instant
        else {
            //one polygon or a multipolygon of triangules for one instant
            if (isPolygon){
                //a polygon representation
                try {
                    Polygon[] pList = new Polygon[3];//will have source, interpolation result at instant, target
                    String[] wktsSourceTarget = AppCorrGeometries.getInstance().getOriginalWKTFromGeometriesInPanels();
                    pList[0] = (Polygon) reader.read(wktsSourceTarget[0]);//source
                    pList[1] = (Polygon) reader.read(wktGeometryInterpolated[0]);//interpolation result
                    pList[2] = (Polygon) reader.read(wktsSourceTarget[1]);//target
                   
                    MorphingGeometryViewerFrame mframe = new MorphingGeometryViewerFrame(isPolygon, morphingMethod, periods, pList);
                    openMorphingGeometryFrame(mframe);
                } catch (ParseException ex) {
                    Logger.getLogger(AppCorrGeometries.class.getName()).log(Level.SEVERE, null, ex);
                }           
            }
            else{
                //a multipolygon, with meshes of triangules
                
                try {
                    MultiPolygon[] multiPolygon = new MultiPolygon[3];
                    multiPolygon [0] = (MultiPolygon) reader.read(wktGeometryInterpolated[0]);
                    multiPolygon [1] = (MultiPolygon) reader.read(wktGeometryInterpolated[1]);
                    multiPolygon [2] = (MultiPolygon) reader.read(wktGeometryInterpolated[2]);
                    MorphingGeometryViewerFrame mframe = new MorphingGeometryViewerFrame(isPolygon, morphingMethod, periods, multiPolygon);
                    openMorphingGeometryFrame(mframe);
                } catch (ParseException ex) {
                    Logger.getLogger(AppCorrGeometries.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void openMorphingGeometryFrame(MorphingGeometryViewerFrame frame){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //start frame to show the animation for the morphing geometry
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //dont close entire project on window close
                frame.pack();
                frame.validate();
                frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH); //start frame maximized
                frame.setLocationRelativeTo(null);
                frame.setTitle(AppStrings.MORPHING_PANEL_TITLE + " - " + frame.getMorphingMethod().toString());
                frame.setVisible(true);
            }
        });
    }
    
    private void openWaitingDialog(){
        modalDialog = new JDialog(new JFrame(), "Please wait....", Dialog.ModalityType.APPLICATION_MODAL);
        JLabel label = new JLabel("Please wait.\n Performing interpolation. \nThis process can take a while depending on the complexity of the geometries.");
        modalDialog.setLocationRelativeTo(this);
        //modalDialog.setTitle("Please Wait...");
        modalDialog.add(label);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        modalDialog.setLocation(dim.width/2-modalDialog.getSize().width/2, dim.height/2-modalDialog.getSize().height/2);
        modalDialog.toFront();
        modalDialog.requestFocus();
        modalDialog.setSize(new Dimension(600, 100));
        modalDialog.setVisible(true);
    }
    
    private void closeWaitingDialog(){
        if (modalDialog != null)
            modalDialog.dispose();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel colinearThresholdLabel;
    private javax.swing.JSpinner colinearThresholdSpinner;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JSpinner initialTimeSpinner;
    private javax.swing.JComboBox<String> instantOrPeriodComboBox;
    private javax.swing.JComboBox<String> meshOrPolygonComboBox;
    private javax.swing.JLabel meshOrPolygonLabel;
    private javax.swing.JLabel methodSelection;
    private javax.swing.JComboBox<String> methodSelectionComboBox;
    private javax.swing.JLabel numSamplesLabel;
    private javax.swing.JSpinner numSamplesSpinner;
    private javax.swing.JLabel orientationLabel;
    private javax.swing.JButton playBtn;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JLabel timeOrInstantLabel;
    private javax.swing.JLabel triangulationLabel;
    private javax.swing.JComboBox<String> triangulationMethodComboBox;
    private javax.swing.JComboBox<String> verticeOrientationComboBox;
    // End of variables declaration//GEN-END:variables

}
