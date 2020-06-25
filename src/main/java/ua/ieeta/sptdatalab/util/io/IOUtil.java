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
package ua.ieeta.sptdatalab.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBHexFileReader;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKTFileReader;
import org.locationtech.jts.io.WKTReader;
import ua.ieeta.sptdatalab.geom.GeometryUtil;
import ua.ieeta.sptdatalab.geom.GeometryUtil.FilterOperationEnum;
import ua.ieeta.sptdatalab.model.GeometrySimplifier;
import ua.ieeta.sptdatalab.model.GeometrySimplifier.SimplificationMethod;
import ua.ieeta.sptdatalab.model.KeyObservationSelection;
import ua.ieeta.sptdatalab.util.FileUtil;

public class IOUtil {

    public static Geometry readFile(String filename, GeometryFactory geomFact)
            throws Exception, IOException {
        return readWKTFile(filename, geomFact);
    }

    private static Geometry readWKTFile(String filename, GeometryFactory geomFact)
            throws ParseException, IOException {
        return readWKTString(FileUtil.readText(filename), geomFact);
    }

    /**
     * Reads one or more WKT geometries from a string.
     *
     * @param fileName
     * @return a list of geometries
     * @throws ParseException
     * @throws IOException
     */
    public static List<Geometry> readWKTList(String fileName)
            throws ParseException, IOException {

        File f = new File(fileName);
        List geomList = new ArrayList();

        WKTReader wktreader = new WKTReader();
        Geometry geom;

        FileInputStream inputStream = new FileInputStream(f);
        Scanner sc = new Scanner(inputStream);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.contains(";")) {
                String[] data = line.split(";");
                geom = wktreader.read(data[1]);
                geom.setUserData(data[0]);
            } else {
                geom = wktreader.read(line);
            }
            geomList.add(geom);
        }

        sc.close();

        return geomList;

    }

    public static Geometry readGeometryLine(Scanner sc)
            throws ParseException, IOException {

        WKTReader wktreader = new WKTReader();
        Geometry geom = null;

        if (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.contains(";")) {
                geom = wktreader.read(line.split(";")[1]);
                geom.setUserData(line.split(";")[0]);
            } else {
                geom = wktreader.read(line);
            }

        }

        return geom;

    }

    public static Scanner openFileRead(String fileName) throws FileNotFoundException {

        File f = new File(fileName);

        FileInputStream inputStream = new FileInputStream(f);

        Scanner sc = new Scanner(inputStream);

        return sc;

    }

    public static GeometrySimilarityCollectionSummary computeMetrics(String geometriesFileName, String fullFileName, String metricsFileName, String summaryFileName) throws ParseException, IOException {

        File f = new File(geometriesFileName);
        File f2 = new File(fullFileName);
        File fw = new File(metricsFileName);

        FileInputStream inputStream = new FileInputStream(f);
        FileInputStream inputStream2 = new FileInputStream(f2);

        WKTReader wktr = new WKTReader();
        WKTReader wktr2 = new WKTReader();

        FileWriter writer = new FileWriter(fw);

        Geometry geom1 = null;
        Geometry geom2 = null;

        GeometrySimilarityCollectionSummary summary = new GeometrySimilarityCollectionSummary();

        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + geometriesFileName;

        configuration = configuration + " - Reference file: " + fullFileName;

        configuration = configuration + " - Create metrics ";

        Scanner sc = new Scanner(inputStream);
        Scanner sc2 = new Scanner(inputStream2);

        int file1Index = 0;

        writer.write("Time;Jaccard Index; Jaccard Distance; Approximate Hausdorff Distance; Hausdorff Similarity Measure" + System.lineSeparator());

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.contains(";")) {
                geom1 = wktr.read(line.split(";")[1]);
                geom1.setUserData(line.split(";")[0]);
                file1Index = Integer.valueOf(geom1.getUserData().toString());
            } else {
                geom1 = wktr.read(line);
                file1Index++;
            }

            boolean found = false;

            int fullIndex = 0;

            while (!(found) && (sc2.hasNextLine())) {
                String line2 = sc2.nextLine();

                if (line2.contains(";")) {
                    geom2 = wktr2.read(line2.split(";")[1]);
                    geom2.setUserData(line2.split(";")[0]);
                    fullIndex = Integer.valueOf(geom2.getUserData().toString());

                } else {
                    geom2 = wktr2.read(line2);
                    fullIndex++;
                }
                if (fullIndex == file1Index) {
                    found = true;
                }
            }
            if (found) {
                summary.addGeometries(geom1, geom2);

                if ((geom1.isValid()) && (geom2.isValid())) {

                    writer.write(String.valueOf(fullIndex) + ";" + String.valueOf(summary.getJaccardIndex()) + ";" + String.valueOf(summary.getJaccardDistance()) + ";" + String.valueOf(summary.getHausdorffDistance()) + ";" + String.valueOf(summary.getHausdorffSimilarity()) + System.lineSeparator());

                } else {
                    writer.write(String.valueOf(fullIndex) + ";" + ";" + ";" + ";" + System.lineSeparator());
                }

            } else {
                writer.write(String.valueOf(fullIndex) + ";" + ";" + ";" + ";" + System.lineSeparator());
            }

            summary.addGeometries(geom1, geom2);

        }

        configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

        summary.setParameterData(configuration);

        writeSummary(summaryFileName, summary.getSummary());

        sc.close();
        sc2.close();

        return summary;

    }

    public static GeometryCollectionSummary makeValid(String inputFileName, String outputFileName, String statisticsFileName)
            throws ParseException, IOException {

        File f = new File(inputFileName);
        File f2 = new File(outputFileName);
        FileWriter writer = new FileWriter(f2);

        FileInputStream inputStream = new FileInputStream(f);
        WKTReader wktr = new WKTReader();
        Geometry geom;

        GeometryCollectionSummary summary = new GeometryCollectionSummary();

        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + inputFileName;

        configuration = configuration + " - Make valid (statistics after make valid) ";

        Scanner sc = new Scanner(inputStream);
        boolean found = false;
        String index = null;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.contains(";")) {
                geom = wktr.read(line.split(";")[1]);
                index = line.split(";")[0];
            } else {
                geom = wktr.read(line);
            }

            geom = GeometryUtil.validateGeometry(geom);
            summary.addGeometry(geom);
            if (!(geom == null)) {
                if (!(index == null)) {
                    writer.write(index + ";" + geom.toString() + System.lineSeparator());
                } else {
                    writer.write(geom.toString() + System.lineSeparator());
                }
            }
        }

        writer.close();

        configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

        summary.setParameterData(configuration);

        writeSummary(statisticsFileName, summary.getSummary());

        sc.close();
        return summary;

    }

    public static GeometryCollectionSummary filterGeometriesByArea(String inputFileName, String outputFileName, String statisticsFileName, FilterOperationEnum operation, Double value)
            throws ParseException, IOException {

        File f = new File(inputFileName);
        File f2 = new File(outputFileName);
        FileWriter writer = new FileWriter(f2);

        FileInputStream inputStream = new FileInputStream(f);
        WKTReader wktr = new WKTReader();
        Geometry geom;

        GeometryCollectionSummary summary = new GeometryCollectionSummary();

        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + inputFileName;

        configuration = configuration + " - Filter area (statistics for filtered) - Filter " + operation.toString() + " " + String.valueOf(value);

        int time = 0;
        Scanner sc = new Scanner(inputStream);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Boolean filtered = false;

            if (line.contains(";")) {
                geom = wktr.read(line.split(";")[1]);
                geom.setUserData(line.split(";")[0]);
            } else {
                geom = wktr.read(line);
            }

            if (operation == FilterOperationEnum.AreaEqualTo) {
                if (geom.getArea() == value) {
                    summary.addGeometry(geom);
                    filtered = true;
                }
            }

            if (operation == FilterOperationEnum.AreaGreaterThan) {
                if (geom.getArea() > value) {
                    summary.addGeometry(geom);
                    filtered = true;
                }
            }

            if (operation == FilterOperationEnum.AreaSmallerThan) {
                if (geom.getArea() < value) {
                    summary.addGeometry(geom);
                    filtered = true;
                }
            }

            if (!(geom == null)) {
                if (!(geom.getUserData() == null)) {
                    if (!(filtered)) {
                        writer.write(String.valueOf(geom.getUserData().toString()) + ";" + geom.toString() + System.lineSeparator());
                    }
                } else {
                    if (!(filtered)) {
                        writer.write(String.valueOf(time) + ";" + geom.toString() + System.lineSeparator());
                    }
                    time++;
                }
            } else {
                time++;
            }

        }

        writer.close();

        sc.close();

        configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

        summary.setParameterData(configuration);

        writeSummary(statisticsFileName, summary.getSummary());

        return summary;

    }

    public static GeometryCollectionSummary createStatistics(String inputFileName, String outputFileName)
            throws ParseException, IOException {

        File f = new File(inputFileName);

        FileInputStream inputStream = new FileInputStream(f);
        WKTReader wktr = new WKTReader();
        Geometry geom = null;
        GeometryCollectionSummary summary = new GeometryCollectionSummary();

        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + inputFileName;

        configuration = configuration + " - Create statistics ";

        Scanner sc = new Scanner(inputStream);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.contains(";")) {
                geom = wktr.read(line.split(";")[1]);
                geom.setUserData(line.split(";")[0]);
            } else {
                geom = wktr.read(line);
            }

            summary.addGeometry(geom);

        }

        configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

        summary.setParameterData(configuration);

        writeSummary(outputFileName, summary.getSummary());

        sc.close();

        return summary;

    }

    public static void writeSummary(String fileName, String summary) {

        FileWriter writer = null;
        try {
            File f = new File(fileName);
            writer = new FileWriter(f);
            writer.write(summary);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(IOUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static List<Geometry> readTimeWKTList(String fileName)
            throws ParseException, IOException {

        File f = new File(fileName);
        List geomList = new ArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {

            String line;

            WKTReader wktreader = new WKTReader();
            Geometry geom;

            while ((line = br.readLine()) != null) {

                geom = wktreader.read(line.split(";")[1]);
                geom.setUserData(line.split(";")[0]);

                geomList.add(geom);

            }

        }
        return geomList;

    }

    /**
     * Reads one or more WKT geometries from a string.
     *
     * @param wkt
     * @param geomFact
     * @return the geometry read
     * @throws ParseException
     * @throws IOException
     */
    public static Geometry readWKTString(String wkt, GeometryFactory geomFact)
            throws ParseException, IOException {
        WKTReader reader = new WKTReader(geomFact);
        WKTFileReader fileReader = new WKTFileReader(new StringReader(wkt), reader);
        List geomList = fileReader.read();

        if (geomList.size() == 1) {
            return (Geometry) geomList.get(0);
        }

        return geomFact.createGeometryCollection(GeometryFactory.toGeometryArray(geomList));
    }

    public static Geometry readWKBHexString(String wkb, GeometryFactory geomFact)
            throws ParseException, IOException {
        WKBReader reader = new WKBReader(geomFact);
        WKBHexFileReader fileReader = new WKBHexFileReader(new StringReader(wkb), reader);
        List geomList = fileReader.read();

        if (geomList.size() == 1) {
            return (Geometry) geomList.get(0);
        }

        return geomFact.createGeometryCollection(GeometryFactory.toGeometryArray(geomList));
    }

    public static void writeFile(String filename, List lines)
            throws ParseException, IOException {

        File file = new File(filename);
        FileWriter writer = new FileWriter(file);

        int i = 0;
        while (i < lines.size()) {

            writer.write(lines.get(i).toString() + System.lineSeparator());
            i++;
        }

        writer.close();

    }

    public static void appendFile(String filename, List lines)
            throws ParseException, IOException {

        File file = new File(filename);
        FileWriter writer = new FileWriter(file, true);

        int i = 0;
        while (i < lines.size()) {

            writer.write(lines.get(i).toString() + System.lineSeparator());
            i++;
        }

        writer.close();

    }

    public static void appendLineFile(FileWriter writer, String line)
            throws ParseException, IOException {

        writer.write(line + System.lineSeparator());

    }

    public static FileWriter openFile(String filename, Boolean append)
            throws ParseException, IOException {

        File file = new File(filename);
        FileWriter writer = new FileWriter(file, append);

        return writer;

    }

    public static void closeFile(FileWriter writer)
            throws ParseException, IOException {

        writer.close();

    }

    public static void closeFile(Scanner sc)
            throws ParseException, IOException {

        sc.close();

    }
}
