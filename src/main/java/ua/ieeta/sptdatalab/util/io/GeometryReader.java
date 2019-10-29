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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import ua.ieeta.sptdatalab.app.AppConstants;

/**
 *
 * Reads/writes coordinates of geometries from/to wkt or .corr files.
 */
public class GeometryReader {

    private File file;

    public GeometryReader(File file) {
        this.file = file;
    }

    public GeometryReader(String fileName) {
        this.file = new File(fileName);
    }

    //reads the coordinates of both source and target from file and parses them into a list of Coordinates
    //Returns an array containing a list of coordinates from the source geometry as the first and a
    //another list with list of coordinates from target geometry as the second element
    public List[] readAndGetPoints() {

        return readAndGetPointsWKTFile();
        
    }


    public List[] readAndGetPointsWKTFile() {

        try {
            return new List[]{readAndGetGeometryWKTFile(true), readAndGetGeometryWKTFile(false)};
        } catch (ParseException ex) {
            Logger.getLogger(GeometryReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }


    private List<Coordinate> readAndGetGeometryWKTFile(boolean source) throws ParseException {
        List<Coordinate> coordinatesSource = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(this.file))) {

            String line;
            WKTReader wktreader = new WKTReader();
            Geometry geom;

            if ((line = br.readLine()) != null) {
                if (!(source)) {
                    line = br.readLine();
                }
            }
            geom = wktreader.read(line);
            for (int i = 0; i < geom.getCoordinates().length; i++) {
                Coordinate cRounded = geom.getCoordinates()[i];
                cRounded.setX(AppConstants.limitMaxNumberOfDecimalPlaces(cRounded.getX()));
                cRounded.setY(AppConstants.limitMaxNumberOfDecimalPlaces(cRounded.getY()));
                coordinatesSource.add(cRounded);
            }
        } catch (IOException ex) {
            Logger.getLogger(GeometryReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        //validate coordinates by creating a temporary geometry
        coordinatesSource = validateCoordinates(coordinatesSource);
        return coordinatesSource;

    }

    //reads the coordinates from file and parses the into a list of Coordinates
    public List<Coordinate> readAndGetPointsOfSource() {

            try {
                return readAndGetGeometryWKTFile(true);
            } catch (ParseException ex) {
                Logger.getLogger(GeometryReader.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

    }

    //reads the coordinates from file and parses the into a list of Coordinates
    public List<Coordinate> readAndGetPointsOfTarget() {

            try {
                return readAndGetGeometryWKTFile(false);
            } catch (ParseException ex) {
                Logger.getLogger(GeometryReader.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

    }

    public List<Coordinate> validateCoordinates(List<Coordinate> coords) {
        try {
            new GeometryFactory().createPolygon(coords.toArray(new Coordinate[coords.size()]));
        } catch (IllegalArgumentException e) {
            //geometry not closed
            List<Coordinate> coordsValid = coords;
            if (!(coordsValid.get(0) == coordsValid.get(coordsValid.size() - 1))) {
                coordsValid.add(coordsValid.get(0));//close geometry
            }
            return coordsValid;
        }
        return coords;
    }
    
}
