package ua.ieeta.sptdatalab.morphing;

/**
 *
 * @author Bruno Silva
 */
public interface InterpolationMethod {
    
    public String atInstant(double instant);
    
    public String[] duringPeriod(double beginPeriod, double endPeriod, int nSamples);
    
}
