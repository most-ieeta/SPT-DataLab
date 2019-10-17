/*
* Copyright (c) 2016 Vivid Solutions.
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* and Eclipse Distribution License v. 1.0 which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
*
* http://www.eclipse.org/org/documents/edl-v10.php.
*/

/* 
* This file has been modified to be part of SPT Data Lab.
*
* This code is distributed "AS IS" in the hope that it will be useful,
* but WITHOUT ANY WARRANTY. You can redistribute it and/or modify
* as explained in License and Readme.
* 
* Redistributions of source code must retain adequate copyright notices,
* as explained in License and Readme.
*/

package ua.ieeta.sptdatalab.ui.render;

import ua.ieeta.sptdatalab.ui.SwingWorker;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.locationtech.jts.geom.Geometry;
import ua.ieeta.sptdatalab.app.AppCorrGeometries;
import ua.ieeta.sptdatalab.app.AppImage;
import ua.ieeta.sptdatalab.app.GeometryEditPanel;
import ua.ieeta.sptdatalab.app.SPTDataLabBuilderFrame;
import ua.ieeta.sptdatalab.ui.style.BasicStyle;
import ua.ieeta.sptdatalab.ui.style.Style;


public class RenderManager
{
    private GeometryEditPanel panel;
    private RendererSwingWorker worker = null;
    private Image image = null;
    private boolean isDirty = true;
    
    private Timer repaintTimer = new Timer(400, new ActionListener()
    {
        public void actionPerformed(ActionEvent e) {
            if (worker.isRendering()) {
                paintPanel();
                return;
            }
            repaintTimer.stop();
            paintPanel();
        }
    });
    
    public RenderManager(GeometryEditPanel panel)
    {
        this.panel = panel;
        // start with a short time cycle to give better appearance
        repaintTimer.setInitialDelay(80);
    }
    
    public void setDirty(boolean isDirty)
    {
        this.isDirty = isDirty;
    }
    
    public void componentResized()
    {
        image = null;
        isDirty = true;
    }
    
    public void render()
    {
        if (image != null && ! isDirty) return;
        
        /*
        * Clear dirty flag at start of rendering, so that subsequent paints will newly rendered image.
        * Another way to think of this is that once rendering has been initiated,
        * from the perspective of the client panel the image is up-to-date
        * (although possibly not yet fully rendered)
        */
        isDirty = false;
        
        repaintTimer.stop();
        
        if (worker != null)
            worker.cancel();
        initImage();
        worker = new RendererSwingWorker(panel.getRenderer(), image);
        worker.start();
        repaintTimer.start();
    }
    
    private void initImage()
    {
        if (image != null) {
            erase(image);
            return;
        }
        image = createPanelImage(panel);
    }
    
    private Image createPanelImage(JPanel panel) {
        return new BufferedImage(panel.getWidth(), panel.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
    }
    
    public void erase(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.white);
        
        Rectangle2D.Double r = new Rectangle2D.Double(0, 0, width, height);
        g.fill(r);
    }
    
    public void copyImage(Graphics g){
        if (image == null) {
            return;
        }
        g.drawImage(image, 0, 0, null);
        
        //necessary to highlight points. This is assynchronous, so if there is a point to be higlighted we call
        //this function here
        //AppCorrGeometries.getInstance().drawPoints(panel);
    }
    
    
    private void paintPanel()
    {
        copyImage(panel.getGraphics());
    }
    
    public Image getImage(){
        return this.image;
    }
}

class RendererSwingWorker extends SwingWorker
{
    private Image image = null;
    
    private Renderer renderer;
    private boolean isRendering = true;
    
    public RendererSwingWorker(Renderer renderable, Image image)
    {
        this.renderer = renderable;
        this.image = image;
    }
    
    public Object construct()
    {
        isRendering = true;
        Graphics2D gr = (Graphics2D) image.getGraphics();
        renderer.render(gr);
        isRendering = false;
        return new Boolean(true);
    }
    
    public boolean isRendering()
    {
        return isRendering;
    }
    
    public void cancel()
    {
        renderer.cancel();
    }
}
