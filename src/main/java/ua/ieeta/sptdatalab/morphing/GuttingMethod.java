package ua.ieeta.sptdatalab.morphing;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import sj.lang.ESInterface;
import sj.lang.IntByReference;
import sj.lang.ListExpr;
import ua.ieeta.sptdatalab.app.AppConstants;


public class GuttingMethod implements InterpolationMethod{
    
    //secondo db credentials
    private String databaseName;
    private String objectName;
    private String host;
    private int port;
    
    private static ESInterface Secondointerface = new ESInterface();
    
    private String sourceGeometry;
    private String targetGeometry;
    private double beginTime;
    private double endTime;
    
    public GuttingMethod(String geometry1, String geometry2, double beginTime, double endTime) {
        this.sourceGeometry = geometry1;
        this.targetGeometry = geometry2;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
    
    @Override
    public String atInstant(double instant){
        connectAndOpenDatabase();
        createGeometriesInDB();
        //System.out.println(instant);
        String cmd = "query " + objectName + " atinstant [const instant value " + instant + "];";
        
        ListExpr resultList = new ListExpr();
        IntByReference error_code = new IntByReference(0);
        IntByReference error_pos = new IntByReference(0);
        StringBuffer error_message = new StringBuffer();
        
        Secondointerface.secondo(cmd, resultList, error_code, error_pos, error_message);
        if(error_code.value != 0)
        {
            JOptionPane.showMessageDialog(null, error_message.toString(), "Secondo1.", JOptionPane.INFORMATION_MESSAGE);
            Secondointerface.terminate();
            return null;
        }
        
        cleanDataBase();
        closeDataBase();
        String wkt = rlistToWkt(resultList);
        if(wkt == null || error_code.value != 0)
        {
            JOptionPane.showMessageDialog(null, "ERR_PARSING_TO_WKT.", "Secondo2.", JOptionPane.INFORMATION_MESSAGE);
            Secondointerface.terminate();
            return null;
        }
        return wkt;
    }
    
    @Override
    public String[] duringPeriod(double beginTimeQuery, double endTimeQuery, int numSamples){
        connectAndOpenDatabase();
        createGeometriesInDB();

        double t;
        String cmd;
        String[] wkts = new String[numSamples];
        System.out.println("Starting interpolation during period using Gutting: " + String.valueOf(beginTimeQuery) + " "  + String.valueOf(endTimeQuery) + " "  + String.valueOf(numSamples) );
        int i = 0;
        for(double j = 1; j < numSamples; j++)
        {
            //t = (j / (dn - 1)) * db + (de - db);
            t = (j / (numSamples + 1)) * (endTimeQuery - beginTimeQuery) + beginTimeQuery;
            //System.out.println(t);
            //cmd = "open database test; query interpolate2([const region value " + source_rlist_str + "], [const instant value " + db + "], [const region value " + target_rlist_str + "], [const instant value " + de + "]) atinstant [const instant value " + t + "];";
            
            cmd = "query " + objectName + " atinstant [const instant value " + t + "];";
            
            ListExpr resultList = new ListExpr();
            IntByReference error_code = new IntByReference(0);
            IntByReference error_pos = new IntByReference(0);
            StringBuffer error_message = new StringBuffer();
            
            Secondointerface.secondo(cmd, resultList, error_code, error_pos, error_message);
            
            if(error_code.value != 0)
            {
                JOptionPane.showMessageDialog(null, error_message.toString(), "Secondo3.", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Command " + cmd);
                System.out.println("Error " + error_message.toString());
                Secondointerface.terminate();
                return null;
            }
            
            wkts[i] = rlistToWkt(resultList);
            
            if(error_code.value != 0)
            {
                JOptionPane.showMessageDialog(null, "ERR_PARSING_TO_WKT.", "Secondo4.", JOptionPane.INFORMATION_MESSAGE);
                Secondointerface.terminate();
                return null;
            }
            
            //if (wkts[i] == null)
            //    JOptionPane.showMessageDialog(null, "ERR_PARSING_TO_WKT.", "Secondo.", JOptionPane.INFORMATION_MESSAGE);
            
            i++;
        }
        
        cleanDataBase();
        closeDataBase();
        
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ex) {
            Logger.getLogger(GuttingMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return wkts;
    }
    
    //given a period of observation, and a source and target geometries, create in the secondo database
    //an object to query and perform interpolation.
    private void createGeometriesInDB(){
        ListExpr resultList = new ListExpr();
        IntByReference error_code = new IntByReference(0);
        IntByReference error_pos = new IntByReference(0);
        StringBuffer error_message = new StringBuffer();
        
        String sourceRList = wktToRList(sourceGeometry);
        String targetRList = wktToRList(targetGeometry);
        String createMovingRegionCmd = "let " + objectName + " = interpolate2([const region value " + sourceRList + "], [const instant value " + this.beginTime + "], [const region value " + targetRList + "], [const instant value " + this.endTime + "]);";
        Secondointerface.secondo(createMovingRegionCmd, resultList, error_code, error_pos, error_message);
        
        if(error_code.value != 0)
        {
            if (error_message.toString().contains("Identifier already used")){
                //old object was not deleted 
                Secondointerface.secondo("delete "+objectName, resultList, error_code, error_pos, error_message);
                System.out.println("-->"+error_message);
                
            }
            else
                JOptionPane.showMessageDialog(null, error_message.toString(), "Secondo5.", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println(error_message);
            Secondointerface.terminate();
            System.out.println(createMovingRegionCmd);
        }
    }
    
    private void connectAndOpenDatabase(){
        readDataBaseCredentials(AppConstants.SECONDO_CREDENTIALS_FILENAME);
        if (host == null)
            return;
        boolean ok;
        // Connection with SECONDO
        Secondointerface.setHostname(host);
        Secondointerface.setPort(port);
        Secondointerface.useBinaryLists(true);
        ok = Secondointerface.connect();

        if(!ok)
        {
            JOptionPane.showMessageDialog(null, "Connection to Secondo Failled.", "Secondo6.", JOptionPane.ERROR_MESSAGE);
            //System.err.println("Connection to Secondo Failled.");
            return;
        }
        
        System.out.println("Connected to Secondo.");
        
       ListExpr resultList = new ListExpr();
       IntByReference erroCode = new IntByReference(0);
       IntByReference errorPos = new IntByReference(0);
       StringBuffer errorMessage = new StringBuffer();

        // Open db.
        Secondointerface.secondo("open database " + databaseName, resultList, erroCode, errorPos, errorMessage);
        
        if(erroCode.value != 0)
        {
            JOptionPane.showMessageDialog(null, errorMessage.toString(), "Secondo7.", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println(error_message);
            Secondointerface.terminate();
            return;
        }
        else{
            System.out.println("Database "+databaseName +" opened");
        }
    }
    
    private void cleanDataBase(){
        ListExpr resultList = new ListExpr();
        IntByReference erroCode = new IntByReference(0);
        IntByReference errorPos = new IntByReference(0);
        StringBuffer errorMessage = new StringBuffer();
        // clean object in database
        Secondointerface.secondo("delete "+objectName+";", resultList, erroCode, errorPos, errorMessage);
        Secondointerface.secondo("close database;", resultList, erroCode, errorPos, errorMessage);
        //Secondointerface.secondo("delete database " + databaseName + ";", resultList, erroCode, errorPos, errorMessage);
    }
    
    private void closeDataBase(){
        // Disconnect from Secondo
        Secondointerface.terminate();
        Secondointerface.destroy();
    }
    
    /**
     * Converts a wkt to a RLIST representation.
     */
    private String wktToRList(String wkt){
        String rlistStr = "";
        String[] tokens = wkt.split("\\(");
        String[] points;
        String[] point;
        
        double x = 0;
        double y = 0;
        
        String token;
        String p;
        
        if(!tokens[0].trim().equals("POLYGON"))
            return "";
        
        // Exterior list.
        rlistStr += "(";
        
        // Open for each polygon.
        rlistStr += "(";
        
        for(int j = 1; j < tokens.length; j++)
        {
            token = tokens[j].trim();
            
            if(token.isEmpty())
                continue;
            
            token = token.replace(")", "");
            
            points = token.split(",");
            
            rlistStr += "(";
            
            for(int h = 0; h < points.length - 1; h++)
            {
                p = points[h].trim();
                
                if(p.isEmpty())
                    continue;
                
                point = p.split(" ");
                
                if(point.length != 2)
                    return "";
                
                x = Double.parseDouble(point[0]);
                y = Double.parseDouble(point[1]);
                
                rlistStr += "( " + x + " " + y + " )";
            }
            
            rlistStr += ")";
        }
        
        // Close for each polygon.
        rlistStr += ")";
        
        // Exterior list.
        rlistStr += ")";
        
        return rlistStr;
    }
    
    private String ScanValue(ListExpr LE)
    {
        ArrayList<ArrayList<Double>> geomS = new ArrayList<>();
        ArrayList<Double> coordinates;
        
        if(LE==null){
            return null;
        }
        
        Double x;
        Double y;
        
        while(!LE.isEmpty())
        {
            ListExpr Face = LE.first();
            LE = LE.rest();
            
            while(!Face.isEmpty())
            {
                ListExpr Cycle = Face.first();
                Face = Face.rest();
                
                coordinates = new ArrayList<>();
                
                if (Cycle==null)
                    return null;
                
                while(!Cycle.isEmpty())
                {
                    ListExpr P = Cycle.first();
                    Cycle = Cycle.rest();
                    
                    if (P==null)
                        return null;
                    
                    if (P.listLength() != 2)
                        return null;
                    
                    x = readNumeric(P.first());
                    y = readNumeric(P.second());
                    
                    if(x!=null && y!=null)
                    {
                        coordinates.add(x);
                        coordinates.add(y);
                    }
                    else
                    {
                        return null;
                    }
                }
                
                geomS.add(coordinates);
            }
        }
        
        // Construct wkt.
        
        String wkt = "POLYGON (";
        
        for(int j = 0; j < geomS.size(); j++)
        {
            coordinates = geomS.get(j);
            
            wkt += "(";
            
            for(int h = 0; h < coordinates.size(); h = h + 2)
            {
                wkt += coordinates.get(h) + " " + coordinates.get(h+1) + ", ";
            }
            
            wkt += coordinates.get(0) + " " + coordinates.get(1) + ")";
            
            if(j < geomS.size() - 1)
                wkt += ", ";
        }
        
        wkt += ")";
        
        return wkt;
    }
    
    private String rlistToWkt(ListExpr resultList)
    {
        ListExpr a_list;
        
        if (resultList == null)
            return null;
        
        if (resultList.listLength() != 2)
            return null;
        
        if (!resultList.first().symbolValue().equals("iregion"))
            return null;
        
        if (resultList.second() == null)
            return null;
        
        a_list = resultList.second();
        
        if (a_list.listLength() < 2)
            return null;
        
        return ScanValue(a_list.second());
    }
    
    private Double readNumeric (ListExpr le)
    {
        if (le.isAtom()) {
            if (! (le.atomType()==ListExpr.INT_ATOM || le.atomType()==ListExpr.REAL_ATOM))
            {
                return  null;
            }
            
            if (le.atomType()==ListExpr.INT_ATOM)
                return  new Double(le.intValue());
            else
                return new Double(le.realValue());
        }
        else
        {
            int length = le.listLength();
            
            if ((length != 5)&& (length != 6))
            {
                return  null;
            }
            
            if (length==5)
            {
                if (
                        (le.first().atomType() != ListExpr.SYMBOL_ATOM) 	||
                        (le.second().atomType() != ListExpr.INT_ATOM) 		||
                        (le.third().atomType() != ListExpr.INT_ATOM)		||
                        (le.fourth().atomType() != ListExpr.SYMBOL_ATOM) 	||
                        (le.fifth().atomType() != ListExpr.INT_ATOM))
                {
                    return  null;
                }
                
                if ((!le.first().symbolValue().equals("rat")) || (!le.fourth().symbolValue().equals("/")))
                {
                    return  null;
                }
                
                double g = (double)le.second().intValue();
                return  new Double((Math.abs(g) + (double)le.third().intValue()/(double)le.fifth().intValue()));
            }
            else
            {
                if (
                        (le.first().atomType() != ListExpr.SYMBOL_ATOM)		||
                        (le.second().atomType() != ListExpr.SYMBOL_ATOM)	||
                        (le.third().atomType() != ListExpr.INT_ATOM) 		||
                        (le.fourth().atomType() != ListExpr.INT_ATOM)		||
                        (le.fifth().atomType() != ListExpr.SYMBOL_ATOM) 	||
                        (le.sixth().atomType() != ListExpr.INT_ATOM)) {
                    return  null;
                }
                
                if (
                        (!le.first().symbolValue().equals("rat")) ||
                        (!le.fifth().symbolValue().equals("/")) 	||
                        !(le.second().symbolValue().equals("-")  	||
                        le.second().symbolValue().equals("+") ))
                {
                    return  null;
                }
                
                double g = (double)le.third().intValue();
                double v=1;
                if (le.second().symbolValue().equals("-")) v=-1;
                
                return  new Double(v*(Math.abs(g) + (double)le.fourth().intValue()/(double)le.sixth().intValue()));
            }
        }
    }
    
    private void readDataBaseCredentials(String credentialsFile){
        try(BufferedReader br = new BufferedReader(new FileReader(credentialsFile))) {
            for(String line; (line = br.readLine()) != null; ) {
                // process the line.
                String [] lineContent = line.split("\\s*=\\s*");
                switch(lineContent[0].toLowerCase()){
                    case AppConstants.SECONDO_HOST_HEADER:
                        host = lineContent[1];
                        break;
                    case AppConstants.SECONDO_PORT_HEADER:
                        port = Integer.parseInt(lineContent[1]);
                        break;
                    case AppConstants.SECONDO_DBNAME_HEADER:
                        databaseName = lineContent[1];
                        break;
                    case AppConstants.SECONDO_OBJNAME_HEADER:
                        objectName = lineContent[1];
                        break;
                }
            }
            // line is not visible here.
        } catch (IOException ex) {
            Logger.getLogger(GuttingMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
