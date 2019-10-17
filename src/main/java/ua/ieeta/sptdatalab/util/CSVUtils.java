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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import static ua.ieeta.sptdatalab.util.ScreenImage.writeImage;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    
    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
    
    /**
     * return 0 if sucess in saving file, 1 if error 2 if user canceled
     * @param dataset
     * @return 
     */
    public static int exportAndSaveToCSV(Map<String, ?> dataset){
        
        JFrame parentFrame = new JFrame();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select directory, name of the file and its extension"); 
        
        fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());//set possible file extensions to save
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".csv", "csv"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".txt", "txt"));
        
        // set default type
        fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[0]);
        
        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            //System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                
            String csvFile = fileToSave.getAbsolutePath();
            if (! csvFile.contains(".")){
                //user did not add an extension, add the file extension filter currently selected in the window
                csvFile += "." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
            }
            FileWriter writer;
            try {
                writer = new FileWriter(csvFile);
                
                SortedSet<Integer> keys = sortMapKeys(dataset);
                if (dataset.get(keys.first().toString()) instanceof List){
                    for (Integer key : keys) {
                        List<?> l = (List<?>) dataset.get(key.toString());
                        String str = "";
                        for (int i = 0; i < l.size(); i ++){
                            str += l.get(i).toString();
                            if (i != l.size()-1)
                                str += ",";
                        }
                        CSVUtils.writeLine(writer, Arrays.asList(key+"", str));
                    }
                }
                else{
                    for (Integer key : keys) { 
                        CSVUtils.writeLine(writer, Arrays.asList(key+"", dataset.get(key.toString())+""));
                    }
                }

                writer.flush();
                writer.close();
                
            } catch (IOException ex) {
                Logger.getLogger(CSVUtils.class.getName()).log(Level.SEVERE, null, ex);
                return 1;
            }
            //success
            return 0;
        }
        else if (userSelection == JFileChooser.CANCEL_OPTION){
            //do not consider  a failure or a success
            return 2;
        }
        //file not saved
        return 1;
    }

    private static SortedSet<Integer> sortMapKeys(Map<String, ?> map){
        //sort the key (by instant)and convert to int
        Set<String> keys = new HashSet<>(map.keySet());
        SortedSet<Integer> sortedKeys = new TreeSet<>();
        for (String inst : keys){
            sortedKeys.add(Integer.parseInt(inst));
        }
        return sortedKeys;
    }

}