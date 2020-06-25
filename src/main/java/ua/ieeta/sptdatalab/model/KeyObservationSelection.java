
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import ua.ieeta.sptdatalab.util.io.GeometryCollectionSummary;
import ua.ieeta.sptdatalab.util.io.IOUtil;
import ua.ieeta.sptdatalab.geom.GeometryUtil;
        
public class KeyObservationSelection {


    public GeometryCollectionSummary distanceBasedObservationIntervals(String inputFileName, String outputFileName, String statisticsFileName, Double limit) throws IOException {

        GeometryCollectionSummary summary = new GeometryCollectionSummary();

        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + inputFileName;

        configuration = configuration + " - Select intervals - distance based (statistics after select) - Limit " + String.valueOf(limit);

        try {
            List<Geometry> listGeom = IOUtil.readWKTList(inputFileName);
            List<String> listSelected = new ArrayList<>();

            int i = 0;
            int j = i + 1;

            double jaccard_distance;
            boolean hasIndex = false;

            if (listGeom.get(i).getUserData() != null) {
                hasIndex = true;
            }

            while (j < listGeom.size()) {

                if (listGeom.get(i).isValid() && listGeom.get(j).isValid()) {

                    jaccard_distance = GeometryUtil.JaccardDistance(listGeom.get(i), listGeom.get(j));
                    if (jaccard_distance > limit) {

                        if (hasIndex) {
                            listSelected.add(listGeom.get(i).getUserData().toString().concat(";").concat(listGeom.get(j-1).getUserData().toString()));
                        } else {
                            listSelected.add(String.valueOf(i).concat(";").concat(String.valueOf(j-1)));
                        }

                        summary.addGeometry(listGeom.get(i));
                        if (i != j - 1) {
                            summary.addGeometry(listGeom.get(j - 1));
                        }
                        i = j;
                    }
                }
                j++;
            }

            if (hasIndex) {
                 listSelected.add(listGeom.get(i).getUserData().toString().concat(";").concat(listGeom.get(j-1).getUserData().toString()));
            } else {
                listSelected.add(String.valueOf(i).concat(";").concat(String.valueOf(j-1)));
            }

            summary.addGeometry(listGeom.get(i));

            if (i != j - 1) {
                summary.addGeometry(listGeom.get(j - 1));
            }

            IOUtil.writeFile(outputFileName, listSelected);

            configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

            summary.setParameterData(configuration);

            IOUtil.writeSummary(statisticsFileName, summary.getSummary());

        } catch (ParseException ex) {
            Logger.getLogger(SQLBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return summary;

    }

    
    public GeometryCollectionSummary distanceBasedObservationSelection(String inputFileName, String outputFileName, String statisticsFileName, Double limit) throws IOException {

        GeometryCollectionSummary summary = new GeometryCollectionSummary();

        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + inputFileName;

        configuration = configuration + " - Select key obs - distance based (statistics after select) - Limit " + String.valueOf(limit);

        try {
            List<Geometry> listGeom = IOUtil.readWKTList(inputFileName);
            List<String> listSelected = new ArrayList<>();

            int i = 0;
            int j = i + 1;

            double jaccard_distance;
            boolean hasIndex = false;

            if (listGeom.get(i).getUserData() != null) {
                hasIndex = true;
            }

            while (j < listGeom.size()) {

                if (listGeom.get(i).isValid() && listGeom.get(j).isValid()) {

                    jaccard_distance = GeometryUtil.JaccardDistance(listGeom.get(i), listGeom.get(j));
                    if (jaccard_distance > limit) {

                        if (hasIndex) {
                            listSelected.add(listGeom.get(i).getUserData().toString().concat(";").concat(listGeom.get(i).toString()));
                        } else {
                            listSelected.add(String.valueOf(i).concat(";").concat(listGeom.get(i).toString()));
                        }

                        summary.addGeometry(listGeom.get(i));
                        if (i != j - 1) {

                            if (hasIndex) {
                                listSelected.add(listGeom.get(j - 1).getUserData().toString().concat(";").concat(listGeom.get(j - 1).toString()));
                            } else {
                                listSelected.add(String.valueOf(j - 1).concat(";").concat(listGeom.get(j - 1).toString()));
                            }

                            summary.addGeometry(listGeom.get(j - 1));
                        }
                        i = j;
                    }
                }
                j++;
            }

            if (hasIndex) {
                listSelected.add(listGeom.get(i).getUserData().toString().concat(";").concat(listGeom.get(i).toString()));                
            } else {
                listSelected.add(String.valueOf(i).concat(";").concat(listGeom.get(i).toString()));
            }

            summary.addGeometry(listGeom.get(i));

            if (i != j - 1) {
                if (hasIndex) {
                    listSelected.add(listGeom.get(j - 1).getUserData().toString().concat(";").concat(listGeom.get(j - 1).toString()));
                } else {
                    listSelected.add(String.valueOf(j - 1).concat(";").concat(listGeom.get(j - 1).toString()));
                }
                summary.addGeometry(listGeom.get(j - 1));
            }

            IOUtil.writeFile(outputFileName, listSelected);

            configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

            summary.setParameterData(configuration);

            IOUtil.writeSummary(statisticsFileName, summary.getSummary());

        } catch (ParseException ex) {
            Logger.getLogger(SQLBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return summary;

    }

    public GeometryCollectionSummary fixedSizeObservationSelection(String inputFileName, String outputFileName, String statisticsFileName, Integer size) throws IOException {

        GeometryCollectionSummary summary = new GeometryCollectionSummary();

        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Input file: " + inputFileName;

        configuration = configuration + " - Select key obs - fixed size (statistics after select) - Size " + String.valueOf(size);

        try {
            List<Geometry> listGeom = IOUtil.readWKTList(inputFileName);
            List<String> listSelected = new ArrayList<>();

            if (listGeom.size() > 2) {

                int i = 0;
                boolean hasIndex = false;

                if (listGeom.get(i).getUserData() != null) {
                    hasIndex = true;
                }

                while (i < listGeom.size() - 1) {

                    if (!(hasIndex)) {
                        listSelected.add(String.valueOf(i).concat(";").concat(listGeom.get(i).toString()));
                    } else {
                        listSelected.add(listGeom.get(i).getUserData().toString().concat(";").concat(listGeom.get(i).toString()));
                    }

                    summary.addGeometry(listGeom.get(i));

                    if (!(hasIndex)) {
                        i = i + size;
                    } else {
                        int lastIndex = Integer.valueOf(listGeom.get(i).getUserData().toString());
                        while ((i < listGeom.size() - 1) && (Integer.valueOf(listGeom.get(i).getUserData().toString()) < lastIndex + size)) {
                            i++;
                        }
                    }
                }

                if (!(hasIndex)) {
                    listSelected.add(String.valueOf(listGeom.size() - 1).concat(";").concat(listGeom.get(listGeom.size() - 1).toString()));
                } else {
                    listSelected.add(listGeom.get(listGeom.size() - 1).getUserData().toString().concat(";").concat(listGeom.get(listGeom.size() - 1).toString()));
                }

                summary.addGeometry(listGeom.get(listGeom.size() - 1));

                IOUtil.writeFile(outputFileName, listSelected);

                configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

                summary.setParameterData(configuration);

                IOUtil.writeSummary(statisticsFileName, summary.getSummary());

            }

        } catch (ParseException ex) {
            Logger.getLogger(SQLBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return summary;

    }

}
