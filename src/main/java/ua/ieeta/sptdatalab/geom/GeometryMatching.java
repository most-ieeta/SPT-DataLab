/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ieeta.sptdatalab.geom;

import java.util.concurrent.Future;
import com.mathworks.engine.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderMenuBar;
import ua.ieeta.sptdatalab.morphing.GuttingMethod;
import ua.ieeta.sptdatalab.util.io.IOUtil;

/**
 *
 * @author Rogerio
 */
public class GeometryMatching {

    public static void executePairMatching(String inputFile1, String inputFile2, String outputFile) throws FileNotFoundException, ParseException {

        try {

            Scanner sc = IOUtil.openFileRead(inputFile1);
            Geometry geom1 = IOUtil.readGeometryLine(sc);
            IOUtil.closeFile(sc);

            double[][] lista1 = new double[geom1.getCoordinates().length][2];

            int i = 0;
            while (i < geom1.getCoordinates().length) {
                lista1[i][0] = geom1.getCoordinates()[i].x;
                lista1[i][1] = geom1.getCoordinates()[i].y;
                i++;
            }

            sc = IOUtil.openFileRead(inputFile2);
            geom1 = IOUtil.readGeometryLine(sc);
            IOUtil.closeFile(sc);

            double[][] lista2 = new double[geom1.getCoordinates().length][2];

            i = 0;
            while (i < geom1.getCoordinates().length) {
                lista2[i][0] = geom1.getCoordinates()[i].x;
                lista2[i][1] = geom1.getCoordinates()[i].y;
                i++;
            }

            Future<MatlabEngine> eng = MatlabEngine.startMatlabAsync();

            MatlabEngine ml = eng.get();
            String path = "'C:/Temp/ACR/aco'";

            ml.evalAsync("cd ".concat(path));

            // Evaluate the function
            Future<double[][]> st;
            st = ml.fevalAsync("calc_and_write_correlations", lista1, lista2);

            while (!(st.isDone())) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GuttingMethod.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            i = 0;
            int j = 0;
            while (i < st.get().length) {

                while (j < st.get()[i].length) {
                    System.out.print(String.valueOf(st.get()[i][j]).concat(" "));
                    j++;
                }
                i++;
                j = 0;
                System.out.println();
            }

            ml.close();

        } catch (ExecutionException ex) {
            Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void executeMatching(String inputFileName, String outputFileName) throws FileNotFoundException, ParseException {

        try {

            Scanner sc = IOUtil.openFileRead(inputFileName);

            long start = System.currentTimeMillis();
            int countMatches = 0;

            Geometry geomInput1 = null;
            Geometry geomInput2 = null;

            FileWriter writer = IOUtil.openFile(outputFileName, false);

            geomInput1 = IOUtil.readGeometryLine(sc);

            if (geomInput1 != null) {
                geomInput2 = IOUtil.readGeometryLine(sc);
            }

            int time = 0;

            while ((geomInput1 != null) && (geomInput2 != null)) {

                double[][] coordinatesArrayFirstGeom = new double[geomInput1.getCoordinates().length][2];

                int i = 0;
                while (i < geomInput1.getCoordinates().length) {
                    coordinatesArrayFirstGeom[i][0] = geomInput1.getCoordinates()[i].x;
                    coordinatesArrayFirstGeom[i][1] = geomInput1.getCoordinates()[i].y;
                    i++;
                }

                double[][] coordinatesArraySecondGeom = new double[geomInput2.getCoordinates().length][2];

                i = 0;
                while (i < geomInput2.getCoordinates().length) {
                    coordinatesArraySecondGeom[i][0] = geomInput2.getCoordinates()[i].x;
                    coordinatesArraySecondGeom[i][1] = geomInput2.getCoordinates()[i].y;
                    i++;
                }

                Future<MatlabEngine> eng = MatlabEngine.startMatlabAsync();

                MatlabEngine ml = eng.get();
                String path = "'C:/Temp/ACR/aco'";

                ml.evalAsync("cd ".concat(path));

                // Evaluate the function
                Future<double[][]> matchingResultArray;
                matchingResultArray = ml.fevalAsync("calc_and_write_correlations", coordinatesArrayFirstGeom, coordinatesArraySecondGeom);

                while (!(matchingResultArray.isDone())) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GuttingMethod.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                i = 0;
                int j = 0;
                while (i < matchingResultArray.get().length) {

                    while (j < matchingResultArray.get()[i].length) {
                        System.out.print(String.valueOf(matchingResultArray.get()[i][j]).concat(" "));
                        j++;
                    }
                    i++;
                    j = 0;
                    System.out.println();
                }

                ml.close();

                if (geomInput1.getUserData() != null) {
                    time = Integer.valueOf(geomInput1.getUserData().toString());
                } else {
                    time++;
                }

                IOUtil.appendLineFile(writer, String.valueOf(time) + ";" + geomInput1.toString());
                countMatches++;

                geomInput1 = geomInput2;
                geomInput2 = IOUtil.readGeometryLine(sc);

            }

            if (geomInput1 != null) {
                IOUtil.appendLineFile(writer, String.valueOf(time) + ";" + geomInput1.toString());
            }

            IOUtil.closeFile(sc);
            IOUtil.closeFile(writer);

            
            System.out.println("Number of matches: " + String.valueOf(countMatches));

            System.out.println("Elapsed time: " + String.valueOf(System.currentTimeMillis() - start));

        } catch (ExecutionException ex) {
            Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
