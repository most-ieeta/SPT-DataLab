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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 
 * Panel located above the 2 panels with the images with legends and the currently selected files.
 */
public class LegendPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private int numberSourceImageFile;
    private int numberTargetImageFile;
    private final int maxStringSize = 80;//maximum number of chars in text for some labels
    
    public LegendPanel() {
        numberSourceImageFile = 0;
        numberTargetImageFile = 1;
        AppImage.getInstance().addPropertyChangeListener(this);
        AppCorrGeometries.getInstance().addPropertyChangeListener(this);
        initComponents();
        updateFilesInLegend();
        
       }
    
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()){
            case AppConstants.IMAGE_LEFT_PANEL_PROPERTY:
                this.numberSourceImageFile = Integer.parseInt(evt.getNewValue().toString());
                break;
            case AppConstants.IMAGE_RIGHT_PANEL_PROPERTY:
                this.numberTargetImageFile = Integer.parseInt(evt.getNewValue().toString());
                break;
        }
        updateFilesInLegend();
    }
    
    private void updateFilesInLegend(){
        setTextForSource();
        setTextForTarget();
        setTextForGeometryFile();
    }
    
    private void setTextForSource(){
        String sourceImageFile = limitStringSize(AppImage.getInstance().getImageName(numberSourceImageFile, false));
        String text = "<html>"
                      + "<font size=+1 color=red>Source</font>";
        text+= "   Image: " + sourceImageFile
            + "</html>";
        this.sourceLabel.setText(text);
        sourceLabel.setToolTipText(AppImage.getInstance().getImageName(numberSourceImageFile, true));//show full path on tooltip
    }
    
    private void setTextForTarget(){
        String targetImageFile = limitStringSize(AppImage.getInstance().getImageName(numberTargetImageFile, false));
        String text = "<html>"
                      + "<font size=+1 color=red>Target</font>"
                      + "   Image: " + targetImageFile;
        text += "</html>";
        this.targetLabel.setText(text);
        targetLabel.setToolTipText(AppImage.getInstance().getImageName(numberTargetImageFile, true));//show full path on tooltip
    }
    
    private void setTextForGeometryFile(){
        String corrFile = AppCorrGeometries.getInstance().getCurrentGeometryFile(false);
        String text = "<html>"
                      + "<font size=+1 color=blue>Geometry File: </font>"
                      + "   " + corrFile;
        text += "</html>";
        this.corrFileLabel.setText(text);
        corrFileLabel.setToolTipText(AppCorrGeometries.getInstance().getCurrentGeometryFile(true));
    }
    
    //limite the size of the text shown in the labels
    private String limitStringSize(String str){
        if (str.length() <= maxStringSize)
            return str;
       return "..."+str.substring(str.length() - maxStringSize, str.length());
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        sourceLabel = new javax.swing.JLabel();
        targetLabel = new javax.swing.JLabel();
        corrFileLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        sourceLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        add(sourceLabel, gridBagConstraints);

        targetLabel.setText("jLabel4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        add(targetLabel, gridBagConstraints);

        corrFileLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(corrFileLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel corrFileLabel;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JLabel targetLabel;
    // End of variables declaration//GEN-END:variables
}
