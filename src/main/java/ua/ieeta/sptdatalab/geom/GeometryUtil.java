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
package ua.ieeta.sptdatalab.geom;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.locationtech.jts.algorithm.distance.DiscreteHausdorffDistance;
import org.locationtech.jts.algorithm.match.HausdorffSimilarityMeasure;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.locationtech.jts.operation.valid.TopologyValidationError;
import ua.ieeta.sptdatalab.model.GeometrySimplifier;
import ua.ieeta.sptdatalab.util.io.GeometryCollectionSummary;
import ua.ieeta.sptdatalab.util.io.GeometrySimilarityCollectionSummary;
import ua.ieeta.sptdatalab.util.io.IOUtil;

public class GeometryUtil {

    public enum FilterOperationEnum {

        AreaGreaterThan, AreaSmallerThan, AreaEqualTo;

        public static String[] getMethodsStringList() {
            String[] s = {AreaGreaterThan.toString(), AreaSmallerThan.toString(), AreaEqualTo.toString()};
            return s;
        }
    }

    public enum BatchMetricsEnum {

        JaccardIndex, JaccardDistance, HausdorffIndex, HausdorffDistance;

    }

    public static Double HausdorffDistance(Geometry geom1, Geometry geom2) {
        if (!(geom1.isValid())) {
            return null;
        }
        if (!(geom2.isValid())) {
            return null;
        }
        DiscreteHausdorffDistance measure = new DiscreteHausdorffDistance(geom1, geom2);
        return measure.orientedDistance();
    }

    public static Double HausdorffSimilarity(Geometry geom1, Geometry geom2) {
        if (!(geom1.isValid())) {
            return null;
        }
        if (!(geom2.isValid())) {
            return null;
        }
        HausdorffSimilarityMeasure measure = new HausdorffSimilarityMeasure();
        return measure.measure(geom1, geom2);
    }

    public static Double JaccardIndex(Geometry geom1, Geometry geom2) {

        Double jaccard = null;

        if (!(geom1.isValid())) {
            return null;
        }
        if (!(geom2.isValid())) {
            return null;
        }
        if (!(geom1.intersects(geom2))) {
            return 0.0;
        } else {
            try {
                jaccard = (geom1.intersection(geom2)).getArea() / (geom1.union(geom2)).getArea();
            } catch (Exception ex) {
            }
            return jaccard;
        }
    }

    public static Double JaccardDistance(Geometry geom1, Geometry geom2) {
        Double jaccardIndex = JaccardIndex(geom1, geom2);
        if (!(jaccardIndex == null)) {
            return 1.0 - jaccardIndex;
        } else {
            return 1.0;
        }
    }

    public static String structureSummary(Geometry g) {
        String structure = "";
        if (g instanceof Polygon) {
            structure = ((Polygon) g).getNumInteriorRing() + " holes";
        } else if (g instanceof GeometryCollection) {
            structure = g.getNumGeometries() + " elements";
        }

        return g.getGeometryType().toUpperCase()
                + " - " + structure
                + (structure.length() > 0 ? ", " : "")
                + g.getNumPoints() + " pts";
    }

    public static String metricsSummary(Geometry g) {
        String metrics = "Length = " + g.getLength() + "    Area = " + g.getArea();
        return metrics;
    }

    private static double chooseValueSmaller(double actual, double value1, double value2, double candidate) {

        if (value1 == actual) {

            if ((value2 < actual) && (value2 > candidate)) {
                return value2;
            } else {
                return candidate;
            }

        }
        if (value2 == actual) {

            if ((value1 < actual) && (value1 > candidate)) {
                return value1;
            } else {
                return candidate;
            }

        }
        if ((value1 > candidate) && (value1 > value2)) {
            return value1;
        }
        if ((value2 > candidate) && (value2 > value1)) {
            return value2;
        }
        return candidate;
    }

    private static double chooseValueGreater(double actual, double value1, double value2, double candidate) {
        if (value1 == actual) {

            if ((value2 > actual) && (value2 < candidate)) {
                return value2;
            } else {
                return candidate;
            }

        }
        if (value2 == actual) {

            if ((value1 > actual) && (value1 < candidate)) {
                return value1;
            } else {
                return candidate;
            }

        }
        if ((value1 < candidate) && (value1 < value2)) {
            return value1;
        }
        if ((value2 < candidate) && (value2 < value1)) {
            return value2;
        }
        return candidate;
    }

    private static Geometry movePoints(Geometry geom, Set<Coordinate> duplicateSet, int start) {

        int i;
        Double distanceStep = 0.01;

        i = start;
        while (duplicateSet.size() > 0) {

            if (duplicateSet.contains(geom.getCoordinates()[i])) {

                duplicateSet.remove(geom.getCoordinates()[i]);

                if (geom.getCoordinates()[i - 1].getX() != geom.getCoordinates()[i + 1].getX()) {
                    if ((geom.getCoordinates()[i - 1].getX() <= geom.getCoordinates()[i].getX()) && (geom.getCoordinates()[i + 1].getX() <= geom.getCoordinates()[i].getX())) {
                        geom.getCoordinates()[i].setX(chooseValueSmaller(geom.getCoordinates()[i].getX(), geom.getCoordinates()[i - 1].getX(), geom.getCoordinates()[i + 1].getX(), geom.getCoordinates()[i].getX() * (1 - distanceStep)));
                    } else if ((geom.getCoordinates()[i - 1].getX() >= geom.getCoordinates()[i].getX()) && (geom.getCoordinates()[i + 1].getX() >= geom.getCoordinates()[i].getX())) {
                        geom.getCoordinates()[i].setX(chooseValueGreater(geom.getCoordinates()[i].getX(), geom.getCoordinates()[i - 1].getX(), geom.getCoordinates()[i + 1].getX(), geom.getCoordinates()[i].getX() * (1 + distanceStep)));
                    }
                }

                if (geom.getCoordinates()[i - 1].getY() != geom.getCoordinates()[i + 1].getY()) {
                    if ((geom.getCoordinates()[i - 1].getY() <= geom.getCoordinates()[i].getY()) && (geom.getCoordinates()[i + 1].getY() <= geom.getCoordinates()[i].getY())) {
                        geom.getCoordinates()[i].setY(chooseValueSmaller(geom.getCoordinates()[i].getY(), geom.getCoordinates()[i - 1].getY(), geom.getCoordinates()[i + 1].getY(), geom.getCoordinates()[i].getY() * (1 - distanceStep)));
                    } else if ((geom.getCoordinates()[i - 1].getY() >= geom.getCoordinates()[i].getY()) && (geom.getCoordinates()[i + 1].getY() >= geom.getCoordinates()[i].getY())) {
                        geom.getCoordinates()[i].setY(chooseValueGreater(geom.getCoordinates()[i].getY(), geom.getCoordinates()[i - 1].getY(), geom.getCoordinates()[i + 1].getY(), geom.getCoordinates()[i].getY() * (1 + distanceStep)));
                    }
                }

            }
            i++;
        }

        return geom;

    }

    public static Geometry validateGeometry(Geometry geom) {

        if (geom.isValid()) {
            return geom;
        }

        int numPoints = geom.getNumPoints();
        double area = geom.getArea();

        GeometrySimplifier simp = new GeometrySimplifier();

        Geometry newPolygon;

        newPolygon = simp.simplifyVW(geom, 0.001, true);

        if (!(newPolygon.isEmpty())) {
            if ((newPolygon.getNumPoints() > numPoints * 0.98) && (newPolygon.getArea() > area * 0.98)) {
                return newPolygon;
            }
        }

        Set<Coordinate> coordSet = new HashSet<Coordinate>();
        Set<Coordinate> duplicateSet = new HashSet<Coordinate>();

        int first = geom.getCoordinates().length - 1;
        int i;

        int movingCount = 1;

        boolean continueTry = true;
        int lastCount = 0;
        int repeatedCount = 0;

        while (continueTry) {

            coordSet.clear();
            duplicateSet.clear();
            IsValidOp validOP = new IsValidOp(geom);
            validOP.setSelfTouchingRingFormingHoleValid(false);
            TopologyValidationError error = validOP.getValidationError();
            if (error != null) {
                coordSet.add(error.getCoordinate());
            }

            i = geom.getCoordinates().length - 2;
            while (i > 0) {

                if (coordSet.contains(geom.getCoordinates()[i]) == false) {
                    coordSet.add(geom.getCoordinates()[i]);
                } else {
                    duplicateSet.add(geom.getCoordinates()[i]);
                    first = i;
                }
                i--;
            }

            if ((duplicateSet.size() == lastCount) && (lastCount > 2)) {
                repeatedCount++;
            } else {
                lastCount = duplicateSet.size();
                repeatedCount = 0;
            }

            geom = movePoints(geom, duplicateSet, first);

            if (geom.isValid()) {
                return geom;
            }

            movingCount++;

            if ((movingCount > 15) || (repeatedCount > 5)) {
                continueTry = false;
            }

        }

        double start = 0.001;
        Geometry newPolygon2;
        try {
            newPolygon2 = simp.simplifyVW(geom, start, true);
        } catch (Exception ex) {
            return newPolygon;
        }
        
        if (!(newPolygon2.isEmpty())) {
            if ((newPolygon2.getNumPoints() > numPoints * 0.98) && (newPolygon2.getArea() > area * 0.98)) {
                return newPolygon2;
            }
        }
        Geometry newPolygon3 = newPolygon;
        if ((newPolygon2.getArea() > newPolygon.getArea()) && (newPolygon2.getNumPoints() > newPolygon.getNumPoints() * 0.3)) {
            newPolygon3 = newPolygon2;
        }
        int j = 1;
        boolean continueEvaluation = true;
        while (continueEvaluation) {
            if (j < 4) {
                newPolygon2 = simp.simplifyVW(geom, start * Math.pow(10, j), true);
            } else {
                newPolygon2 = simp.simplifyVW(geom, ((j - 3) * 0.1) + (start * Math.pow(10, 3)), true);
            }
            if ((newPolygon2.getArea() > newPolygon3.getArea()) && (newPolygon2.getNumPoints() > numPoints * 0.3)) {
                newPolygon3 = newPolygon2;
            }
            if ((j > 48) || (newPolygon2.getArea() >= area * 0.98)) {
                continueEvaluation = false;
            }
            j++;
        }
        if ((newPolygon3.getArea() >= newPolygon.getArea()) && (newPolygon3.getNumPoints() >= numPoints * 0.3)) {
            newPolygon = newPolygon3;
        }
        if (newPolygon.isEmpty()) {
            newPolygon = geom.buffer(0);
        }
        return newPolygon;

    }

    public static GeometryCollectionSummary createFileStatistics(String geometriesFileName, String summaryFileName) throws ParseException, IOException {

        return IOUtil.createStatistics(geometriesFileName, summaryFileName);

    }

    public static GeometryCollectionSummary makeGeometryValid(String geometriesFileName, String outputFileName, String statisticsFileName) throws ParseException, IOException {
        return IOUtil.makeValid(geometriesFileName, outputFileName, statisticsFileName);
    }

    public static GeometryCollectionSummary filterGeometriesByArea(String geometriesFileName, String outputFileName, String statisticsFileName, FilterOperationEnum operation, Double value) throws ParseException, IOException {
        return IOUtil.filterGeometriesByArea(geometriesFileName, outputFileName, statisticsFileName, operation, value);
    }

    public static GeometrySimilarityCollectionSummary computeMetrics(String geometriesFileName, String fullFileName, String metricsFileName, String summaryFileName) throws ParseException, IOException {
        return IOUtil.computeMetrics(geometriesFileName, fullFileName, metricsFileName, summaryFileName);
    }

}
