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

public enum Statistics 
{
	AREA_EVOLUTION(1), ROTATION_ANGLES(2), COLLINEAR_POINTS_BY_METHOD(3), QUALITY_MEASURES(4);
	
	private int value;    

	private Statistics(int value) {
		this.value = value;
	}

	public int get_value() {
		return value;
	}
        
        public static String[] getStatStringList(){
            String[] s = {AREA_EVOLUTION.toString(), ROTATION_ANGLES.toString(), COLLINEAR_POINTS_BY_METHOD.toString(), 
                QUALITY_MEASURES.toString()};
            return s;
        }
        
        public static int stringToInt(String statisticString){
            int statistic = 0;
            if(statisticString.equals(AREA_EVOLUTION.toString())){
            statistic = AREA_EVOLUTION.get_value();
            }
            else if(statisticString.equals(ROTATION_ANGLES.toString())){
                statistic = ROTATION_ANGLES.get_value();
            }
            else if(statisticString.equals(COLLINEAR_POINTS_BY_METHOD.toString())){
                statistic = COLLINEAR_POINTS_BY_METHOD.get_value();
            }
            else if(statisticString.equals(QUALITY_MEASURES.toString())){
                statistic = QUALITY_MEASURES.get_value();
            }
            return statistic;
        }
}
