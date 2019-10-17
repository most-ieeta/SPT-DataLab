package ua.ieeta.sptdatalab.morphing;

/**
 *
 * @author Bruno Silva
 */
public enum InterpolationMethodEnum {
    
    SPTMesh, PySpatioTemporalGeom, Secondo;
    
    public static String[] getMethodsStringList(){
        String[] s = {SPTMesh.toString(), PySpatioTemporalGeom.toString(), Secondo.toString()};
        return s;
    }
}
