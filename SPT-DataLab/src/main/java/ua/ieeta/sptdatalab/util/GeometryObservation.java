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

package ua.ieeta.sptdatalab.util;

import java.awt.Dimension;
import java.io.File;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;

/*
    Represents a period of observation in geometries, with source and target geometries, and number of observation
*/
public class GeometryObservation {
    
    private int observationNumber;
    
    private List<Coordinate> source;
    
    private List<Coordinate> target;
    
    private File observationFile;
    
    public GeometryObservation(int observationNumber, List<Coordinate> source, List<Coordinate> target) {
        this.observationNumber = observationNumber;
        this.source = source;
        this.target = target;
    }
    
    public GeometryObservation setObservation(int observationNumber, List<Coordinate> source, List<Coordinate> target) {
        this.observationNumber = observationNumber;
        this.source = source;
        this.target = target;
        return this;
    }
    
    public int getObservationNumber() {
        return observationNumber;
    }

    public void setObservationNumber(int observationNumber) {
        this.observationNumber = observationNumber;
    }

    public List<Coordinate> getSource() {
        return source;
    }

    public void setSource(List<Coordinate> source) {
        this.source = source;
    }

    public List<Coordinate> getTarget() {
        return target;
    }

    public void setTarget(List<Coordinate> target) {
        this.target = target;
    }
    
    public void setGeometryCoordinates(List<Coordinate> coords, boolean isSource){
        if (isSource)
            this.source = coords;
        else
            this.target = coords;
    }
    
    public List<Coordinate> getGeometryCoordinates(boolean isSource) {
        if (isSource)
            return source;
        return target;
    }

    public File getObservationFile() {
        return observationFile;
    }

    public GeometryObservation setObservationFile(File observationFile) {
        this.observationFile = observationFile;
        return this;
    }
}
