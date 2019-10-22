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

package ua.ieeta.sptdatalab.app;

import ua.ieeta.sptdatalab.ui.tools.Tool;
import ua.ieeta.sptdatalab.ui.render.LayerRenderer;
import ua.ieeta.sptdatalab.ui.render.GeometryPainter;
import ua.ieeta.sptdatalab.ui.render.GridRenderer;
import ua.ieeta.sptdatalab.ui.render.RenderManager;
import ua.ieeta.sptdatalab.ui.render.Renderer;
import ua.ieeta.sptdatalab.ui.Viewport;
import ua.ieeta.sptdatalab.ui.GeometryLocationsWriter;
import ua.ieeta.sptdatalab.ui.ColorUtil;
import ua.ieeta.sptdatalab.model.GeometryType;
import ua.ieeta.sptdatalab.model.DisplayParameters;
import ua.ieeta.sptdatalab.model.GeometryEditModel;
import ua.ieeta.sptdatalab.model.StaticGeometryContainer;
import ua.ieeta.sptdatalab.model.LayerList;
import ua.ieeta.sptdatalab.model.DrawingGrid;
import ua.ieeta.sptdatalab.model.GeometryStretcherView;
import ua.ieeta.sptdatalab.model.GeometryDepiction;
import ua.ieeta.sptdatalab.model.TestBuilderModel;
import java.text.NumberFormat;
import java.util.List;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import ua.ieeta.sptdatalab.ui.style.AWTUtil;


/**
 * Panel which displays rendered geometries.
 * 
 * Zoom methods take arguments in model space.
 * 
 * @version 1.7
 */
public class GeometryEditPanel extends JPanel {	
	
    private TestBuilderModel tbModel;
    
    private DrawingGrid grid = new DrawingGrid();
    private GridRenderer gridRenderer;

    boolean stateAddingPoints = false;

    Coordinate markPoint;
    Point2D lastPt = new Point2D.Double();

    private Tool currentTool = null;  //PolygonTool.getInstance();

    private Viewport viewport = new Viewport(this);

    private RenderManager renderMgr;
    
    //added
    //used to indentify the geometry that contains the object represented in a corr file
    private static final int OBJECT_GEOMETRY_INDEX = 0;
    //index for the geometry that is used to place the image
    private static final int BACKGROUND_IMAGE_GEOMETRY_INDEX = 1;
    private static final int OTHER_GEOMETRY_INDEX = 2;
    //----------------------------------------
    BorderLayout borderLayout1 = new BorderLayout();
    GeometryPopupMenu menu = new GeometryPopupMenu();
    
    //flag to check if this is the first(right) or second (left) panel
    private boolean isSecondPanel = false;
    
    private boolean corrGeometryAndImageLoaded = false;
    //flag to indicate if there is currently any marked point
    private boolean pointsDrawn = false;
    
    public GeometryEditPanel(boolean iSecondPanel) {
        this();
        this.isSecondPanel = iSecondPanel;
    }

    public GeometryEditPanel() {
        gridRenderer = new GridRenderer(viewport, grid);
        try {
          initUI();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        renderMgr = new RenderManager(this);
        //opMonitor = new OperationMonitorManager(this, viewport);
    }

    void initUI() throws Exception {
      this.addComponentListener(new java.awt.event.ComponentAdapter() {

        public void componentResized(ComponentEvent e) {
          this_componentResized(e);
        }
      });

      //this.setBackground(Color.white);
      this.setBorder(BorderFactory.createLoweredBevelBorder());
      this.setLayout(borderLayout1);
      setToolTipText("");
      setBorder(BorderFactory.createEmptyBorder());
      
      //legendPanel.setSize(new Dimension(50, 50));
      //this.add(legendPanel);
    }  

    class PopupClickListener extends MouseAdapter
    {
      public void mousePressed(MouseEvent e)
      {
        if (e.isPopupTrigger())
          doPopUp(e);
      }
      public void mouseReleased(MouseEvent e)
      {
        if (e.isPopupTrigger())
          doPopUp(e);
      }
      private void doPopUp(MouseEvent e)
      {
          menu.show(e.getComponent(), e.getX(), e.getY());
      }
    }


    public void setModel(TestBuilderModel model) {
      this.tbModel = model;
    }

    public TestBuilderModel getModel() {
      return tbModel;
    }
    public GeometryEditModel getGeomModel()
    {
      return tbModel.getGeometryEditModel();
    }

    public void setGridEnabled(boolean isEnabled) {
      gridRenderer.setEnabled(isEnabled);
    }


    public Viewport getViewport() { return viewport; }

    public void updateView()
    {
  //    fireGeometryChanged(new GeometryEvent(this));
        forceRepaint();
    }

    public void forceRepaint() {
          renderMgr.setDirty(true);
          
          Component source = SwingUtilities.windowForComponent(this);
          if (source == null)
            source = this;
          source.repaint();
    }

    private LayerList getLayerList()
    {
      return tbModel.getLayers();
    }

    public void setShowingInput(boolean isEnabled)
    {
      if (tbModel == null) return;
      getLayerList().getLayer(LayerList.LYR_A).setEnabled(isEnabled);
      getLayerList().getLayer(LayerList.LYR_B).setEnabled(isEnabled);
      forceRepaint();
    }

    public void setShowingGeometryA(boolean isEnabled) {
      if (tbModel == null) return;
      getLayerList().getLayer(LayerList.LYR_A).setEnabled(isEnabled);
      forceRepaint();
    }

    public void setShowingGeometryB(boolean isEnabled) {
      if (tbModel == null) return;
      getLayerList().getLayer(LayerList.LYR_B).setEnabled(isEnabled);
      forceRepaint();
    }

    public void setShowingResult(boolean isEnabled) 
    {
      if (tbModel == null) return;
      getLayerList().getLayer(LayerList.LYR_RESULT).setEnabled(isEnabled);
      forceRepaint();
    }


    public void setHighlightPoint(Coordinate pt) {
      markPoint = pt;
    }


    public void updateGeom()
    {
          renderMgr.setDirty(true);
          getGeomModel().geomChanged();
    }

    public String getToolTipText(MouseEvent event) {
  //    if (event.getPoint().x < 100) return null;
      Coordinate pt = viewport.toModelCoordinate(event.getPoint());
      double toleranceInModel = AppConstants.TOLERANCE_PIXELS / getViewport().getScale();
      
      // avoid wierd scale issues
      if (toleranceInModel <= 0.0) return null;
      
      return GeometryLocationsWriter.writeLocation(getLayerList(), pt, toleranceInModel, isSecondPanel);
  //    return viewport.toModel(event.getPoint()).toString();
  //    return null;
    }

    public double getToleranceInModel()
    {
      return AppConstants.TOLERANCE_PIXELS / getViewport().getScale();
    }

    public String getInfo(Coordinate pt)
    {
      double toleranceInModel = AppConstants.TOLERANCE_PIXELS / getViewport().getScale();
      GeometryLocationsWriter writer = new GeometryLocationsWriter();
      writer.setHtml(false);
      return writer.writeLocationString(getLayerList(), pt, toleranceInModel, isSecondPanel);
    }

    public double getGridSize() {
      return grid.getGridSize();
    }
     
   
    @Override
    public void paintComponent(Graphics g) {
        renderMgr.render();
        renderMgr.copyImage(g);
    }
    
    public void cleanAndDrawGeometry(){
        tbModel.getGeometryEditModel().setEditGeomIndex(OBJECT_GEOMETRY_INDEX);
        tbModel.getGeometryEditModel().clear(OBJECT_GEOMETRY_INDEX);
        drawGeometry();
    }
    
    //draws the geometry defined by the coordinates in the "corr" file. These coordinates are transformed
    //to match the display
    public void drawGeometry(){
        //call this just to make sure that the variables with the image dimensions in the panel are not null or zero
        AppImage.getInstance().resizeImageDimension(this.getSize(), isSecondPanel);
        AppCorrGeometries appCorr = AppCorrGeometries.getInstance();
        List<Coordinate> coord;//for polygon or normal geometry read from corr file
        List<List<Coordinate>> coords; //for multipolygon
        
         //remove any other geometries previously drawn
        
        //tbModel.getGeometryEditModel().clear();
         
        drawImagePolygon();
        if (!this.isSecondPanel && appCorr.showMorphingGeometry()){
            //this is the first panel and will draw the result of the morphing of a geometry
            coord = appCorr.getMorphingPolygon();
            coords = appCorr.getMorphingMultiPolygon();
            if (!coord.isEmpty()){ //polygon
                //the below 2 lines of code have to be repeated every time we add a component... else it throws an exception
                //remove any other geometries previously drawn
                tbModel.getGeometryEditModel().setEditGeomIndex(OBJECT_GEOMETRY_INDEX);
                tbModel.getGeometryEditModel().clear();

                tbModel.getGeometryEditModel().setGeometryType(GeometryType.POLYGON);
                tbModel.getGeometryEditModel().setEditGeomIndex(OBJECT_GEOMETRY_INDEX);
                tbModel.getGeometryEditModel().addComponent(coord);//draw a polygon from the morphing operation
            }
            else if (!coords.isEmpty()){//multipolygon
                //this chunk of code has to be repeated every time we add a component... else it throws an exception
                //remove any other geometries previously drawn
                tbModel.getGeometryEditModel().setEditGeomIndex(OBJECT_GEOMETRY_INDEX);
                tbModel.getGeometryEditModel().clear();

                tbModel.getGeometryEditModel().setGeometryType(GeometryType.POLYGON);
                tbModel.getGeometryEditModel().setEditGeomIndex(OBJECT_GEOMETRY_INDEX);
                for (List<Coordinate> mesh : coords){
                    tbModel.getGeometryEditModel().addComponent(mesh);
                }
            }
        }
        else{
            //this panel will draw the geometry read from the file or from the cache if previously edited
            coord = appCorr.getSourceTargetCoordsFixedForScreen(this);
            //this chunk of code has to be repeated every time we add a component... else it throws an exception
            //remove any other geometries previously drawn
            tbModel.getGeometryEditModel().setEditGeomIndex(OBJECT_GEOMETRY_INDEX);
            tbModel.getGeometryEditModel().clear();

            tbModel.getGeometryEditModel().setGeometryType(GeometryType.POLYGON);
            tbModel.getGeometryEditModel().setEditGeomIndex(OBJECT_GEOMETRY_INDEX);
            //tbModel.getGeometryEditModel().clear();
            tbModel.getGeometryEditModel().addComponent(coord);
        }
        
        this.updateGeom();   
    }
    
    //draw a square whose size is the same as the background image in the panel
    //it will add the image as background
    public void drawImagePolygon(){
        AppImage appImage = AppImage.getInstance();
        double currImageHeight = appImage.getImageHeightInPanel(isSecondPanel);
        double currImageWidth = appImage.getImageWidthInPanel(isSecondPanel);
        
        List<Coordinate> coord = new ArrayList<>();
        coord.add( new Coordinate(0, 0));
        coord.add( new Coordinate(currImageWidth, 0));
        coord.add( new Coordinate(currImageWidth, currImageHeight));
        coord.add( new Coordinate(0, currImageHeight));
        
        // clear the current square and update, if any (to be accordingly with the size of the panel)
        try{
            tbModel.getGeometryEditModel().clear(BACKGROUND_IMAGE_GEOMETRY_INDEX);
        } catch (NullPointerException ex){ 
            //Logger.getLogger(GeometryEditPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        tbModel.getGeometryEditModel().setGeometryType(GeometryType.POLYGON);
        tbModel.getGeometryEditModel().setEditGeomIndex(BACKGROUND_IMAGE_GEOMETRY_INDEX);
        tbModel.getGeometryEditModel().addComponent(coord);
        this.updateGeom();
        
        tbModel.getGeometryEditModel().setEditGeomIndex(OTHER_GEOMETRY_INDEX);
        
    }
    
  
    /*private List<Coordinate>correctCoordinates(Coordinate[] coord){
        AppImage appImage = AppImage.getInstance();
        Point2D viewOrigin = viewport.toView(new Coordinate(0, 0));
        double vOriginX = viewOrigin.getX();
        double currImageHeight = appImage.getImageHeightInPanel(isSecondPanel);
        double currImageWidth = appImage.getImageWidthInPanel(isSecondPanel);
        List<Coordinate> transformedCoords = new ArrayList<>();
        CoordinateUtils coordUtils;
        for (Coordinate c : coord){
            coordUtils = new CoordinateUtils(c.getX(), c.getY() );
            
            coordUtils.transformCoords(appImage.getImageOriginalWidth(isSecondPanel), appImage.getImageOriginalHeight(isSecondPanel),
                    currImageWidth, currImageHeight);
            
            //add the diference of the top of the panel and the image 
            coordUtils.translate(new CoordinateUtils(vOriginX, (getSize().getHeight() - currImageHeight) - 
                    (getSize().getHeight() - currImageHeight) ));
            
            transformedCoords.add(coordUtils);
        }
        return transformedCoords;
    }*/
  
  private static int VERTEX_SIZE = AppConstants.VERTEX_SIZE + 1;
  private static double VERTEX_SIZE_OVER_2 = VERTEX_SIZE / 2;
  
  private static int INNER_SIZE = VERTEX_SIZE  - 2;
  private static double INNER_SIZE_OVER_2 = INNER_SIZE / 2;
  
  private void drawHighlightedVertices(Graphics2D g, List coords, Color clr) {
    Rectangle2D rect = new Rectangle2D.Double();
    for (int i = 0; i < coords.size(); i++) {
      Coordinate pt = (Coordinate) coords.get(i);
      Point2D p = viewport.toView(pt);
      rect.setFrame(
          p.getX() - VERTEX_SIZE_OVER_2,
          p.getY() - VERTEX_SIZE_OVER_2, 
          VERTEX_SIZE, 
          VERTEX_SIZE);
      g.setColor(clr);
      g.fill(rect);
      Rectangle2D rectInner = new Rectangle2D.Double(
          p.getX() - INNER_SIZE_OVER_2,
          p.getY() - INNER_SIZE_OVER_2, 
          INNER_SIZE, 
          INNER_SIZE);
      g.setColor(AppConstants.VERTEX_HIGHLIGHT_CLR);
      g.fill(rectInner);

    }
  }
  
  private void drawHighlightedVertex(Graphics2D g, Coordinate pt, Color clr) {
    Rectangle2D rect = new Rectangle2D.Double();
    Point2D p = viewport.toView(pt);
    rect.setFrame(
        p.getX() - VERTEX_SIZE_OVER_2,
        p.getY() - VERTEX_SIZE_OVER_2, 
        VERTEX_SIZE, 
        VERTEX_SIZE);
    g.setColor(clr);
    g.fill(rect);
    Rectangle2D rectInner = new Rectangle2D.Double(
        p.getX() - INNER_SIZE_OVER_2,
        p.getY() - INNER_SIZE_OVER_2, 
        INNER_SIZE, 
        INNER_SIZE);
    g.setColor(AppConstants.VERTEX_HIGHLIGHT_CLR);
    g.fill(rectInner);
  }
  
  private static double VERTEX_SHADOW_SIZE_OVER_2 = AppConstants.VERTEX_SHADOW_SIZE / 2;

  private void drawVertexShadow(Graphics2D g, Coordinate pt, Color clr) {
    Ellipse2D rect = new Ellipse2D.Double();
    Point2D p = viewport.toView(pt);
    rect.setFrame(
        p.getX() - VERTEX_SHADOW_SIZE_OVER_2,
        p.getY() - VERTEX_SHADOW_SIZE_OVER_2, 
        AppConstants.VERTEX_SHADOW_SIZE, 
        AppConstants.VERTEX_SHADOW_SIZE);
    g.setColor(clr);
    g.fill(rect);
  }
  
  private void drawMark(Graphics2D g) {
    if (markPoint == null)
      return;
    
    String markLabel = markPoint.x + ",  " + markPoint.y;
    int strWidth = g.getFontMetrics().stringWidth(markLabel);

    double markSize = AppConstants.HIGHLIGHT_SIZE;
    Point2D highlightPointView = viewport.toView(markPoint);
    double markX = highlightPointView.getX();
    double markY = highlightPointView.getY();
    Ellipse2D.Double shape = new Ellipse2D.Double(
        markX - markSize / 2, 
        markY - markSize / 2,
        markSize, markSize);
    AWTUtil.setStroke(g, 4);
    g.setColor(AppConstants.HIGHLIGHT_CLR);
    g.draw(shape);
    
    // draw label box
    Envelope viewEnv = viewport.getViewEnv();
    
    int bottomOffset = 10;
    int boxHgt = 20;
    int boxPadX = 20;
    int boxWidth = strWidth + 2 * boxPadX;
    int arrowWidth = 10;
    int arrowOffset = 2;
    int labelOffsetY = 5;
    
    int bottom = (int) viewEnv.getMaxY() - bottomOffset;
    int centreX = (int) (viewEnv.getMinX() + viewEnv.getMaxX()) / 2;
    
    int boxMinX = centreX - boxWidth/2;
    int boxMaxX = centreX + boxWidth/2;
    int boxMinY = bottom - boxHgt;
    int boxMaxY = bottom;
    
    int[] xpts = new int[] { 
        boxMinX, centreX - arrowWidth/2, (int) markX, centreX + arrowWidth/2,
        boxMaxX, boxMaxX,   boxMinX };
    int[] ypts = new int[] {  
        boxMinY, boxMinY, (int) (markY + arrowOffset), boxMinY,
        boxMinY, boxMaxY, boxMaxY };
    
    Polygon poly = new Polygon(xpts, ypts, xpts.length);
    
    g.setColor(AppConstants.HIGHLIGHT_FILL_CLR);
    g.fill(poly);
    AWTUtil.setStroke(g, 1);
    g.setColor(ColorUtil.opaque(AppConstants.HIGHLIGHT_CLR));
    g.draw(poly);

    // draw mark point label
    g.setColor(Color.BLACK);
    g.drawString(markLabel, centreX - strWidth/2, boxMaxY - labelOffsetY);

  }

  /**
   * Draws a mask surround to indicate that geometry is being visually altered
   * @param g
   */
  private void drawMagnifyMask(Graphics2D g) {
    double viewWidth = viewport.getWidthInView();
    double viewHeight = viewport.getHeightInView();
    float minExtent = (float) Math.min(viewWidth, viewHeight);
    float maskWidth = (float) (minExtent * AppConstants.MASK_WIDTH_FRAC / 2);
    
    Area mask = new Area(new Rectangle2D.Float(
    		(float) 0, (float) 0, 
    		(float) viewWidth, (float) viewHeight));
    
    Area maskHole = new Area(new Rectangle2D.Float(
    		(float) maskWidth, 
    		(float) maskWidth, 
    		((float) viewWidth) - 2 * maskWidth, 
    		((float) viewHeight) - 2 * maskWidth));
    
    mask.subtract(maskHole);
    g.setColor(AppConstants.MASK_CLR);
    g.fill(mask);
  }

  public void flash(Geometry g)
  {
    Graphics2D gr = (Graphics2D) getGraphics();
    gr.setXORMode(Color.white);
    Stroke stroke = new BasicStroke(5);
    
    Geometry flashGeom = g;
    if (g instanceof org.locationtech.jts.geom.Point)
      flashGeom = flashPointGeom(g);
    
    try {
      GeometryPainter.paint(flashGeom, viewport, gr, Color.RED, null, stroke);
      Thread.sleep(200);
      GeometryPainter.paint(flashGeom, viewport, gr, Color.RED, null, stroke);
    }
    catch (Exception ex) { 
      // nothing we can do
    }
    gr.setPaintMode();
  }
    
  private Geometry flashPointGeom(Geometry g)
  {
    double ptRadius = viewport.toModel(4);
    return g.buffer(ptRadius);
  }
  
  

  void this_componentResized(ComponentEvent e) {
  	renderMgr.componentResized();
        viewport.update(this.getSize());
        //redraw the geometry
        if (!corrGeometryAndImageLoaded){
            drawGeometry();
            corrGeometryAndImageLoaded = true;
        }
  }

  /**
   * 
   * @param newTool tool to set, or null to clear tool
   */
  public void setCurrentTool(Tool newTool) {
    if (currentTool != null) currentTool.deactivate();
    currentTool = newTool;
    if (currentTool != null) currentTool.activate(this, this.isSecondPanel);
  }

  public void zoomToGeometry(int i) {
    Geometry g = getGeomModel().getGeometry(i);
    if (g == null) return;
    zoom(g.getEnvelopeInternal());
  }

  public void zoomToInput() {
    zoom(getGeomModel().getEnvelope());
  }

  public void zoomToResult() {
    zoom(getGeomModel().getEnvelopeResult());
  }

  public void zoomToFullExtent() {
    zoom(getGeomModel().getEnvelopeAll());
  }
  
  public void zoom(Geometry geom) 
  {
    if (geom == null) return;
    zoom(geom.getEnvelopeInternal());
  }
  
  public void zoom(Point2D zoomBox1, Point2D zoomBox2) 
  {
    Envelope zoomEnv = new Envelope();
    zoomEnv.expandToInclude(zoomBox1.getX(), zoomBox1.getY());
    zoomEnv.expandToInclude(zoomBox2.getX(), zoomBox2.getY());
    zoom(zoomEnv);
  }
  
  //called only when zooming by clicking, dragging the mouse to select an area to zoom in and releasing
  //not called when clicking or moving mouse wheel to zoom..
    public void zoom(Envelope zoomEnv) {
        if (zoomEnv == null)
          return;

        if (zoomEnv.isNull()) {
          viewport.zoomToInitialExtent();
          return;
        }
        double averageExtent = (zoomEnv.getWidth() + zoomEnv.getHeight()) / 2d;
        // fix to allow zooming to points
        if (averageExtent == 0.0)
          averageExtent = 1.0;
        double buffer = averageExtent * 0.1;
        zoomEnv.expandBy(buffer);
        viewport.zoom(zoomEnv);
    }

  /**
   * Zoom to a point, ensuring that the zoom point remains in the same screen location.
   * 
   * @param zoomPt
   * @param zoomFactor
   */
  public void zoom(Point2D zoomPt, double zoomFactor) {
      
    double zoomScale = getViewport().getScale() * zoomFactor;
    viewport.zoom(zoomPt, zoomScale);
  }
  
  //paning while viewport is zoomed (in or out)
  public void zoomPan(double dx, double dy) {
    getViewport().zoomPan(dx, dy);
  }

  public String cursorLocationString(Point2D pView)
  {
    Point2D p = getViewport().toModel(pView);
    NumberFormat format = getViewport().getScaleFormat();
    return format.format(p.getX()) 
    + ", " 
    + format.format(p.getY());
  }

  public Renderer getRenderer()
  {
    return new GeometryEditPanelRenderer();
  }
  
  class GeometryEditPanelRenderer implements Renderer
  {
    private GeometryStretcherView stretchView = null;
    private Renderer currentRenderer = null;
    private boolean isMagnifyingTopology = false; 
    private boolean isRenderingStretchVertices = false; 
    
    public GeometryEditPanelRenderer()
    {
        if (DisplayParameters.isMagnifyingTopology()) {
          stretchView = new GeometryStretcherView(getGeomModel());
          stretchView.setStretchSize(viewport.toModel(DisplayParameters.getTopologyStretchSize()));
          stretchView.setNearnessTolerance(viewport.toModel(GeometryStretcherView.NEARNESS_TOL_IN_VIEW));
          stretchView.setEnvelope(viewport.getModelEnv());
          isMagnifyingTopology = DisplayParameters.isMagnifyingTopology();
          isRenderingStretchVertices = stretchView.isViewPerformant();
        }  		
    }
  	
    public void render(Graphics2D g)
    {
        Graphics2D g2 = g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        if (isMagnifyingTopology) {
          if (isRenderingStretchVertices) {
            //renderMagnifiedVertexShadows(g2);
            renderMagnifiedVertexMask(g2);
          }
          else {
            // render indicator that shows stretched view is non-performant
            renderMagnifyWarning(g2);
          }
        }

        gridRenderer.paint(g2);

        renderLayers(g2);

        if (isMagnifyingTopology && isRenderingStretchVertices) {
          renderMagnifiedVertices(g2);
        }

        drawMark(g2);
      
    }
    
    public void renderLayers(Graphics2D g)
    {
    	LayerList layerList = getLayerList();
    	int n = layerList.size();
    	for (int i = 0; i < n; i++) {
    		if (isMagnifyingTopology && isRenderingStretchVertices
            && stretchView != null && i < 2) {
          //System.out.println("rendering stretch verts");
      		currentRenderer = new LayerRenderer(layerList.getLayer(i),
      				new StaticGeometryContainer(stretchView.getStretchedGeometry(i)),
      				viewport);
        }
    		else {
    			currentRenderer = new LayerRenderer(layerList.getLayer(i), viewport);
        }
    		currentRenderer.render(g);
    	}
    	currentRenderer = null;
    }
    
    public void renderMagnifiedVertices(Graphics2D g)
    {
      LayerList layerList = getLayerList();
      for (int i = 0; i < 2; i++) {
        // respect layer visibility
        if (! layerList.getLayer(i).isEnabled()) continue;
        
        List stretchedVerts = stretchView.getStretchedVertices(i);
        if (stretchedVerts == null) continue;
        for (int j = 0; j < stretchedVerts.size(); j++) {
          Coordinate p = (Coordinate) stretchedVerts.get(j);
          drawHighlightedVertex(g, p, 
            i == 0 ? GeometryDepiction.GEOM_A_HIGHLIGHT_CLR :
              GeometryDepiction.GEOM_B_HIGHLIGHT_CLR);
        } 
      }
    }
    
    public void renderMagnifiedVertexShadows(Graphics2D g)
    {
      if (stretchView == null) return;
      for (int i = 0; i < 2; i++) {
        List stretchedVerts = stretchView.getStretchedVertices(i);
        if (stretchedVerts == null) continue;
        for (int j = 0; j < stretchedVerts.size(); j++) {
          Coordinate p = (Coordinate) stretchedVerts.get(j);
          drawVertexShadow(g, p, AppConstants.VERTEX_SHADOW_CLR);
        }
      }
    }
    
    public void renderMagnifiedVertexMask(Graphics2D g)
    {
      if (stretchView == null) return;
      
      // render lowlight background
      Rectangle2D rect = new Rectangle2D.Float();
      rect.setFrame(
          0,
          0, 
          viewport.getWidthInView(), 
          viewport.getHeightInView());
      g.setColor(AppConstants.MASK_CLR);
      g.fill(rect);
  
      // highlight mag vertices
      for (int i = 0; i < 2; i++) {
        List stretchedVerts = stretchView.getStretchedVertices(i);
        if (stretchedVerts == null) continue;
        for (int j = 0; j < stretchedVerts.size(); j++) {
          Coordinate p = (Coordinate) stretchedVerts.get(j);
          drawVertexShadow(g, p, Color.WHITE);
        }
      }
    }
    
    public void renderMagnifyWarning(Graphics2D g)
    {
      if (stretchView == null) return;

      float maxx = (float) viewport.getWidthInView();
      float maxy = (float) viewport.getHeightInView();
      GeneralPath path = new GeneralPath();
      path.moveTo(0, 0);
      path.lineTo(maxx, maxy);
      path.moveTo(0, maxy);
      path.lineTo(maxx, 0);
      // render lowlight background
      g.setColor(AppConstants.MASK_CLR);
      g.setStroke(new BasicStroke(30));
      g.draw(path);
  
    }
    
    public synchronized void cancel()
    {
        if (currentRenderer != null)
                currentRenderer.cancel();
    }

  }

    public RenderManager getRenderMgr() {
        return renderMgr;
    }
  
    public GridRenderer getGridRenderer(){
        return gridRenderer;
    }

    public boolean isSecondPanel() {
        return isSecondPanel;
    }

    public void setPointsDrawn(boolean pointsDrawn) {
        this.pointsDrawn = pointsDrawn;
    }
    
    public boolean isPointsDrawn() {
        return pointsDrawn;
    }
    
}


