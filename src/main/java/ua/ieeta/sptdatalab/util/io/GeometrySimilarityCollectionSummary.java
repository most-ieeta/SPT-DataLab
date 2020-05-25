/* This file is part of SPT Data Lab.
*
* Copyright (C) 2020, University of Aveiro,
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
package ua.ieeta.sptdatalab.util.io;

import org.locationtech.jts.geom.Geometry;
import ua.ieeta.sptdatalab.geom.GeometryUtil;


public class GeometrySimilarityCollectionSummary {

    private String parameterData = "";
    
    private double minJaccardIndex = Double.MAX_VALUE;
    private double maxJaccardIndex = Double.MIN_VALUE;
    private double avgJaccardIndex = 0;

    private double minJaccardDistance = Double.MAX_VALUE;
    private double maxJaccardDistance = Double.MIN_VALUE;
    private double avgJaccardDistance = 0;

    private double minHausdorffDistance = Double.MAX_VALUE;
    private double maxHausdorffDistance = Double.MIN_VALUE;
    private double avgHausdorffDistance = 0;

    private double minHausdorffSimilarity = Double.MAX_VALUE;
    private double maxHausdorffSimilarity = Double.MIN_VALUE;
    private double avgHausdorffSimilarity = 0;

    private int count = 0;
    private int countInvalid = 0;
    private int countEmpty = 0;

    private Double JaccardIndex = null;
    private Double JaccardDistance = null;
    private Double HausdorffDistance = null;
    private Double HausdorffSimilarity = null;

    
    public void addGeometries(Geometry geom1, Geometry geom2) {

        count++;

        if (geom1.isEmpty()) {
            countEmpty++;
            return;
        }

        if (geom2.isEmpty()) {
            countEmpty++;
            return;
        }

        if (!(geom1.isValid())) {
            countInvalid++;
            return;
        }

        if (!(geom2.isValid())) {
            countInvalid++;
            return;
        }

        JaccardIndex = GeometryUtil.JaccardIndex(geom1, geom2);
        if (!(JaccardIndex == null)) {
            if ((getJaccardIndex() >= 0) && (getJaccardIndex() < minJaccardIndex)) {
                minJaccardIndex = getJaccardIndex();
            }

            if ((getJaccardIndex() > maxJaccardIndex)) {
                maxJaccardIndex = getJaccardIndex();
            }

            if ((getJaccardIndex() >= 0)) {
                avgJaccardIndex = avgJaccardIndex + getJaccardIndex();
                
                JaccardDistance = 1 - getJaccardIndex();
                avgJaccardDistance = avgJaccardDistance + getJaccardDistance();
            }
            else 
                JaccardDistance = -1.0;

            minJaccardDistance = 1 - maxJaccardIndex;
            maxJaccardDistance = 1 - minJaccardIndex;
            
        }
        else
            JaccardDistance = null;

        
        HausdorffDistance = GeometryUtil.HausdorffDistance(geom1, geom2);
        if (!(HausdorffDistance == null)) {

            if ((getHausdorffDistance() >= 0) && (getHausdorffDistance() < minHausdorffDistance)) {
                minHausdorffDistance = getHausdorffDistance();
            }

            if ((getHausdorffDistance() > maxHausdorffDistance)) {
                maxHausdorffDistance = getHausdorffDistance();
            }

            if ((getHausdorffDistance() >= 0)) {
                avgHausdorffDistance = avgHausdorffDistance + getHausdorffDistance();
            }

        }

        HausdorffSimilarity = null;
        HausdorffSimilarity = GeometryUtil.HausdorffSimilarity(geom1, geom2);
        if (!(HausdorffSimilarity == null)) {

            if ((getHausdorffSimilarity() >= 0) && (getHausdorffSimilarity() < minHausdorffSimilarity)) {
                minHausdorffSimilarity = getHausdorffSimilarity();
            }

            if ((getHausdorffSimilarity() > maxHausdorffSimilarity)) {
                maxHausdorffSimilarity = getHausdorffSimilarity();
            }

            if ((getHausdorffSimilarity() >= 0)) {
                avgHausdorffSimilarity = avgHausdorffSimilarity + getHausdorffSimilarity();
            }

        }

    }

    public String getHTMLSummary() {

        String text;

        text = "<html>";

        text = text + "Summary" + "<br>";
        text = text + parameterData + "<br>"+"<br>";
        text = text + "Number of geometry compare operations: " + String.valueOf(getCount()) + "<br>";
        text = text + "Number of invalided compares: " + String.valueOf(getCountInvalid()) + "<br>";
        text = text + "Number of empty compares: " + String.valueOf(getCountEmpty()) + "<br>";
        text = text + "<br>";
        text = text + "Lowest Jaccard Index: " + String.valueOf(getMinJaccardIndex()) + "<br>";
        text = text + "Highest Jaccard Index: " + String.valueOf(getMaxJaccardIndex()) + "<br>";
        text = text + "Average Jaccard Index: " + String.valueOf(getAvgJaccardIndex() / (getCount() - getCountInvalid() - getCountEmpty())) + "<br>";
        text = text + "<br>";
        text = text + "Lowest Jaccard Distance: " + String.valueOf(getMinJaccardDistance()) + "<br>";
        text = text + "Highest Jaccard Distance: " + String.valueOf(getMaxJaccardDistance()) + "<br>";
        text = text + "Average Jaccard Distance: " + String.valueOf(getAvgJaccardDistance() / (getCount() - getCountInvalid() - getCountEmpty())) + "<br>";
        text = text + "<br>";
        text = text + "Lowest Hausdorff Distance: " + String.valueOf(getMinHausdorffDistance()) + "<br>";
        text = text + "Highest Hausdorff Distance: " + String.valueOf(getMaxHausdorffDistance()) + "<br>";
        text = text + "Average Hausdorff Distance: " + String.valueOf(getAvgHausdorffDistance() / (getCount() - getCountInvalid() - getCountEmpty())) + "<br>";
        text = text + "<br>";
        text = text + "Lowest Hausdorff Similarity: " + String.valueOf(getMinHausdorffSimilarity()) + "<br>";
        text = text + "Highest Hausdorff Similarity: " + String.valueOf(getMaxHausdorffSimilarity()) + "<br>";
        text = text + "Average Hausdorff Similarity: " + String.valueOf(getAvgHausdorffSimilarity() / (getCount() - getCountInvalid() - getCountEmpty())) + "<br>";
        text = text + "<br>";
        
        text = text + "</html>";
        
        return text;
    }

    public String getSummary() {

        String text;

        text = "Summary" + System.lineSeparator();
        
        text = text + parameterData + System.lineSeparator()+ System.lineSeparator();
        text = text + "Number of geometry compare operations: " + String.valueOf(getCount()) + System.lineSeparator();
        text = text + "Number of invalided compares: " + String.valueOf(getCountInvalid()) + System.lineSeparator();
        text = text + "Number of empty compares: " + String.valueOf(getCountEmpty()) + System.lineSeparator();
        text = text + System.lineSeparator();
        text = text + "Lowest Jaccard Index: " + String.valueOf(getMinJaccardIndex()) + System.lineSeparator();
        text = text + "Highest Jaccard Index: " + String.valueOf(getMaxJaccardIndex()) + System.lineSeparator();
        text = text + "Average Jaccard Index: " + String.valueOf(getAvgJaccardIndex() / (getCount() - getCountInvalid() - getCountEmpty())) + System.lineSeparator();
        text = text + System.lineSeparator();
        text = text + "Lowest Jaccard Distance: " + String.valueOf(getMinJaccardDistance()) + System.lineSeparator();
        text = text + "Highest Jaccard Distance: " + String.valueOf(getMaxJaccardDistance()) + System.lineSeparator();
        text = text + "Average Jaccard Distance: " + String.valueOf(getAvgJaccardDistance() / (getCount() - getCountInvalid() - getCountEmpty())) + System.lineSeparator();
        text = text + System.lineSeparator();
        text = text + "Lowest Hausdorff Distance: " + String.valueOf(getMinHausdorffDistance()) + System.lineSeparator();
        text = text + "Highest Hausdorff Distance: " + String.valueOf(getMaxHausdorffDistance()) + System.lineSeparator();
        text = text + "Average Hausdorff Distance: " + String.valueOf(getAvgHausdorffDistance() / (getCount() - getCountInvalid() - getCountEmpty())) + System.lineSeparator();
        text = text + System.lineSeparator();
        text = text + "Lowest Hausdorff Similarity: " + String.valueOf(getMinHausdorffSimilarity()) + System.lineSeparator();
        text = text + "Highest Hausdorff Similarity: " + String.valueOf(getMaxHausdorffSimilarity()) + System.lineSeparator();
        text = text + "Average Hausdorff Similarity: " + String.valueOf(getAvgHausdorffSimilarity() / (getCount() - getCountInvalid() - getCountEmpty())) + System.lineSeparator();
        text = text + System.lineSeparator();
        
        return text;
    }


    public double getMinJaccardIndex() {
        return minJaccardIndex;
    }

    public double getMaxJaccardIndex() {
        return maxJaccardIndex;
    }

    public double getAvgJaccardIndex() {
        return avgJaccardIndex;
    }

    public double getMinJaccardDistance() {
        return minJaccardDistance;
    }

    public double getMaxJaccardDistance() {
        return maxJaccardDistance;
    }

    public double getAvgJaccardDistance() {
        return avgJaccardDistance;
    }

    public double getMinHausdorffDistance() {
        return minHausdorffDistance;
    }

    public double getMaxHausdorffDistance() {
        return maxHausdorffDistance;
    }

    public double getAvgHausdorffDistance() {
        return avgHausdorffDistance;
    }

    public double getMinHausdorffSimilarity() {
        return minHausdorffSimilarity;
    }

    public double getMaxHausdorffSimilarity() {
        return maxHausdorffSimilarity;
    }

    public double getAvgHausdorffSimilarity() {
        return avgHausdorffSimilarity;
    }

    public int getCount() {
        return count;
    }

    public int getCountInvalid() {
        return countInvalid;
    }

    public int getCountEmpty() {
        return countEmpty;
    }

    public Double getJaccardIndex() {
        return JaccardIndex;
    }

    public Double getJaccardDistance() {
        return JaccardDistance;
    }

    public Double getHausdorffDistance() {
        return HausdorffDistance;
    }

    public Double getHausdorffSimilarity() {
        return HausdorffSimilarity;
    }

    public String getParameterData() {
        return parameterData;
    }

    public void setParameterData(String parameterData) {
        this.parameterData = parameterData;
    }

}
