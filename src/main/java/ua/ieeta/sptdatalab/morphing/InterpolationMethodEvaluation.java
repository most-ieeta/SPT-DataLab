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
package ua.ieeta.sptdatalab.morphing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import static ua.ieeta.sptdatalab.app.SPTDataLab.getMethodEnum;
import ua.ieeta.sptdatalab.model.KeyObservationSelection;
import ua.ieeta.sptdatalab.util.io.GeometrySimilarityCollectionSummary;
import ua.ieeta.sptdatalab.util.io.IOUtil;

public class InterpolationMethodEvaluation {

    public GeometrySimilarityCollectionSummary evaluate(String fullGeometriesDataset, String selectedGeometriesDataset, String outputFileName, String statisticsFileName, InterpolationMethodEnum method) {

        int i = 0;
        int j = i + 1;

        List<String> list = new ArrayList();
        List<String> listJaccard = new ArrayList();

        List<Geometry> geometries;
        List<Geometry> selected;

        GeometrySimilarityCollectionSummary summary = new GeometrySimilarityCollectionSummary();

        String configuration;

        long start = System.currentTimeMillis();

        configuration = "Full file: " + fullGeometriesDataset;

        configuration = configuration + " - Selected geometries " + selectedGeometriesDataset;

        String methodDescription = "";

        if (method == InterpolationMethodEnum.PySpatioTemporalGeom) {
            methodDescription = "PySpatioTemporalGeom";
        }

        if (method == InterpolationMethodEnum.Secondo) {
            methodDescription = "Secondo";
        }

        if (method == InterpolationMethodEnum.SPTMesh) {
            methodDescription = "SPTMesh";
        }

        configuration = configuration + " - Method " + methodDescription;

        try {

            geometries = IOUtil.readWKTList(fullGeometriesDataset);
            selected = IOUtil.readTimeWKTList(selectedGeometriesDataset);

            boolean hasIndex = false;
            if (geometries.get(0).getUserData() != null) {
                hasIndex = true;
            }

            String summaryFileName = outputFileName.replaceFirst("[.][^.]+$", "").concat("JaccardIndex").concat(".txt");

            IOUtil.writeFile(outputFileName, list);
            IOUtil.writeFile(summaryFileName, listJaccard);

            while (j < selected.size()) {

                int samples = Integer.valueOf(selected.get(j).getUserData().toString()) - Integer.valueOf(selected.get(i).getUserData().toString()) - 1;
                if (samples > 0) {
                    Geometry geom1 = selected.get(i);
                    Geometry geom2 = selected.get(j);
                    InterpolationMethodFacade interpolation = new InterpolationMethodFacade(geom1.toString(), geom2.toString(), 0.0, 1000.0);

                    if (geom1.isValid() && geom2.isValid()) {

                        System.out.println("Dados interpolacao");
                        System.out.println(selected.get(j).getUserData().toString());
                        System.out.println(selected.get(i).getUserData().toString());
                        System.out.println(Integer.valueOf(samples));

                        String[] interpolationsResults = interpolation.interpolationDuringPeriod(0.0, 1000.0, samples, method);
                        if (!(interpolationsResults == null)) {

                            int k = 0;
                            while (k < samples) {

                                System.out.println(String.valueOf("Dados"));
                                System.out.println(String.valueOf(i));

                                int sampleId = Integer.valueOf((selected.get(i).getUserData().toString()));
                                sampleId = sampleId + k + 1;

                                System.out.println(String.valueOf("K"));
                                System.out.println(String.valueOf(k));
                                System.out.println(String.valueOf(sampleId));

                                if (interpolationsResults.length >= k + 1) {

                                    if (interpolationsResults[k] != null) {

                                        System.out.println(String.valueOf("results"));
                                        System.out.println(String.valueOf(interpolationsResults[k]));

                                        WKTReader reader = new WKTReader();

                                        Geometry geomResult = null;
                                        try {
                                            geomResult = reader.read(interpolationsResults[k]);

                                        } catch (Exception e) {
                                            System.out.println("error in parsing k =" + String.valueOf(k));
                                        }
                                        //if (!geomResult.isValid())
                                        //    geomResult = JTS.makeValid(geomResult, false);
                                        //        
                                        if (!(geomResult == null)) {

                                            boolean found = false;
                                            int indexFullSet = 0;

                                            if (hasIndex) {
                                                while (indexFullSet <= sampleId && indexFullSet < geometries.size() && (found == false)) {
                                                    if (Integer.valueOf(geometries.get(indexFullSet).getUserData().toString()) == sampleId) {
                                                        found = true;
                                                    } else {
                                                        indexFullSet++;
                                                    }
                                                }
                                                if (found) {
                                                    summary.addGeometries(geomResult, geometries.get(indexFullSet));

                                                    if ((geomResult.isValid()) && (geometries.get(indexFullSet).isValid())) {
                                                        listJaccard.add(String.valueOf(sampleId).concat(";").concat(String.valueOf(summary.getJaccardIndex())));
                                                    } else {
                                                        listJaccard.add(String.valueOf(sampleId).concat(";"));
                                                    }

                                                } else {
                                                    listJaccard.add(String.valueOf(sampleId).concat(";"));
                                                }

                                            } else {
                                                summary.addGeometries(geomResult, geometries.get(sampleId));

                                                if ((geomResult.isValid()) && (geometries.get(sampleId).isValid())) {
                                                    listJaccard.add(String.valueOf(sampleId).concat(";").concat(String.valueOf(summary.getJaccardIndex())));
                                                } else {
                                                    listJaccard.add(String.valueOf(sampleId).concat(";"));
                                                }

                                            }

                                            list.add(String.valueOf(sampleId).concat(";").concat(interpolationsResults[k]));

                                        }
                                    }
                                }
                                k++;
                            }
                        }

                    }

                    IOUtil.appendFile(outputFileName, list);

                    list.removeAll(list);

                    IOUtil.appendFile(summaryFileName, listJaccard);

                    listJaccard.removeAll(listJaccard);
                }
                i++;
                j++;
            }

            configuration = configuration + " - Elapsed time: " + String.valueOf(System.currentTimeMillis() - start);

            summary.setParameterData(configuration);

            IOUtil.writeSummary(statisticsFileName, summary.getSummary());

        } catch (ParseException | IOException ex) {
            Logger.getLogger(InterpolationMethodEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return summary;

    }

}
