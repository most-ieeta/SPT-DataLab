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
package ua.ieeta.sptdatalab.geom;

import java.util.concurrent.Future;
import com.mathworks.engine.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import ua.ieeta.sptdatalab.app.AppConstants;
import ua.ieeta.sptdatalab.util.io.IOUtil;


public class GeometryMatching {

    public static void executePairMatching(String inputFile1, String inputFile2, String outputFile) throws FileNotFoundException, ParseException {

        try {

            Scanner sc = IOUtil.openFileRead(inputFile1);
            Geometry geom1 = IOUtil.readGeometryLine(sc);
            IOUtil.closeFile(sc);

            double[][] coordinatesList1 = new double[geom1.getCoordinates().length][2];

            int i = 0;
            while (i < geom1.getCoordinates().length) {
                coordinatesList1[i][0] = geom1.getCoordinates()[i].x;
                coordinatesList1[i][1] = geom1.getCoordinates()[i].y;
                i++;
            }

            sc = IOUtil.openFileRead(inputFile2);
            geom1 = IOUtil.readGeometryLine(sc);
            IOUtil.closeFile(sc);

            double[][] coordinatesList2 = new double[geom1.getCoordinates().length][2];

            i = 0;
            while (i < geom1.getCoordinates().length) {
                coordinatesList2[i][0] = geom1.getCoordinates()[i].x;
                coordinatesList2[i][1] = geom1.getCoordinates()[i].y;
                i++;
            }

            Future<MatlabEngine> eng = MatlabEngine.startMatlabAsync();

            MatlabEngine ml = eng.get();
            String path = AppConstants.DEFAULT_DIRECTORY + "aco";

            ml.evalAsync("cd ".concat(path));

            // Evaluate the function
            Future<double[][]> st;
            st = ml.fevalAsync("calc_and_write_sptdatalab", coordinatesList1, coordinatesList2);

            while (!(st.isDone())) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
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

        } catch (ExecutionException | InterruptedException | IOException ex) {
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
                String path = AppConstants.DEFAULT_DIRECTORY + "aco";

                ml.evalAsync("cd ".concat(path));

                // Evaluate the function
                Future<double[][]> matchingResultArray;
                matchingResultArray = ml.fevalAsync("calc_and_write_sptdatalab", coordinatesArrayFirstGeom, coordinatesArraySecondGeom);

                while (!(matchingResultArray.isDone())) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
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
                if (geomInput1.getUserData() != null) {
                    time = Integer.valueOf(geomInput1.getUserData().toString());
                } else {
                    time++;
                }
                IOUtil.appendLineFile(writer, String.valueOf(time) + ";" + geomInput1.toString());
            }

            IOUtil.closeFile(sc);
            IOUtil.closeFile(writer);

            
            System.out.println("Number of matches: " + String.valueOf(countMatches));

            System.out.println("Elapsed time: " + String.valueOf(System.currentTimeMillis() - start));

        } catch (ExecutionException | InterruptedException | IOException ex) {
            Logger.getLogger(GeometryMatching.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
