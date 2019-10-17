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
