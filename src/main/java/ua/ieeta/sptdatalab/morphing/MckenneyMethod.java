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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Given a source and a target, a begin and end time, a query time, and a morphing method, calls appropriate libraries to
 * to perform the morphing operations using the provided input. This class acts as facade to call morphing methos from multiple
 * libraries
 */
public class MckenneyMethod implements InterpolationMethod{
    private String sourceGeometry;
    private String targetGeometry;
    private double beginTime;
    private double endTime;
    
    public MckenneyMethod(String geometry1, String geometry2, double beginTime, double endTime) {
        this.sourceGeometry = geometry1;
        this.targetGeometry = geometry2;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
    
    
    /**
     * @param queryTime - must not be lower than beginTime and not higher than endTime
     * @return
     */
    public String atInstant (double queryTime){
        String sep = System.getProperty("file.separator");
        //call python script
        ProcessBuilder pb = new ProcessBuilder("python","pyspatiotemporalgeom" + sep + "mckenney_final.py", beginTime+"", endTime+"", sourceGeometry, targetGeometry, queryTime+"");
        String wkt = "";
        try {
            Process p = pb.start();
            BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
            //the output will be a wkt representing the geometry at the specified instant
            wkt = reader.readLine();
            //System.out.println(wkt);
            
        } catch (IOException ex) {
            Logger.getLogger(MckenneyMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (wkt != null && !wkt.isEmpty() && !wkt.contains("Error")){
            return wkt;
        }
        //System.out.println(p.toText());
        return null;
    }
    
    /**
     * Calls a python script and passes as input the time and geometries for the morphing operation using mckeeney algorithm.
     * @param queryTimeBegin
     * @param queryTimeEnd
     * @param numSamples
     * @return wkt list
     */
    public String[] duringPeriod (double queryTimeBegin, double queryTimeEnd, int numSamples){
        List<String> wkts = new ArrayList<>();
        String sep = System.getProperty("file.separator");
        //call python script
        //parameters for during period are: begin time, end time, source geo, target geo, query time begin, query time end, num samples
        
        
        if (numSamples == 0)
            return null;
        
        ProcessBuilder pb = new ProcessBuilder("python","pyspatiotemporalgeom" + sep + "mckenney_final.py", beginTime+"", endTime+"", sourceGeometry, targetGeometry, queryTimeBegin+"", queryTimeEnd+"", numSamples+"");
        String line;
        try {
            Process p = pb.start();
            BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            while ((line = reader.readLine())!= null) {
                //System.out.println(line);
                wkts.add(line);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(MckenneyMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] wkt = new String[wkts.size()];
        wkt = wkts.toArray(wkt);
        
        //receive morphing result. It's a list of line segment format, so we must convert it to wkt
        return wkt;
    }
    
}
