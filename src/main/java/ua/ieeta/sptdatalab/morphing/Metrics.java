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

package ua.ieeta.sptdatalab.morphing;

import jni_st_mesh.Main;
import java.util.HashMap;
import java.util.Map;
import ua.ieeta.sptdatalab.app.AppStrings;

/**
 *
 * @author Bruno Silva
 * Performs quality metrics in a geometry or similarity metrics between 2 geometries
 */
public class Metrics {
    private Main m;
    
    public Metrics(){
        System.loadLibrary(AppStrings.DLL_LIBRARY);
        m = new Main();
    }
    
    public double computeMetric(String geo, MetricsEnum metric){
        return m.numerical_metric(geo, metric.get_value());
    }
    
    public double computeSimilarityMetric(String geo1, String geo2, SimilarityMetricsEnum metric){
        return m.compare_geometries(geo1, geo2, metric.get_value());
    }
    public Map<String, Double> computeMetricForMultipleObservations(String[] geo, MetricsEnum metric){
        Map<String, Double> metricsObservations = new HashMap<>();
        for(int j = 0; j < geo.length; j++)
                metricsObservations.put(String.valueOf(j), m.numerical_metric(geo[j], metric.get_value()));
        return metricsObservations;
    }
    public Map<String, Double> computeSimilarityMetricForMultipleObservations(String[] geo1, String geo2, SimilarityMetricsEnum metric){
        Map<String, Double> metricsObservations = new HashMap<>();
        for(int j = 0; j < geo1.length; j++)
                metricsObservations.put(String.valueOf(j), m.compare_geometries(geo1[j], geo2, metric.get_value()));
        return metricsObservations; 
    }
    
}
