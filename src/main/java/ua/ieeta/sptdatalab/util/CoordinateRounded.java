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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import org.locationtech.jts.geom.Coordinate;

/**
 * 
 * Same as coordinate class, but limits the number of decimal places to a maximum of 3
 */
public class CoordinateRounded extends Coordinate{
    
    private double maxDecimalPlaces = 1000d; //3 zeros = 3 decimal places max

    public CoordinateRounded(double x, double y, double z) {
        super(x, y, z);
        this.x = roundDouble(x);
        this.y = roundDouble(y);
        this.z = roundDouble(z);
    }

    public CoordinateRounded() {
        
    }

    public CoordinateRounded(Coordinate c) {
        super(c);
        this.x = roundDouble(c.x);
        this.y = roundDouble(c.y);
        if (!Double.isNaN(c.z)) {
            this.z = roundDouble(c.z);
        }
    }

    public CoordinateRounded(double x, double y) {
        super(x, y);
        this.x = roundDouble(x);
        this.y = roundDouble(y);
    }
    
    private double roundDouble(double d){
        return (double)Math.round(d * maxDecimalPlaces) / maxDecimalPlaces;
    }
}
