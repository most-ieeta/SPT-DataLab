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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import ua.ieeta.sptdatalab.util.CSVUtils;
import ua.ieeta.sptdatalab.util.ChartMaker;
import ua.ieeta.sptdatalab.util.ChartType;
import ua.ieeta.sptdatalab.morphing.MetricsEnum;
import ua.ieeta.sptdatalab.util.ScreenImage;
import ua.ieeta.sptdatalab.morphing.SimilarityMetricsEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.jfree.chart.ChartPanel;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import ua.ieeta.sptdatalab.morphing.InterpolationMethodEnum;
import ua.ieeta.sptdatalab.morphing.Metrics;

/**
 *This frame shows an animation of a geometry throught a period of time. It is possible to pause and play the animation
 * as well as manually animate the geometry, see charts with quality metrics and save the geometry.
 * Contains the panel that handles the animation of the geometry
 */
public class MorphingGeometryViewerFrame extends javax.swing.JFrame {
    
    private MorphingGeometryPanel morphingGeoPanel;
    private static boolean userChangedSlider = true;
    private boolean isPolygon;
    private int beginTime = 1000; //<----- temporary
    private int endTime = 2000; //<-----
    private int numSamples;
    int[] periods;
    
    //statistics information. If used with similarity measures, contains the value of the similarity between interpolation geometry and source during all observations
    private Map<String, Double> statistics;
    //used only for similarity metrics. Contains the value of the similarity between interpolation geometry and target during all observations
    private Map<String, Double> statisticsTarget;
    //flag to identify wich type of quality metrics are selected (Normal statistics such as area and perimiters or similarity measures)
    private boolean isSimilarityMeasure;
    
    private ChartMaker chartMaker;
    private InterpolationMethodEnum morphingMethod;
    
    private MultiPolygon mp;
    private MultiPolygon[] multiPolyList;
    private Polygon[] polyList;
    
    private MorphingGeometryViewerFrame(boolean isPolygon, InterpolationMethodEnum morphingMethod, int[] periods) {
        this.isPolygon = isPolygon;
        this.morphingMethod = morphingMethod;
        this.periods = periods;
        this.numSamples = periods.length;
        this.statistics = new HashMap<>();
        this.statisticsTarget = new HashMap<>();
        chartMaker = new ChartMaker();
        initComponents();
        //do not allow metrics to be calculated if geometry is a mesh of triangules
        this.showStatisticsBtn.setEnabled(isPolygon);
        this.chartTypeComboBox.setEnabled(isPolygon);
        this.metricsComboBox.setEnabled(isPolygon);
        this.saveStatisticsBtn.setEnabled(isPolygon);
        this.exportBtn.setEnabled(isPolygon);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //remove this panel from the list of panels to be updated on the animation
                morphingGeoPanel.removePanel();
            }
        });
    }
    
    /**
     *
     * @param wktGeometry - array with wkt as string with the geometry on the first panel and the wkt of the geometry
     * in the second panel
     * @param isPolygon
     * @param morphingMethod
     * @param numSamples
     * @param mp - the result of the morphing of the geometries as a multipolygon, each polygon in an instant
     */
    public MorphingGeometryViewerFrame(boolean isPolygon, InterpolationMethodEnum morphingMethod,
            int[] periods, MultiPolygon mp) {
        this(isPolygon, morphingMethod, periods);
        this.morphingGeoPanel = new MorphingGeometryPanel(mp,this);
        this.mp = mp;
        startComponents();
        initMorphingPanel();
        //show area evolution line chart
        showStatisticsInChart(metricsComboBox.getSelectedItem().toString(), chartTypeComboBox.getSelectedItem().toString());
    }
    
    /**
     * @param wktGeometry - array with wkt as string with the geometry on the first panel and the wkt of the geometry
     * in the second panel
     * @param geometryList - the result of the morphing of the geometries as a list of multipolygon, or polygon
     * each multipolygon being a mesh of triangles in an instant or a polygon in each instant of time
     */
    public MorphingGeometryViewerFrame(boolean isPolygon, InterpolationMethodEnum morphingMethod, 
            int[] periods, Polygon[] geometryList) {
        this(isPolygon, morphingMethod, periods);
        this.morphingGeoPanel = new MorphingGeometryPanel(geometryList, this);
        this.polyList = geometryList;
        startComponents();
        initMorphingPanel();
        //show area evolution line chart
        showStatisticsInChart(metricsComboBox.getSelectedItem().toString(), chartTypeComboBox.getSelectedItem().toString());
    }
    
    public MorphingGeometryViewerFrame(boolean isPolygon, InterpolationMethodEnum morphingMethod, 
            int[] periods, MultiPolygon[] geometryList) {
        this(isPolygon, morphingMethod, periods);
        this.morphingGeoPanel = new MorphingGeometryPanel(geometryList, this);
        this.multiPolyList = geometryList;
        startComponents();
        initMorphingPanel();
        //show area evolution line chart
        showStatisticsInChart(metricsComboBox.getSelectedItem().toString(), chartTypeComboBox.getSelectedItem().toString());
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

        chartPanel = new javax.swing.JPanel();
        morphingFormPanel = new javax.swing.JPanel();
        playBtn = new javax.swing.JButton();
        pauseBtn = new javax.swing.JButton();
        timeSlider = new javax.swing.JSlider();
        saveCurrentGeometryBtn = new javax.swing.JButton();
        saveAnimationBtn = new javax.swing.JButton();
        chartTypeLabel = new javax.swing.JLabel();
        chartTypeComboBox = new javax.swing.JComboBox<>();
        metricsLabel = new javax.swing.JLabel();
        metricsComboBox = new javax.swing.JComboBox<>();
        exportBtn = new javax.swing.JButton();
        saveStatisticsBtn = new javax.swing.JButton();
        showStatisticsBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        interpolationNameLabel = new javax.swing.JLabel();
        currentInstantInfoLabel = new javax.swing.JLabel();
        currentInstantLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        chartPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 30.0;
        gridBagConstraints.weighty = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 15);
        getContentPane().add(chartPanel, gridBagConstraints);

        morphingFormPanel.setLayout(new java.awt.GridBagLayout());

        playBtn.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        morphingFormPanel.add(playBtn, gridBagConstraints);

        pauseBtn.setText("jButton2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        morphingFormPanel.add(pauseBtn, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        morphingFormPanel.add(timeSlider, gridBagConstraints);

        saveCurrentGeometryBtn.setText("jButton3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        morphingFormPanel.add(saveCurrentGeometryBtn, gridBagConstraints);

        saveAnimationBtn.setText("jButton4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        morphingFormPanel.add(saveAnimationBtn, gridBagConstraints);

        chartTypeLabel.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        morphingFormPanel.add(chartTypeLabel, gridBagConstraints);

        chartTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        morphingFormPanel.add(chartTypeComboBox, gridBagConstraints);

        metricsLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        morphingFormPanel.add(metricsLabel, gridBagConstraints);

        metricsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        morphingFormPanel.add(metricsComboBox, gridBagConstraints);

        exportBtn.setText("jButton2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        morphingFormPanel.add(exportBtn, gridBagConstraints);

        saveStatisticsBtn.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        morphingFormPanel.add(saveStatisticsBtn, gridBagConstraints);

        showStatisticsBtn.setText("jButton3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        morphingFormPanel.add(showStatisticsBtn, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        morphingFormPanel.add(jSeparator1, gridBagConstraints);

        interpolationNameLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        morphingFormPanel.add(interpolationNameLabel, gridBagConstraints);

        currentInstantInfoLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        morphingFormPanel.add(currentInstantInfoLabel, gridBagConstraints);

        currentInstantLabel.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        morphingFormPanel.add(currentInstantLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(morphingFormPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void initMorphingPanel(){
        //remove any previous panels
        getContentPane().remove(morphingGeoPanel);
        //add here the panel
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;///<--- increase or decrease the size of this acording to the number of columns the top buttons take
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        gridBagConstraints.weightx = 4.0; 
        gridBagConstraints.weighty = 10.0;
        try{
            getContentPane().add(morphingGeoPanel, gridBagConstraints);
        } catch (NullPointerException ex){ }
        
    }
    
    //called by the geometry animation panel to update the current observation in the slider
    public void updateSlider(int n){
        if (n >= numSamples)
            return;
        userChangedSlider = false;
        timeSlider.setValue(n);
        userChangedSlider = true;
    }
    
    /** Add text, values and listeners to the components of this panel.
     *
     */
    private void startComponents(){
        //initialize buttons
        this.playBtn.setText(AppStrings.PLAY_STRING);
        this.pauseBtn.setText(AppStrings.PAUSE_STRING);
        this.exportBtn.setText(AppStrings.EXPORT_QUALITY_MEASURES_STRING);
        this.saveStatisticsBtn.setText(AppStrings.SAVE_CURRENT_STATISTICS_STRING);
        this.saveCurrentGeometryBtn.setText(AppStrings.SAVE_CURRENT_GEOMETRY_STRING);
        this.saveAnimationBtn.setText(AppStrings.SAVE_ANIMATION_STRING);
        //this.saveAsGifButton.setText(AppStrings.SAVE_ANIMATION_GIF_STRING);
        this.showStatisticsBtn.setText(AppStrings.SHOW_STATISTIC_STRING);
        
        //initialize labels
        this.chartTypeLabel.setText(AppStrings.CHART_TYPE_LABEL_STRING);
        this.metricsLabel.setText(AppStrings.STATISTIC_LABEL_STRING);
        this.interpolationNameLabel.setText(this.morphingMethod.toString() +" - " + this.numSamples + " samples");
        this.currentInstantInfoLabel.setText(AppStrings.CURRENT_INSTANT_LABEL_STRING);
        
        //initialize combo box
        this.metricsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(ArrayUtils.addAll(MetricsEnum.getStatStringList(), SimilarityMetricsEnum.getStatStringList())));
        this.chartTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(ChartType.getChartTypeList()));
        
        playBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                morphingGeoPanel.playAll();
            }
        });
        
        pauseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                morphingGeoPanel.pauseAll();
            }
        });
        
        this.exportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                //save data in the chart as CSV
                if(statistics != null){
                    Map<String, List<String>> dataset = new HashMap<>();
                    int status;
                    if (isSimilarityMeasure){
                        //consider source comparison and target comparison
                        for (Map.Entry<String, Double> entry : statistics.entrySet()) {
                            List l = new ArrayList<>();
                            l.add(entry.getValue());
                            l.add(statisticsTarget.get(entry.getKey()));
                            dataset.put(entry.getKey(), l);
                        }
                        status = CSVUtils.exportAndSaveToCSV(dataset);
                    }
                    else{
                        status = CSVUtils.exportAndSaveToCSV(statistics);
                    }
                    if (status == 0){
                        //success
                        JOptionPane.showMessageDialog(new JFrame(), "File save succesfully");
                    }
                    else if (status == 1){
                        //error
                        JOptionPane.showMessageDialog(new JFrame(), "Error while saving file", "Error",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        saveCurrentGeometryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                //capture the panel with the current geometry displayed and save as image
                ScreenImage.printAndSaveAsImage(morphingGeoPanel);
            }
        });
        
        saveAnimationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create images for every frame and save in user created folder;
                JDialog dialog = createWaitDialog();
                dialog.setVisible(true);
                morphingGeoPanel.saveAnimationToImages(false);
                dialog.setVisible(false);
                //get list of all images corresponding to every frame of the animation
                /*List<BufferedImage> imagesFromAnimation = morphingGeoPanel.generateImagesFromAnimation(10);
                GifSequenceWriter.createGIFAndSave(imagesFromAnimation);*/
            }
        });
        
        /*saveAsGifButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create images for every frame and save in user created folder;
                JDialog dialog = createWaitDialog();
                dialog.setVisible(true);
                morphingGeoPanel.saveAnimationToImages(true);
                dialog.setVisible(false);
            }
        });*/
        
        
        saveStatisticsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                //capture the panel with the chart or table and save as image
                ScreenImage.printAndSaveAsImage(chartPanel);
            }
        });
        
        showStatisticsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatisticsInChart(metricsComboBox.getSelectedItem().toString(), chartTypeComboBox.getSelectedItem().toString());
            }
        });
        //the slider's range will be from the begin time to begin time + number of samples
        timeSlider.setModel(new DefaultBoundedRangeModel(0, 0, 0, numSamples-1));
        //static labels for the slider (max min, middle, middle right, middle left values)
        Hashtable labelTable = new Hashtable();
        if (periods.length > 10){
            //too many values to add for jSlider label. Add only 5 (first, last, middle, middle-first, middle-last
            labelTable.put(  0 , new JLabel(periods[0]+"") );
            labelTable.put(  numSamples-1 , new JLabel(periods[numSamples-1]+"") );
            int c = (int) Math.round(numSamples/2);
            labelTable.put(  c , new JLabel(periods[c]+"") );
            c = (int) Math.round(numSamples/3);
            labelTable.put(  c , new JLabel(periods[c]+"") );
            c = numSamples - c;
            labelTable.put(  c , new JLabel(periods[c]+"") );
        }
        else{
            for (int i = 0; i < periods.length; i++){
                labelTable.put(  i , new JLabel(periods[i]+"") );
            }
        }
        timeSlider.setLabelTable(labelTable);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        
        
        timeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (userChangedSlider){
                    morphingGeoPanel.paintAllAtInstant(timeSlider.getValue());
                }
                if (timeSlider.getValue() < numSamples)
                    currentInstantLabel.setText(periods[timeSlider.getValue()]+"");//update current instant label
            }
        });
    }
    
    private void showStatisticsInChart(String statisticStr, String chart){
        //dont calculate quality measures for a mesh of triangules
        if (!isPolygon){
            //special case
            return;
        }
        //clear any other element in the panel that shows the charts
        chartPanel.removeAll();
       
        Metrics metrics = new Metrics();
        //call the method from the C++ library
        String headerLegend = "";
        String xAxisLegend = "";
        String yAxisLegend = "";
        boolean keysAreNumbers = false; //flag to check if sort by key is needed to display data in the charts
        String[] geometries;
        //check wich type of geometry is currently selected
        if (mp == null && multiPolyList == null && this.polyList.length > 0){
            geometries = new String[polyList.length];
            for (int i = 0; i < polyList.length; i++){
                geometries[i] = polyList[i].toText();
            }
        }
        else if (mp == null && polyList == null && this.multiPolyList.length > 0){
            geometries = new String[multiPolyList.length];
            for (int i = 0; i < multiPolyList.length; i++){
                geometries[i] = multiPolyList[i].toText();
            }
        }
        else{
            geometries = new String[mp.getNumGeometries()];
            for (int i = 0; i < mp.getNumGeometries(); i++){
                geometries[i] = mp.getGeometryN(i).toText();
            }
        }
        String metric;
        
        try{
            metric = MetricsEnum.valueOf(statisticStr).toString();
        } catch (IllegalArgumentException e) {
            //the selected metric is not a statistic metric, and must be a similarity metric
            metric = SimilarityMetricsEnum.valueOf(statisticStr).toString();
        }
        if(metric.equals(MetricsEnum.Area.toString())){
            statistics = metrics.computeMetricForMultipleObservations(geometries, MetricsEnum.Area);

            headerLegend = "Evolution of the Area";
            yAxisLegend = "Observations";
            xAxisLegend = "Area (Dimensionless)";
            keysAreNumbers = true;
            this.isSimilarityMeasure = false;
        }
        else if(metric.equals(MetricsEnum.Perimeter.toString())){
            statistics = metrics.computeMetricForMultipleObservations(geometries, MetricsEnum.Perimeter);
            headerLegend = "Evolution of the Perimeter";
            yAxisLegend = "Observations";
            xAxisLegend = "Perimeter (Dimensionless)";
            keysAreNumbers = true;
            this.isSimilarityMeasure = false;
        }
        else if(metric.equals(SimilarityMetricsEnum.Jaccard_Index.toString())){
            //compare with source
            statistics = metrics.computeSimilarityMetricForMultipleObservations(geometries, geometries[0],
                    SimilarityMetricsEnum.Jaccard_Index );
            //compare with target
            statisticsTarget = metrics.computeSimilarityMetricForMultipleObservations(geometries, geometries[geometries.length-1],
                    SimilarityMetricsEnum.Jaccard_Index );
            headerLegend = "Evolution of the Jaccard Index";
            yAxisLegend = "Observations";
            xAxisLegend = "%";
            keysAreNumbers = true;
            this.isSimilarityMeasure = true;
        }
        else if(metric.equals(SimilarityMetricsEnum.Hausdorff_Distance.toString())){
                //compare with source
            statistics = metrics.computeSimilarityMetricForMultipleObservations(geometries, geometries[0],
                    SimilarityMetricsEnum.Hausdorff_Distance );
            //compare with target
            statisticsTarget = metrics.computeSimilarityMetricForMultipleObservations(geometries, geometries[geometries.length-1],
                    SimilarityMetricsEnum.Hausdorff_Distance );
                
            headerLegend = "Evolution of the Hausdorff Distance";
            yAxisLegend = "Observations";
            xAxisLegend = "Hausdorff Distance (Dimensionless)";
            keysAreNumbers = true;
            this.isSimilarityMeasure = true;
        }            
        
        /*for (Map.Entry<String, Double> entry : statistics.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();
            System.out.println("stats -> "+key+", "+value);
        }*/
        //call apropriate function to create selected chart type
        ChartPanel cp = null;
        if(chart.equals(ChartType.LINE_CHART.getValue())){
            if (isSimilarityMeasure)
                cp = chartMaker.createLineChart(statistics, statisticsTarget, headerLegend, xAxisLegend, yAxisLegend);
            else
                cp = chartMaker.createLineChart(statistics, headerLegend, xAxisLegend, yAxisLegend);
        }
        else if (chart.equals(ChartType.TABLE.getValue())){
            JTable table = null;
            if (isSimilarityMeasure)
                table = chartMaker.createJTable(statistics, statisticsTarget, xAxisLegend, yAxisLegend, keysAreNumbers);
            else
                table = chartMaker.createJTable(statistics, yAxisLegend, xAxisLegend, keysAreNumbers);
            this.chartPanel.add(new JScrollPane(table), BorderLayout.CENTER);
            chartPanel.validate();
            return;
        }
        
        //add the panel to the frame
        this.chartPanel.add(cp, BorderLayout.CENTER);
        chartPanel.validate();
    }
    
    private JDialog createWaitDialog(){
        JDialog dialog = new JDialog();
        JLabel label = new JLabel("Please wait.\nSaving animation in images.\nThis process can take a while if the number of samples is too big.");
        dialog.setLocationRelativeTo(null);
        dialog.setTitle("Please Wait...");
        dialog.add(label);
        dialog.pack();
        return dialog;
    }

    public InterpolationMethodEnum getMorphingMethod() {
        return morphingMethod;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    private javax.swing.JComboBox<String> chartTypeComboBox;
    private javax.swing.JLabel chartTypeLabel;
    private javax.swing.JLabel currentInstantInfoLabel;
    private javax.swing.JLabel currentInstantLabel;
    private javax.swing.JButton exportBtn;
    private javax.swing.JLabel interpolationNameLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox<String> metricsComboBox;
    private javax.swing.JLabel metricsLabel;
    private javax.swing.JPanel morphingFormPanel;
    private javax.swing.JButton pauseBtn;
    private javax.swing.JButton playBtn;
    private javax.swing.JButton saveAnimationBtn;
    private javax.swing.JButton saveCurrentGeometryBtn;
    private javax.swing.JButton saveStatisticsBtn;
    private javax.swing.JButton showStatisticsBtn;
    private javax.swing.JSlider timeSlider;
    // End of variables declaration//GEN-END:variables
}
