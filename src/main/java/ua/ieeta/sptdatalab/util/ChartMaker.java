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
package ua.ieeta.sptdatalab.util;


import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * Utility class that, given a data set, creates a chart with it.
 */
public class ChartMaker {
    
    /**
     * Create a line chart with the evolution of the area of a geometry through time.
     * @param statistics - map with the statistics over time (area, rotation angles...)
     * @param headerLegend - Title of the chart
     * @param xAxisLegend - x axis legend
     * @param yAxisLegend - y axis legend
     * @return the Chart Panel Swing component with the generated chart
     */
    public ChartPanel createLineChart(Map<String, Double> statistics, String headerLegend, 
                    String xAxisLegend, String yAxisLegend){
        XYSeries series = new XYSeries(headerLegend);
        
        //sort the key (by instant)and convert to int
        SortedSet<Integer> instants = sortMapKeys(statistics);
        /////add the sorted elements to the dataset
        for (Integer instant : instants) { 
            series.add( instant, statistics.get(instant.toString()) );
        }

        //Add series to dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        JFreeChart lineChart = ChartFactory.createXYLineChart(headerLegend, yAxisLegend, xAxisLegend,
                dataset, PlotOrientation.VERTICAL, true,true,false);
        
        // Assign it to the chart
        /*XYPlot plot = (XYPlot) lineChart.getPlot();
        //plot.setDomainAxis(xAxis);
        
        NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
        //xAxis.setTickUnit(new NumberTickUnit( NUMBER_OF_TICKS_X_AXIS ));
        xAxis.setRange(instants.first(), instants.last());
        xAxis.setAutoTickUnitSelection(true);
        //xAxis.setAutoRangeMinimumSize(instants.first());
        xAxis.setAutoRangeIncludesZero(true);
        
        NumberAxis yAxis = (NumberAxis) plot.getDomainAxis();
        //get max min value of area
        //double min = statistics.values().stream().min(Double::compare).get();
        double max = statistics.get(instants.last().toString());
        double min = statistics.get(instants.first().toString());
        
        DecimalFormat newFormat = new DecimalFormat("#.00");
        int offset = getChartOffset((int) max);
        int bottomOffset = offset;
        yAxis.setNumberFormatOverride(newFormat);
        if (min > 0.0){
            min = 0;
            bottomOffset = 0;
        }
        yAxis.setRange(min+bottomOffset, max+offset);
        //yAxis.setAutoRange(true);
        //yAxis.setTickUnit(new NumberTickUnit(statistics.size()/2));*/
        
        return new ChartPanel(lineChart);  
    }
    
    /**
     * Create a line chart with the evolution of the similarity between the interpolation geometry and the source and target
     * geometry. 2 lines are drawn: one with interpolation geometry and source, another with interpolation geometry and target.
     * @param statisticsSource - map with observation and similarity measure at that instant between interpolation geometry and source
     * @param statisticsTarget - map with observation and similarity measure at that instant between interpolation geometry and target
     * @param headerLegend - Title of the chart
     * @param xAxisLegend - x axis legend
     * @param yAxisLegend - y axis legend
     * @return the Chart Panel Swing component with the generated chart
     */
    public ChartPanel createLineChart(Map<String, Double> statisticsSource, Map<String, Double> statisticsTarget, String headerLegend, 
                    String xAxisLegend, String yAxisLegend){
        
        XYSeries seriesSource = new XYSeries(headerLegend +" - Source");
        XYSeries seriesTarget = new XYSeries(headerLegend +" - Target");
        //sort the key (by instant)and convert to int
        SortedSet<Integer> instantsSource = sortMapKeys(statisticsSource);
        SortedSet<Integer> instantsTarget = sortMapKeys(statisticsTarget);
        /////add the sorted elements to the dataset
        for (Integer instant : instantsSource) { 
            //System.out.println(instant +" -> "+statisticsSource.get(instant.toString()));
            seriesSource.add( instant, statisticsSource.get(instant.toString()) );
        }
        for (Integer instant : instantsTarget) { 
            //System.out.println(instant +" -> "+statisticsTarget.get(instant.toString()));
            seriesTarget.add( instant, statisticsTarget.get(instant.toString()) );
        }

        //Add series to dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesSource);
        dataset.addSeries(seriesTarget);
        JFreeChart lineChart = ChartFactory.createXYLineChart(headerLegend, yAxisLegend, xAxisLegend,
                dataset, PlotOrientation.VERTICAL, true,true,false);
        
        // Assign it to the chart
        /*XYPlot plot = (XYPlot) lineChart.getPlot();
        //plot.setDomainAxis(xAxis);
        
        NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
        //xAxis.setTickUnit(new NumberTickUnit( NUMBER_OF_TICKS_X_AXIS ));
        xAxis.setRange(instants.first(), instants.last());
        xAxis.setAutoTickUnitSelection(true);
        //xAxis.setAutoRangeMinimumSize(instants.first());
        xAxis.setAutoRangeIncludesZero(true);
        
        NumberAxis yAxis = (NumberAxis) plot.getDomainAxis();
        //get max min value of area
        //double min = statistics.values().stream().min(Double::compare).get();
        double max = statistics.get(instants.last().toString());
        double min = statistics.get(instants.first().toString());
        
        DecimalFormat newFormat = new DecimalFormat("#.00");
        int offset = getChartOffset((int) max);
        int bottomOffset = offset;
        yAxis.setNumberFormatOverride(newFormat);
        if (min > 0.0){
            min = 0;
            bottomOffset = 0;
        }
        yAxis.setRange(min+bottomOffset, max+offset);
        //yAxis.setAutoRange(true);
        //yAxis.setTickUnit(new NumberTickUnit(statistics.size()/2));*/
        
        return new ChartPanel(lineChart);
    }
    
    public JTable createJTable(Map<String, ?> dataset, String xAxisLegend, String yAxisLegend, boolean keysAreNumbers){
        //headers for the table
        String[] columns = new String[] {xAxisLegend, yAxisLegend };
        Object[][] data = new Object[dataset.size()][columns.length];
        DecimalFormat df = new DecimalFormat("#.##");
        if (keysAreNumbers){
            //sort the key
            SortedSet<Integer> statKeys = sortMapKeys(dataset);
            int i = 0;
            for (Integer statKey : statKeys) {
                data[i][0] = df.format(statKey);
                data[i][1] = df.format(dataset.get(statKey.toString()));
                i++;
            }
        }
        else{
            //keys are not numbers and should not be sorted
            int i = 0;
            for (Map.Entry<String, ?> entry : dataset.entrySet()) {
                data[i][0] = entry.getKey();
                data[i][1] = entry.getValue();
                i++;
            }
        }
        //create table with data
        return new JTable(data, columns);
    }
    
    //creates a JTable with 4 columns: 2 for source similarity comparison, and 2 for target similarity
    public JTable createJTable(Map<String, ?> datasetSource, Map<String, ?> datasetTarget, String xAxisLegend, String yAxisLegend, boolean keysAreNumbers){
        //headers for the table
        String[] columns = new String[] {xAxisLegend, yAxisLegend + " - source", yAxisLegend + " - target",};
        Object[][] data = new Object[datasetSource.size()][columns.length];
        DecimalFormat df = new DecimalFormat("#.##");
        if (keysAreNumbers){
            //sort the key
            SortedSet<Integer> statKeys = sortMapKeys(datasetSource);
            //both target and source have the same observations, so it is enought o sort one map and use those keys
            int i = 0;
            for (Integer statKey : statKeys) {
                data[i][0] = df.format(statKey);
                data[i][1] = df.format(datasetSource.get(statKey.toString()));
                data[i][2] = df.format(datasetTarget.get(statKey.toString()));
                i++;
            }
        }
        else{
            //keys are not numbers and should not be sorted
            int i = 0;
            for (Map.Entry<String, ?> entry : datasetSource.entrySet()) {
                data[i][0] = entry.getKey();
                data[i][1] = df.format(entry.getValue());
                data[i][2] = df.format(datasetTarget.get(entry.getKey()));
                i++;
            }
        }
        //create table with data
        return new JTable(data, columns);
    }
    
    private SortedSet<Integer> sortMapKeys(Map<String, ?> map){
        //sort the key (by instant)and convert to int
        Set<String> keys = new HashSet<>(map.keySet());
        SortedSet<Integer> sortedKeys = new TreeSet<>();
        for (String inst : keys){
            sortedKeys.add(Integer.parseInt(inst));
        }
        return sortedKeys;
    }
    
    //get an offset to add to the top (and maybe bottom) of the chart so the lines are visible
    private int getChartOffset(int number){
        String numberString = number+"";
        String offsetString = "1";
        for (int i = 1; i < numberString.length(); i++){
            offsetString += "0";
        }
        return Integer.parseInt(offsetString);
    }
}