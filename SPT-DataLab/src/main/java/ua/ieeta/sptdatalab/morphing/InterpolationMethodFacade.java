package ua.ieeta.sptdatalab.morphing;

import javax.swing.JOptionPane;

/**
 *
 * @author Bruno Silva
 * 
 * Intermediary class that acts as a facade and allows the realization of interpolation operations using 3 different libraries:
 * JDuarte (IEETA - UA), Mckenney and Gutting
 */
public class InterpolationMethodFacade {
    private String sourceGeometry;
    private String targetGeometry;
    private double beginTime;
    private double endTime;
    
    private JDuarteMethod jdMethod;
    private MckenneyMethod mckMethod;
    private GuttingMethod gutMethod;

    public InterpolationMethodFacade(String sourceGeometry, String targetGeometry, double beginTime, double endTime) {
        this.sourceGeometry = sourceGeometry;
        this.targetGeometry = targetGeometry;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
    
    public String interpolationAtInstant(double instant, InterpolationMethodEnum interpolationMethod){
        String result = null;
        switch(interpolationMethod){
            case SPTMesh:
                jdMethod = new JDuarteMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = jdMethod.atInstant(instant);
                break;
            case PySpatioTemporalGeom:
                mckMethod = new MckenneyMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = mckMethod.atInstant(instant);
                break;
            case Secondo:
                gutMethod = new GuttingMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = gutMethod.atInstant(instant);
                break;
        }
        return result;
    }
    
    public String interpolationAtInstant(double instant, InterpolationMethodEnum interpolationMethod, 
            int triangulationMethod, boolean cw, double threshold){
        //aditional parameters only for JDuarte Algorithm
        String result = null;
        switch(interpolationMethod){
            case SPTMesh:
                jdMethod = new JDuarteMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = jdMethod.atInstant(instant, triangulationMethod, cw, threshold);
                break;
            case PySpatioTemporalGeom:
                mckMethod = new MckenneyMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = mckMethod.atInstant(instant);
                break;
            case Secondo:
                gutMethod = new GuttingMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = gutMethod.atInstant(instant);
                break;
        }
        return result;
    }
    
    //mesh representation of geometries valid only for JDuarte algorithm
    public String interpolationAtInstantMesh(double instant, InterpolationMethodEnum interpolationMethod,
            int triangulationMethod, boolean cw, double threshold){
        
        String result = null;
        switch(interpolationMethod){
            case SPTMesh:
                jdMethod = new JDuarteMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = jdMethod.atInstantMesh(instant, triangulationMethod, cw, threshold);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Only JDuarte supports a representation of geometries using mesh.",
                "Invalid interpolation method", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    public String[] interpolationDuringPeriod(double beginTime, double endTime, int nSamples, InterpolationMethodEnum interpolationMethod){
        String[] result = null;
        switch(interpolationMethod){
            case SPTMesh:
                jdMethod = new JDuarteMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = jdMethod.duringPeriod(beginTime, endTime, nSamples);
                break;
            case PySpatioTemporalGeom:
                mckMethod = new MckenneyMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = mckMethod.duringPeriod(beginTime, endTime, nSamples);
                break;
            case Secondo:
                gutMethod = new GuttingMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = gutMethod.duringPeriod(beginTime, endTime, nSamples);
                break;
        }
        return result;
    }
    
    public String[] interpolationDuringPeriod(double beginTime, double endTime, int nSamples, 
            InterpolationMethodEnum interpolationMethod, int triangulationMethod, boolean cw, double threshold){
        String[] result = null;
        switch(interpolationMethod){
            case SPTMesh:
                //only JDuarte method accepts these aditional parameters
                jdMethod = new JDuarteMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = jdMethod.duringPeriod(beginTime, endTime, nSamples, triangulationMethod, cw, threshold);
                break;
            case PySpatioTemporalGeom:
                mckMethod = new MckenneyMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = mckMethod.duringPeriod(beginTime, endTime, nSamples);
                break;
            case Secondo:
                gutMethod = new GuttingMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = gutMethod.duringPeriod(beginTime, endTime, nSamples);
                break;
        }
        return result;
    }
    
    //mesh only for JDuarte algorithm
    public String[] interpolationDuringPeriodMesh(double beginTime, double endTime, int nSamples, 
            InterpolationMethodEnum interpolationMethod, int triangulationMethod, boolean cw, double threshold){
        String[] result = null;
        switch(interpolationMethod){
            case SPTMesh:
                jdMethod = new JDuarteMethod(sourceGeometry, targetGeometry, beginTime, endTime);
                result = jdMethod.duringPeriodMesh(beginTime, endTime, nSamples, triangulationMethod, cw, threshold);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Only JDuarte supports a representation of geometries using mesh.",
                "Invalid interpolation method", JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    
}
