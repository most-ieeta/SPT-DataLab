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

public class GeometryCollectionSummary {

    private String parameterData = "";

    private double minArea = Double.MAX_VALUE;
    private double maxArea = Double.MIN_VALUE;
    private double avgArea = 0;
    private double firstArea = 0;
    private double lastArea = 0;

    private int minPoints = Integer.MAX_VALUE;
    private int maxPoints = Integer.MIN_VALUE;
    private int sumPoints = 0;

    private int count = 0;
    private int countInvalid = 0;
    private int countEmpty = 0;

    public void addGeometry(Geometry geom) {


        if (geom.isEmpty()) {
            countEmpty++;
            count++;
            return;
        }

        if (geom.getArea() < getMinArea()) {
            minArea = geom.getArea();
        }

        if (geom.getArea() > getMaxArea()) {
            maxArea = geom.getArea();
        }

        avgArea = getAvgArea() + geom.getArea();

        if (getCount() == 0) {
            firstArea = geom.getArea();
        }
        count++;

        if (geom.getNumPoints() < getMinPoints()) {
            minPoints = geom.getNumPoints();
        }

        if (geom.getNumPoints() > getMaxPoints()) {
            maxPoints = geom.getNumPoints();
        }

        sumPoints = getNumPoints() + geom.getNumPoints();

        lastArea = geom.getArea();

        if (!(geom.isValid())) {
            countInvalid++;
        }

    }

    public double getMinArea() {
        return minArea;
    }

    public double getMaxArea() {
        return maxArea;
    }

    public double getAvgArea() {
        return avgArea;
    }

    public double getFirstArea() {
        return firstArea;
    }

    public double getLastArea() {
        return lastArea;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getNumPoints() {
        return sumPoints;
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

    public String getParameterData() {
        return parameterData;
    }

    public void setParameterData(String parameterData) {
        this.parameterData = parameterData;
    }

    public String getHTMLSummary() {

        String text;

        text = "<html>";

        text = text + "Summary" + "<br>";
        text = text + parameterData + "<br>" + "<br>";
        text = text + "Number of geometries: " + String.valueOf(getCount()) + "<br>";
        text = text + "Number of invalid geometries: " + String.valueOf(getCountInvalid()) + "<br>";
        text = text + "Number of empty geometries: " + String.valueOf(getCountEmpty()) + "<br>";
        text = text + "<br>";
        text = text + "Number of points: " + String.valueOf(getNumPoints()) + "<br>";
        text = text + "Lowest number of points in a geometry: " + String.valueOf(getMinPoints()) + "<br>";
        text = text + "Highest number of points in a geometry: " + String.valueOf(getMaxPoints()) + "<br>";
        text = text + "Average number of points per geometry: " + String.valueOf((1.0 * getNumPoints()) / (getCount() * 1.0)) + "<br>";
        text = text + "<br>";
        text = text + "Largest area per geometry: " + String.valueOf(getMaxArea()) + "<br>";
        text = text + "Smallest area per geometry: " + String.valueOf(getMinArea()) + "<br>";
        text = text + "Average area per geometry: " + String.valueOf(getAvgArea() / getCount()) + "<br>";
        text = text + "Area of the first geometry: " + String.valueOf(getFirstArea()) + "<br>";
        text = text + "Area of the last geometry: " + String.valueOf(getLastArea()) + "<br>";

        text = text + "</html>";

        return text;
    }

    public String getSummary() {

        String text;

        text = "Summary" + System.lineSeparator();

        text = text + parameterData + System.lineSeparator() + System.lineSeparator();
        text = text + "Number of geometries: " + String.valueOf(getCount()) + System.lineSeparator();
        text = text + "Number of invalid geometries: " + String.valueOf(getCountInvalid()) + System.lineSeparator();
        text = text + "Number of empty geometries: " + String.valueOf(getCountEmpty()) + System.lineSeparator();
        text = text + System.lineSeparator();
        text = text + "Number of points: " + String.valueOf(getNumPoints()) + System.lineSeparator();
        text = text + "Lowest number of points in a geometry: " + String.valueOf(getMinPoints()) + System.lineSeparator();
        text = text + "Highest number of points in a geometry: " + String.valueOf(getMaxPoints()) + System.lineSeparator();
        text = text + "Average number of points per geometry: " + String.valueOf((1.0 * getNumPoints()) / (getCount() * 1.0)) + System.lineSeparator();
        text = text + System.lineSeparator();
        text = text + "Largest area per geometry: " + String.valueOf(getMaxArea()) + System.lineSeparator();
        text = text + "Smallest area per geometry: " + String.valueOf(getMinArea()) + System.lineSeparator();
        text = text + "Average area per geometry: " + String.valueOf(getAvgArea() / getCount()) + System.lineSeparator();
        text = text + "Area of the first geometry: " + String.valueOf(getFirstArea()) + System.lineSeparator();
        text = text + "Area of the last geometry: " + String.valueOf(getLastArea()) + System.lineSeparator();

        return text;
    }

}
