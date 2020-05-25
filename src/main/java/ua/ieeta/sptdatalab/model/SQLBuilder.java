
/* This file is part of SPT Data Lab.
*
* Copyright (C) 2020, University of Aveiro,
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
package ua.ieeta.sptdatalab.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import ua.ieeta.sptdatalab.util.io.IOUtil;

public class SQLBuilder {

    public int createSQLDiscreteModel(String sourcefilename, String destfilename, String tableName, String seriesName, boolean includeDelete) throws IOException {

        int i = 0;

        try {
            List<Geometry> listGeom = IOUtil.readWKTList(sourcefilename);
            
            FileWriter writer = IOUtil.openFile(destfilename, false);

            if (includeDelete) {
                IOUtil.appendLineFile(writer, "delete from " + tableName + " where name = '" + seriesName + "';");
            }

            while (i < listGeom.size()) {
                if (listGeom.get(i).getUserData() == null) {
                    IOUtil.appendLineFile(writer, "insert into " + tableName + " (name, time, object) values ('" + seriesName + "'," + Integer.toString(i) + ",'" + listGeom.get(i).toString() + "');");
                } else {
                    IOUtil.appendLineFile(writer, "insert into " + tableName + " (name, time, object) values ('" + seriesName + "'," + listGeom.get(i).getUserData().toString() + ",'" + listGeom.get(i).toString() + "');");
                }

                i++;

            }

            IOUtil.closeFile(writer);
            
        } catch (ParseException ex) {
            Logger.getLogger(SQLBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return i;
    }

    public int createSQLContinuousModel(String sourcefilename, String destfilename, String tableName, String seriesName, boolean includeDelete) throws IOException {

        int i = 0;
            
        try {
            
            Scanner sc = IOUtil.openFileRead(sourcefilename);
            
            Geometry geom = null;
            Geometry geom2 = null;

            FileWriter writer = IOUtil.openFile(destfilename, false);

            if (includeDelete) {
                IOUtil.appendLineFile(writer, "delete from " + tableName + " where name = '" + seriesName + "';");
            }

            geom = IOUtil.readGeometryLine(sc);
            if (geom!=null)
                geom2 = IOUtil.readGeometryLine(sc);
            while ((geom!=null) && (geom2!=null)) {
                
                String time = "(" + geom.getUserData().toString() + " " + geom2.getUserData().toString() + ", ";
                String coordinates = "(";

                for (Coordinate c : geom.getCoordinates()) {
                    coordinates = coordinates.concat(String.valueOf(c.x)).concat(" ").concat(String.valueOf(c.y).concat(","));
                }
                coordinates = coordinates.substring(0, coordinates.length() - 1).concat("), (");

                for (Coordinate c : geom2.getCoordinates()) {
                    coordinates = coordinates.concat(String.valueOf(c.x)).concat(" ").concat(String.valueOf(c.y).concat(","));
                }

                coordinates = coordinates.substring(0, coordinates.length() - 1).concat("))");

                IOUtil.appendLineFile(writer, "insert into " + tableName + " (name, mobject) values ('" + seriesName + "',ST_MovingMesh_FromSTWKT('MOVINGMESH(" + time + coordinates + ")'));");
             
                geom = geom2;
                geom = IOUtil.readGeometryLine(sc);
                
                i++;
            }

            IOUtil.closeFile(writer);
            IOUtil.closeFile(sc);
    
        } catch (ParseException ex) {
            Logger.getLogger(SQLBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return i;
    }

}
