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
package ua.ieeta.sptdatalab.util.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTWriter;
/*
* Writes to files source and target geometries. Can write to .corr files and .wkt
*/
public class GeometryWriter {
    
    private File file;

    public GeometryWriter(File file) {
        this.file = file;
    }

    public GeometryWriter(String fileName) {
        this.file = new File(fileName);
    }
    
    public boolean writeGeometriesToFile(List<Coordinate> source, List<Coordinate> target) {

        if (this.file.getName().endsWith(".corr")) {
            return writeToCorrFile(source, target);
        } else {
            return writeToWKTFile(source, target);
        }
    }
    
    private boolean writeToWKTFile(List<Coordinate> source, List<Coordinate> target){
        String sourceWKT = coordinateListToWKT(source.toArray(new Coordinate[source.size()]));
        String targetWKT = coordinateListToWKT(target.toArray(new Coordinate[target.size()]));
        try {
            //write to file, first line is wkt source, second line is wkt target
            FileOutputStream fos = new FileOutputStream(this.file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(sourceWKT);
            bw.newLine();
            bw.write(targetWKT);
            bw.flush();
            bw.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(GeometryReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean writeToCorrFile(List<Coordinate> source, List<Coordinate> target){
        if (source.size() == target.size()){
            try {
            //write to file, first line is wkt source, second line is wkt target
            FileOutputStream fos = new FileOutputStream(this.file);
 
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        
                for (int i = 0; i < source.size(); i++){
                    Coordinate sourceCoord = source.get(i);
                    Coordinate targetCoord = target.get(i);
                    bw.write(sourceCoord.getX()+" "+sourceCoord.getY()+" "+targetCoord.getX()+" "+targetCoord.getY());
                    bw.newLine();
                }
                //close geometries, by writing the first coordinate again
                Coordinate sourceCoord = source.get(0);
                Coordinate targetCoord = target.get(0);
                bw.write(sourceCoord.getX()+" "+sourceCoord.getY()+" "+targetCoord.getX()+" "+targetCoord.getY());
                bw.flush();
                bw.close();
                return true;
            } catch (IOException ex) {
                Logger.getLogger(GeometryReader.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return false;
    }
    
    private String coordinateListToWKT(Coordinate[] c){
        GeometryFactory gf = new GeometryFactory();
        Polygon sourcePolygon = null;
        try{
            sourcePolygon = gf.createPolygon(c);
        } catch(IllegalArgumentException e){
            System.out.println(Arrays.toString(c));
            Coordinate[] closedGeometry = new Coordinate[c.length+1];
            closedGeometry = Arrays.copyOf(c, c.length);
            closedGeometry[closedGeometry.length-1] = closedGeometry[0];//close geometry (last point equals first)
            c = closedGeometry;
        }
        sourcePolygon = gf.createPolygon(c);
        WKTWriter wktwriter = new WKTWriter();
        return wktwriter.write(sourcePolygon);
    }
}
