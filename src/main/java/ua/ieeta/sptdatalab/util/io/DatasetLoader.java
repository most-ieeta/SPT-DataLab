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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import static ua.ieeta.sptdatalab.app.AppConstants.CACHE_FILE;
import ua.ieeta.sptdatalab.app.AppCorrGeometries;
import ua.ieeta.sptdatalab.app.AppImage;
import ua.ieeta.sptdatalab.app.SPTDataLab;
import ua.ieeta.sptdatalab.ui.SwingUtil;

/*
 Loads coordinates files and/or image files from moving objects datasets
*/
public class DatasetLoader {
    
    private static File previousDirectory = new File("");//stores the directory in which the user selected something to reopen on the same dir (for convenience)
    
    /**
     * Loads from user selected directories a dataset (images and coordinates files). Loads only if user didn't cancel any jfilechooser.
     * @return true if a new dataset has been successfully loaded to the application, false otherwise
     */
    public static boolean loadAndSetDataset(){
        File imagesDirectory = promptAndLoadImagesFromDirectory();
        if (imagesDirectory == null)
            return false;
        File coordinatesDirectory = promptAndLoadCoordinateFiles();
        
        if (coordinatesDirectory != null){ //imagesDirectory is not null
            AppImage.getInstance().loadImages(imagesDirectory.listFiles());//load image files to panels
            AppCorrGeometries.getInstance().setNewCoordinatesDataset(coordinatesDirectory);//load coordinate files to panels
            saveDirectoriesToCache(imagesDirectory, coordinatesDirectory);
            return true;
        }
        return false;
    }
    
    public static boolean loadAndSetCoordinatesFiles(){
        File coordinatesDirectory = promptAndLoadCoordinateFiles();
        
        if (coordinatesDirectory != null){
            AppCorrGeometries.getInstance().setNewCoordinatesDataset(coordinatesDirectory);//load coordinate files to panels
            saveCoordinatesDirectoryToCache(coordinatesDirectory);
            return true;
        }
        return false;
    }
    
    private static File promptAndLoadCoordinateFiles(){
        JFileChooser fileChooser = new JFileChooser(previousDirectory);
        fileChooser.removeChoosableFileFilter(SwingUtil.JAVA_FILE_FILTER);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("wkt, corr", "corr", "wkt");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.addChoosableFileFilter(SwingUtil.XML_FILE_FILTER);
        fileChooser.setDialogTitle("Choose a directory with the coordinate files to open: ");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(null)) {
            previousDirectory = fileChooser.getCurrentDirectory();
            File[] files = fileChooser.getSelectedFile().listFiles();
            if (files.length == 0) {
                //no files in directory!
                JOptionPane.showMessageDialog(null, "The indicated folder is empty!\n No files were loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    
    /**
     * Show a file chooser for the user to select the diretory with images.
     * @return diretory with images. Null if user canceled or directory is empty.
     */
    private static File promptAndLoadImagesFromDirectory(){
        //file chooser window to select a directory of images
        JFileChooser jfc = new JFileChooser(previousDirectory);
        jfc.setDialogTitle("Choose a directory with images to open: ");

        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("png, gif, jpg, bmp or tiff images", "png", "gif", "jpg", "bmp", "tiff");
        jfc.addChoosableFileFilter(filter);
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            previousDirectory = jfc.getCurrentDirectory();
            if (jfc.getSelectedFile().listFiles().length == 0) {
                //no files in directory!
                JOptionPane.showMessageDialog(null, "The indicated folder is empty!\n No files were loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return jfc.getSelectedFile();
        }
        return null;
    }
    
    /**
     * Update the cache file with new image and coordinates directories.
     * @param dir - directory with images
     */
    private static void saveDirectoriesToCache(File imageDir, File coordinatesDir){
        try {
            //save to cache file the directories of the image and corr files used
            PrintWriter writer = new PrintWriter(CACHE_FILE, "UTF-8");
            writer.println(imageDir);
            writer.println(coordinatesDir);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(SPTDataLab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Update the cache file with a new image directory. Coordinates directory is not updated.
     * @param dir - directory with images
     */
    private static void saveImageDirectoryToCache(File dir){
        //get the current image directory in the cache
        String[] directories = readDirectoriesFromCache();
        String coordinateDirectories = directories[0];
        try {
            //save to cache file the directories of the image and corr files used
            PrintWriter writer = new PrintWriter(CACHE_FILE, "UTF-8");
            writer.println(dir);
            writer.println(coordinateDirectories);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(SPTDataLab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Update the cache file with a new coordinates directory. Images directory is not updated.
     * @param dir - directory with Coordinates files.
     */
    private static void saveCoordinatesDirectoryToCache(File dir){
        //get the current image directory in the cache
        String[] directories = readDirectoriesFromCache();
        String imageDirectories = directories[0];
        try {
            //save to cache file the directories of the image and corr files used
            PrintWriter writer = new PrintWriter(CACHE_FILE, "UTF-8");
            writer.println(imageDirectories);
            writer.println(dir);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(SPTDataLab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Reads the content of the cache file of last used data set (images and coordinates files).
     * @return String[], where first element is images directory, and second is coordinates directory. Null if file is empty
     */
    public static String[] readDirectoriesFromCache(){
        String currentImagesDir = "";
        String currentCoordinatesDir = "";
        try(BufferedReader br = new BufferedReader(new FileReader(CACHE_FILE))) {
            currentImagesDir = br.readLine();
            currentCoordinatesDir = br.readLine();
            return new String[] {currentImagesDir, currentCoordinatesDir};
        } catch (IOException ex) {
            Logger.getLogger(DatasetLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
