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

import java.text.DecimalFormat;
import org.locationtech.jts.geom.Coordinate;

public class CoordinateUtils extends Coordinate{
    
    public CoordinateUtils (double x, double y){
        super(x, y);
    }
    
    //used to transform this point from the original image to the size of the window the image is in
    public Coordinate transform (double originalWidth, double originalHeight, double panelWidth, double panelHeight){
        double transformedX = getCoordinateConversion(this.getX(), originalWidth, panelWidth);
        double transformedY = getCoordinateConversion(originalHeight - this.getY(), originalHeight, panelHeight);
        return new Coordinate(transformedX, transformedY);
    }
    
    public void transformCoords (double originalWidth, double originalHeight, double panelWidth, double panelHeight){
        this.setX(getCoordinateConversion(this.getX(), originalWidth, panelWidth));
        this.setY(getCoordinateConversion(originalHeight - this.getY(), originalHeight, panelHeight));
    }
    
    public void transformOriginal (double originalWidth, double originalHeight, double panelWidth, double panelHeight){
        this.setX(getCoordinateOriginal(this.getX(), originalWidth, panelWidth));
        this.setY(getCoordinateOriginal(this.getY(), originalHeight, panelHeight));
        //remove original height from original coordinates!
        this.setY(originalHeight - this.getY() );
    }
    
    private double getCoordinateConversion(double c, double originalPanelSize, double panelSize){
        return (c/originalPanelSize) * panelSize;
    }
    
    private double getCoordinateOriginal(double c, double originalPanelSize, double panelSize){
        return (c *originalPanelSize)/panelSize;
    }
    
    public void translate(Coordinate coord){
        this.x+=coord.getX();
        this.y+=coord.getY();
    }
    
    @Override
    public void setX(double x){
        DecimalFormat df = new DecimalFormat("#.###");
        df.format(x);
        this.x = x;
    }
    
    @Override
    public void setY(double y){
        DecimalFormat df = new DecimalFormat("#.###");
        df.format(y);
        this.y = y;
    }
}
