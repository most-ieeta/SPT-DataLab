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
package ua.ieeta.sptdatalab.app;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * Manages and loads images in the dataset for the panels.
 */
public class AppImage {
    private int currentImageWidthInPanel1;
    private int currentImageWidthInPanel2;
    private int currentImageHeightInPanel1;
    private int currentImageHeightInPanel2;
    
    private List<Image> images = new ArrayList<>();
    private List<String> imageNames = new ArrayList<>();
    
    //stores the index of the image selected in each panel in the list of images 
    //the index of the selected image in panel 1 will always be 1 less than the index of the image in panel2
    private int selectedImageIndexPanel1;
    private int selectedImageIndexPanel2;
    
    private static AppImage instance;
    
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    
    private AppImage() {
        setSelectedImageIndexPanel1(0);
        setSelectedImageIndexPanel2(1);
    }
    
    public static AppImage getInstance(){
        if(instance == null){
            instance = new AppImage();
        }
        return instance;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
 
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
    
    public void loadImages(File[] files){
        //sort files first
        images.clear();
        imageNames.clear();
        sortByNumber(files);
        for (File file : files){
            try {
                BufferedImage b = ImageIO.read(file);
                images.add(b);
                imageNames.add(file.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(AppImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (images.size() == 1){
            setSelectedImageIndexPanel2(0);
            //if there is only one image, immediately deactivate one
        }
        setSelectedImageIndexPanel1(0);
        setSelectedImageIndexPanel2(1);
    }
    
    private void sortByNumber(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                if (name.contains("."))
                    name = name.split("\\.")[0];
                int i = 0;
                String[] nameSplit = name.split("_");
                String s = nameSplit[nameSplit.length-1];
                try {
                    return Integer.parseInt(s);
                } catch(NumberFormatException e) {
                    i = 0; // if filename does not match the format
                           // then default to 0
                }
                return i;
            }
        });

        
    }
    
    //load next Image in the list, unless it is the last image in the list 
    //return true if the image changed is the last one, false otherwise
    public void loadNextImage(){
        if(selectedImageIndexPanel1 < images.size()-2){
            setSelectedImageIndexPanel1(selectedImageIndexPanel1+1);
            setSelectedImageIndexPanel2(selectedImageIndexPanel2+1);
        }
    }
    
    //load previous image in the list, unless the image in the first panel is the first one in the list
    //returns true if the image changed is the first image, false otherwise
    public void loadPreviousImage(){
        if(selectedImageIndexPanel1 > 0){
            setSelectedImageIndexPanel1(selectedImageIndexPanel1-1);
            setSelectedImageIndexPanel2(selectedImageIndexPanel2-1);
        }
    }

    
    public Image getCurrentlySelectedImageForPanel(boolean isSecondPanel){
        if (isSecondPanel){
            return images.get(selectedImageIndexPanel2);
        }
        else{
            return images.get(selectedImageIndexPanel1);
        }
    }
    
    public int getTotalNumberOfImages(){
        return images.size();
    }
    
    public boolean selectImageForPanel1(int i){
        i--;//decrease because the index of an image in the list of images == the number of the image in the panel -1
        if (i <= images.size()-2 && i >= 0){
            //there is a image for both panels
            setSelectedImageIndexPanel1(i);
            //the index of the selected image in panel 2 will always be 1 more than the index of the image in panel 1
            setSelectedImageIndexPanel2(i+1);
            return true;
        }
        return false;
    }
    
    public boolean isLastImageForPanel1(){
        if(images.size() == 1){
            return this.selectedImageIndexPanel2 == images.size()-1;
        }
        return this.selectedImageIndexPanel1 == images.size()-2;
    }
    
    public boolean isFirstImageForPanel1(){
        return this.selectedImageIndexPanel1 == 0;
    }
    
    public boolean isFirstImageForPanel2(){
        if(images.size() == 1){
            return this.selectedImageIndexPanel2 == 0;
        }
        return this.selectedImageIndexPanel2 == 1;
    }
    
    public boolean isLastImageForPanel2(){
        return this.selectedImageIndexPanel2 == images.size()-1;
    }
    
    public boolean selectImageForPanel2(int i){
        i--; //decrease because the index of an image in the list of images == the number of the image in the panel -1
        if (i <= images.size()-1 && i >= 1){
            setSelectedImageIndexPanel2(i);
            //the index of the selected image in panel 1 will always be 1 less than the index of the image in panel2
            setSelectedImageIndexPanel1(i-1);
            return true;
        }
        return false;
    }
    
    public int getCurrentIndexImageForPanel1(){
        return selectedImageIndexPanel1;
    }
    
    public int getCurrentIndexImageForPanel2(){
        return selectedImageIndexPanel2;
    }
    
    public int getImageOriginalWidth(boolean isSecondPanel) {
        return getCurrentlySelectedImageForPanel(isSecondPanel).getWidth(null);
    }


    public int getImageOriginalHeight(boolean isSecondPanel) {
        return getCurrentlySelectedImageForPanel(isSecondPanel).getHeight(null);
    }
    
     //keeps the 1:1 aspect ration of the image and draws it
    public void keepAspectRatioAndDrawImage(Graphics2D g, Dimension panelDim, boolean isSecondPanel){
        Dimension d = resizeImageDimension(panelDim, isSecondPanel);
        g.drawImage(getCurrentlySelectedImageForPanel(isSecondPanel), 0, 0, d.width, d.height, null);
    }
    
    //resizes a dimension to fit a boundary dimension, while maintaining the aspect ratio
    public Dimension resizeImageDimension(Dimension windowDimension, boolean isSecondPanel){
        //Dimension windowDimension = new Dimension(panelWidth, panelHeight);
        Image im = getCurrentlySelectedImageForPanel(isSecondPanel);
        Dimension d = getScaledDimension(new Dimension((int) im.getWidth(null), 
        (int) im.getHeight(null)), windowDimension );
        setImageHeightInPanel((int) Math.round(d.height), isSecondPanel);
        setImageWidthInPanel((int) Math.round(d.width), isSecondPanel);
        //System.out.println("image width: "+d.width +", image height: "+d.height);
        return d;
    }
      
    private Dimension getScaledDimension(Dimension imageSize, Dimension boundary) {
        double widthRatio = boundary.getWidth() / imageSize.getWidth();
        double heightRatio = boundary.getHeight() / imageSize.getHeight();
        double ratio = Math.min(widthRatio, heightRatio);

        return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
    }

    public int getImageWidthInPanel(boolean isSecondPanel) {
        if(isSecondPanel){
            return currentImageWidthInPanel2;
        }
        return currentImageWidthInPanel1;
    }

    public void setImageWidthInPanel(int imageWidthInPanel, boolean isSecondPanel) {
        if (isSecondPanel){
            this.currentImageWidthInPanel2 = imageWidthInPanel;
        }
        else{
            this.currentImageWidthInPanel1 = imageWidthInPanel;
        }
    }

    public int getImageHeightInPanel(boolean isSecondPanel) {
        if(isSecondPanel){
            return currentImageHeightInPanel2;
        }
        return currentImageHeightInPanel1;
    }

    public void setImageHeightInPanel(int imageHeightInPanel, boolean isSecondPanel) {
        if(isSecondPanel){
            this.currentImageHeightInPanel2 = imageHeightInPanel;
        }
        else{
            this.currentImageHeightInPanel1 = imageHeightInPanel;
        }   
    }

    public void setSelectedImageIndexPanel1(int index){
        support.firePropertyChange(AppConstants.IMAGE_LEFT_PANEL_PROPERTY, selectedImageIndexPanel1, index);
        this.selectedImageIndexPanel1 = index;
    }
    
    public void setSelectedImageIndexPanel2(int index){
        support.firePropertyChange(AppConstants.IMAGE_RIGHT_PANEL_PROPERTY, selectedImageIndexPanel2, index);
        this.selectedImageIndexPanel2 = index;
    }
    
    public void setToFirstImages(){
        setSelectedImageIndexPanel1(0);
        setSelectedImageIndexPanel2(1);
    }
    
    public String getImageName(int index){
        if (index >= imageNames.size())
            return "";
        return this.imageNames.get(index);
    }
}
