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
import ua.ieeta.sptdatalab.app.AppStrings;

/**
 *
 * @author Bruno Silva
 */
public class JDuarteMethod implements InterpolationMethod{
    private String sourceGeometry;
    private String targetGeometry;
    private double beginTime;
    private double endTime;
    private Main m;
    
    public JDuarteMethod(String geometry1, String geometry2, double beginTime, double endTime) {
        this.sourceGeometry = geometry1;
        this.targetGeometry = geometry2;
        this.beginTime = beginTime;
        this.endTime = endTime;
        //load c++ libraries
        System.loadLibrary(AppStrings.DLL_LIBRARY);
        m = new Main();
    }
    
    @Override
    public String atInstant(double instant) {
        return m.at_instant_poly(beginTime, endTime, sourceGeometry, targetGeometry, 
                        instant, TriangulationMethod.COMPATIBLE.get_value(), true, 0.5);//default values
    }
    
    public String atInstant(double instant, int triangulationMethod, boolean cw, double threshold) {
        return m.at_instant_poly(beginTime, endTime, sourceGeometry, targetGeometry, 
                        instant, triangulationMethod, cw, threshold);
    }
    
    public String atInstantMesh(double instant, int triangulationMethod, boolean cw, double threshold) {
        return m.at_instant_mesh(beginTime, endTime, sourceGeometry, targetGeometry, 
                        instant, triangulationMethod, cw, threshold);
    }

    @Override
    public String[] duringPeriod(double beginPeriod, double endPeriod, int nSamples) {
        return m.during_period_poly(beginTime, endTime, sourceGeometry, targetGeometry, beginPeriod, 
                endPeriod, nSamples, TriangulationMethod.COMPATIBLE.get_value(), true, 0.5, 0.0);
    }
    
    public String[] duringPeriod(double beginPeriod, double endPeriod, int nSamples, 
                                int triangulationMethod, boolean cw, double threshold) {
        return m.during_period_poly(beginTime, endTime, sourceGeometry, targetGeometry, beginPeriod, 
                endPeriod, nSamples, triangulationMethod, cw, threshold, 0.0);
    }
    
    public String[] duringPeriodMesh(double beginPeriod, double endPeriod, int nSamples, 
                                int triangulationMethod, boolean cw, double threshold) {
        return m.during_period_mesh(beginTime, endTime, sourceGeometry, targetGeometry, beginPeriod, 
                endPeriod, nSamples, triangulationMethod, cw, threshold);
    }
    
}
