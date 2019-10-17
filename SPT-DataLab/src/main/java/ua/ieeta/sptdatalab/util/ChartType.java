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

/**
 *
 * @author Bruno Silva
 */
public enum ChartType {
    LINE_CHART("Line Chart"), TABLE("Table");
	
    private String value;    

    private ChartType(String value) {
            this.value = value;
    }

    public String getValue() {
            return value;
    }
    
    public static String[] getChartTypeList(){
            String[] s = {LINE_CHART.getValue(), TABLE.getValue()};
            return s;
    }
}
