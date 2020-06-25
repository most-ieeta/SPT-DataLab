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
package ua.ieeta.sptdatalab.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.simplify.TopologyPreservingSimplifier;
import org.locationtech.jts.simplify.VWSimplifier;
import ua.ieeta.sptdatalab.app.AppConstants;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderMenuBar;
import ua.ieeta.sptdatalab.util.io.GeometrySimilarityCollectionSummary;
import ua.ieeta.sptdatalab.util.io.IOUtil;
import static ua.ieeta.sptdatalab.util.io.IOUtil.writeSummary;

public class GeometrySimplifier {

    public enum SimplificationMethod {
        TopologyPreserve,
        VisvalingamWhyatt,
        MatchingAware
    }

    private GeometrySimilarityCollectionSummary simplifyPreserveTopology(String inputFileName, String outputFileName, String statisticsFileName, String statisticsSummaryFileName, Double tolerance) throws IOException, ParseException {

        Scanner sc = IOUtil.openFileRead(inputFileName);

        FileWriter writer = IOUtil.openFile(outputFileName, false);
        FileWriter writerSummary = IOUtil.openFile(statisticsFileName, false);

        WKTReader wktr = new WKTReader();
        Geometry geom = null;
        Geometry geom2 = null;

        GeometrySimilarityCollectionSummary summary = new GeometrySimilarityCollectionSummary();
        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + inputFileName;

        configuration = configuration + " - Method: DP - Tolerance: " + String.valueOf(tolerance);

        writerSummary.write("Jaccard Index; Jaccard Distance; Approximate Hausdorff Distance; Hausdorff Similarity Measure" + System.lineSeparator());

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.contains(";")) {
                geom = wktr.read(line.split(";")[1]);
                geom.setUserData(line.split(";")[0]);
            } else {
                geom = wktr.read(line);
            }

            TopologyPreservingSimplifier simp = new TopologyPreservingSimplifier(geom);

            simp.setDistanceTolerance(tolerance);
            geom2 = simp.getResultGeometry();

            summary.addGeometries(geom, geom2);

            IOUtil.appendLineFile(writer, geom2.toString());
            IOUtil.appendLineFile(writerSummary, String.valueOf(summary.getJaccardIndex()) + ";" + String.valueOf(summary.getJaccardDistance()) + ";" + String.valueOf(summary.getHausdorffDistance()) + ";" + String.valueOf(summary.getHausdorffSimilarity()));
        }

        configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

        summary.setParameterData(configuration);

        IOUtil.closeFile(sc);
        IOUtil.closeFile(writer);
        IOUtil.closeFile(writerSummary);

        writeSummary(statisticsSummaryFileName, summary.getSummary());

        return summary;

    }

    private GeometrySimilarityCollectionSummary simplifyVW(String inputFileName, String outputFileName, String statisticsFileName, String statisticsSummaryFileName, Double tolerance, boolean valid) throws FileNotFoundException, IOException, ParseException {

        Scanner sc = IOUtil.openFileRead(inputFileName);

        FileWriter writer = IOUtil.openFile(outputFileName, false);
        FileWriter writerSummary = IOUtil.openFile(statisticsFileName, false);

        WKTReader wktr = new WKTReader();
        Geometry geom = null;
        Geometry geom2 = null;

        GeometrySimilarityCollectionSummary summary = new GeometrySimilarityCollectionSummary();
        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + inputFileName;

        configuration = configuration + " - Method: VW - Tolerance: " + String.valueOf(tolerance);

        writerSummary.write("Jaccard Index; Jaccard Distance; Approximate Hausdorff Distance; Hausdorff Similarity Measure" + System.lineSeparator());

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.contains(";")) {
                geom = wktr.read(line.split(";")[1]);
                geom.setUserData(line.split(";")[0]);
            } else {
                geom = wktr.read(line);
            }

            VWSimplifier simp = new VWSimplifier(geom);

            simp.setDistanceTolerance(tolerance);
            simp.setEnsureValid(valid);

            Geometry geomS = simp.getResultGeometry();

            int i = 0;
            int selected = 0;
            double area = 0;

            if (geomS instanceof MultiPolygon) {
                while (i < geomS.getNumGeometries()) {

                    if (geomS.getGeometryN(i).getArea() > area) {
                        selected = i;
                        area = geomS.getGeometryN(i).getArea();
                    }

                    i++;
                }

                geom2 = geomS.getGeometryN(selected);

            } else {
                geom2 = geomS;
            }

            summary.addGeometries(geom, geom2);

            IOUtil.appendLineFile(writer, geom2.toString());
            IOUtil.appendLineFile(writerSummary, String.valueOf(summary.getJaccardIndex()) + ";" + String.valueOf(summary.getJaccardDistance()) + ";" + String.valueOf(summary.getHausdorffDistance()) + ";" + String.valueOf(summary.getHausdorffSimilarity()));
        }

        configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

        summary.setParameterData(configuration);
        IOUtil.closeFile(sc);

        IOUtil.closeFile(writer);
        IOUtil.closeFile(writerSummary);

        writeSummary(statisticsSummaryFileName, summary.getSummary());

        return summary;

    }

    public Geometry simplifyVW(Geometry geom, Double tolerance, boolean valid) {

        VWSimplifier simp = new VWSimplifier(geom);

        simp.setDistanceTolerance(tolerance);
        simp.setEnsureValid(valid);

        Geometry geomS = simp.getResultGeometry();

        int i = 0;
        int selected = 0;
        double area = 0;

        if (geomS instanceof MultiPolygon) {
            while (i < geomS.getNumGeometries()) {

                if (geomS.getGeometryN(i).getArea() > area) {
                    selected = i;
                    area = geomS.getGeometryN(i).getArea();
                }

                i++;
            }

            return geomS.getGeometryN(selected);

        } else {
            return geomS;
        }

    }

    public GeometrySimilarityCollectionSummary simplify(String inputFileName, String outputFileName, String statisticsFileName, String statisticsSummaryFileName, Double tolerance, SimplificationMethod method) {

        try {

            if (method == SimplificationMethod.TopologyPreserve) {
                return simplifyPreserveTopology(inputFileName, outputFileName, statisticsFileName, statisticsSummaryFileName, tolerance);
            }

            if (method == SimplificationMethod.VisvalingamWhyatt) {
                return simplifyVW(inputFileName, outputFileName, statisticsFileName, statisticsSummaryFileName, tolerance, true);
            }

        } catch (ParseException ex) {
            Logger.getLogger(GeometrySimplifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeometrySimplifier.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public GeometrySimilarityCollectionSummary simplify(String inputFileName, String outputFileName, String statisticsFileName, String statisticsSummaryFileName, String userInputPercentage, String userInputTime, SimplificationMethod method) throws IOException, FileNotFoundException, InterruptedException {

        if (method == SimplificationMethod.MatchingAware) {
            return simplifyMatchingAware(inputFileName, outputFileName, statisticsFileName, statisticsSummaryFileName, userInputPercentage, userInputTime);
        }

        return null;
    }

    private GeometrySimilarityCollectionSummary simplifyMatchingAware(String inputFileName, String outputFileName, String statisticsFileName, String statisticsSummaryFileName, String userInputPercentage, String userInputTime) throws FileNotFoundException, IOException, InterruptedException {

        try {

            Scanner sc = IOUtil.openFileRead(inputFileName);

            Geometry geomInput1 = null;
            Geometry geomInput2 = null;

            Geometry geom1 = null;
            Geometry geom2 = null;

            Geometry geomPrev = null;

            FileWriter writer = IOUtil.openFile(outputFileName, false);

            geomInput1 = IOUtil.readGeometryLine(sc);

            if (geomInput1 != null) {
                geomInput2 = IOUtil.readGeometryLine(sc);
            }

            int time = 0;

            String geomFile1 = AppConstants.DEFAULT_DIRECTORY + "pol1.tmp";
            String geomFile2 = AppConstants.DEFAULT_DIRECTORY + "pol2.tmp";
            String imageFile = AppConstants.DEFAULT_DIRECTORY + "image.tmp";

            while ((geomInput1 != null) && (geomInput2 != null)) {

                FileWriter writer1 = IOUtil.openFile(geomFile1, false);
                IOUtil.appendLineFile(writer1, geomInput1.toString());
                IOUtil.closeFile(writer1);

                writer1 = IOUtil.openFile(geomFile2, false);
                IOUtil.appendLineFile(writer1, geomInput2.toString());
                IOUtil.closeFile(writer1);

                ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "simplifier", "-p", geomFile1, "-q", geomFile2, "-o", imageFile, "-r", userInputPercentage, "-t", userInputTime);

                pb.directory(new File(AppConstants.DEFAULT_DIRECTORY));

                try {
                    Process p = pb.start();
                    p.waitFor();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                } catch (IOException ex) {
                    Logger.getLogger(SPTDataLabBuilderMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("erro");
                }

                Scanner scP1 = IOUtil.openFileRead(AppConstants.DEFAULT_DIRECTORY + "p1_mas.wkt");
                Scanner scP2 = IOUtil.openFileRead(AppConstants.DEFAULT_DIRECTORY + "p2_mas.wkt");

                geom1 = IOUtil.readGeometryLine(scP1);
                geom2 = IOUtil.readGeometryLine(scP2);

                if (geomPrev != null) {
                    if (geom1.isValid() && geomPrev.isValid()) {
                        geom1 = geom1.union(geomPrev);
                    } else if (geomPrev.isValid()) {
                        geom1 = geomPrev;
                    }

                }

                if (geomInput1.getUserData() != null) {
                    time = Integer.valueOf(geomInput1.getUserData().toString());
                } else {
                    time++;
                }

                IOUtil.closeFile(scP1);
                IOUtil.closeFile(scP2);

                IOUtil.appendLineFile(writer, String.valueOf(time) + ";" + geom1.toString());

                geomPrev = geom2;

                geomInput1 = geomInput2;
                geomInput2 = IOUtil.readGeometryLine(sc);

            }

            if (geom2 != null) {
                IOUtil.appendLineFile(writer, String.valueOf(time) + ";" + geom2.toString());
            }

            IOUtil.closeFile(sc);
            IOUtil.closeFile(writer);

            return IOUtil.computeMetrics(inputFileName, outputFileName, statisticsFileName, statisticsSummaryFileName);

        } catch (ParseException ex) {
            Logger.getLogger(SQLBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
