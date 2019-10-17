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

import java.io.File;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;

/**
 *
 * Parses a correlation file to return list of coordinates, forming a geometry.
 */
public class CorrToGeometryUtils {
    
    private GeometryReader corrReader;
    
    public CorrToGeometryUtils (File corrFile){
        this.corrReader = new GeometryReader(corrFile);
    }
    
    public CorrToGeometryUtils (String corrFile){
        this.corrReader = new GeometryReader(new File(corrFile));
    }
    
    public List[] getCoordsFromFile(){
        return corrReader.readAndGetPoints(); //contains all points from both geometries (source and target)
    }
    
    public List<Coordinate> getSourceCoordsFromFile(){
        return corrReader.readAndGetPointsOfSource(); //contains all points from source geometry
    }
    
    public List<Coordinate> getTargetCoordsFromFile(){
        return corrReader.readAndGetPointsOfTarget(); //contains all points from source geometry
    }
    
    /*
    //the coordinates read from the file are not closed, and a polygon cannot be formed. 
    //this method copies the first coordinate into the last position in the array
    public Coordinate[] closeCoordinates(Coordinate[] coord){
        Coordinate[] closedCoords = new Coordinate[coord.length + 1];
        closedCoords = coord;
        closedCoords[closedCoords.length-1] = closedCoords[0];
        return closedCoords;
    }*/
    
}
