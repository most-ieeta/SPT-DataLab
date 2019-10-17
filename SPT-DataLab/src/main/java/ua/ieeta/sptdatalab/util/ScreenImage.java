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

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 *  Convenience class to create and optionally save to a file a
 *  BufferedImage of an area on the screen. Generally there are
 *  four different scenarios. Create an image of:
 *
 *  a) an entire component
 *  b) a region of the component
 *  c) the entire desktop
 *  d) a region of the desktop
 *
 *  The first two use the Swing paint() method to draw the
 *  component image to the BufferedImage. The latter two use the
 *  AWT Robot to create the BufferedImage.
 *
 *	The created image can then be saved to a file by usig the
 *  writeImage(...) method. The type of file must be supported by the
 *  ImageIO write method.
 *
 *  Although this class was originally designed to create an image of a
 *  component on the screen it can be used to create an image of components
 *  not displayed on a GUI. Behind the scenes the component will be given a
 *  size and the component will be layed out. The default size will be the
 *  preferred size of the component although you can invoke the setSize()
 *  method on the component before invoking a createImage(...) method. The
 *  default functionality should work in most cases. However the only
 *  foolproof way to get a image to is make sure the component has been
 *  added to a realized window with code something like the following:
 *
 *  JFrame frame = new JFrame();
 *  frame.setContentPane( someComponent );
 *  frame.pack();
 *  ScreenImage.createImage( someComponent );
 *
 */
public class ScreenImage
{
	private static List<String> types = Arrays.asList( ImageIO.getWriterFileSuffixes() );

	/*
	 *  Create a BufferedImage for Swing components.
	 *  The entire component will be captured to an image.
	 *
	 *  @param  component Swing component to create image from
	 *  @return	image the image for the given region
	*/
	public static BufferedImage createImage(JComponent component)
	{
		Dimension d = component.getSize();

		if (d.width == 0 || d.height == 0)
		{
			d = component.getPreferredSize();
			component.setSize( d );
		}

		Rectangle region = new Rectangle(0, 0, d.width, d.height);
		return ScreenImage.createImage(component, region);
	}

	/*
	 *  Create a BufferedImage for Swing components.
	 *  All or part of the component can be captured to an image.
	 *
	 *  @param  component Swing component to create image from
	 *  @param  region The region of the component to be captured to an image
	 *  @return	image the image for the given region
	*/
	public static BufferedImage createImage(JComponent component, Rectangle region)
	{
		//  Make sure the component has a size and has been layed out.
		//  (necessary check for components not added to a realized frame)

		if (! component.isDisplayable())
		{
			Dimension d = component.getSize();

			if (d.width == 0 || d.height == 0)
			{
				d = component.getPreferredSize();
				component.setSize( d );
			}

			layoutComponent( component );
		}

		BufferedImage image = new BufferedImage(region.width, region.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();

		//  Paint a background for non-opaque components,
		//  otherwise the background will be black

		if (! component.isOpaque())
		{
			g2d.setColor( component.getBackground() );
			g2d.fillRect(region.x, region.y, region.width, region.height);
		}

		g2d.translate(-region.x, -region.y);
		component.print( g2d );
		g2d.dispose();
		return image;
	}

	/**
	 *  Convenience method to create a BufferedImage of the desktop
	 *
	 *  @param  fileName name of file to be created or null
	 *  @return	image the image for the given region
	 *  @exception AWTException see Robot class constructors
	 *  @exception IOException if an error occurs during writing
	 */
	public static BufferedImage createDesktopImage()
		throws AWTException, IOException
	{
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle region = new Rectangle(0, 0, d.width, d.height);
		return ScreenImage.createImage(region);
	}

	/*
	 *  Create a BufferedImage for AWT components.
	 *  This will include Swing components JFrame, JDialog and JWindow
	 *  which all extend from Component, not JComponent.
	 *
	 *  @param  component AWT component to create image from
	 *  @return	image the image for the given region
	 *  @exception AWTException see Robot class constructors
	*/
	public static BufferedImage createImage(Component component)
		throws AWTException
	{
		Point p = new Point(0, 0);
		SwingUtilities.convertPointToScreen(p, component);
		Rectangle region = component.getBounds();
		region.x = p.x;
		region.y = p.y;
		return ScreenImage.createImage(region);
	}

	/**
	 *  Create a BufferedImage from a rectangular region on the screen.
	 *
	 *  @param	 region region on the screen to create image from
	 *  @return	image the image for the given region
	 *  @exception AWTException see Robot class constructors
	 */
	public static BufferedImage createImage(Rectangle region)
		throws AWTException
	{
		BufferedImage image = new Robot().createScreenCapture( region );
		return image;
	}

	/**
	 *  Write a BufferedImage to a File.
	 *
	 *  @param	 image image to be written
	 *  @param	 fileName name of file to be created
	 *  @exception IOException if an error occurs during writing
	*/
	public static void writeImage(BufferedImage image, String fileName)
		throws IOException
	{
		if (fileName == null) return;

		int offset = fileName.lastIndexOf( "." );

		if (offset == -1)
		{
			String message = "file suffix was not specified";
			throw new IOException( message );
		}

		String type = fileName.substring(offset + 1);

		if (types.contains(type))
		{
			ImageIO.write(image, type, new File( fileName ));
		}
		else
		{
			String message = "unknown writer file suffix (" + type + ")";
			throw new IOException( message );
		}
	}

	static void layoutComponent(Component component)
	{
		synchronized (component.getTreeLock())
		{
			component.doLayout();

			if (component instanceof Container)
			{
				for (Component child : ((Container)component).getComponents())
				{
					layoutComponent(child);
				}
			}
		}
	}
        
        public static void printAndSaveAsImage(JComponent component){
            BufferedImage printScreenImage = createImage(component);
            
            // parent component of the dialog
            JFrame parentFrame = new JFrame();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select directory, name of the file and its extension");
            
            //set possible file extensions to save
            fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter()); //remove "allFiles" option
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".png", "png"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".jpeg", "jpeg"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".bmp", "bmp"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".tiff", "tiff"));
            // set default type
            fileChooser.setFileFilter(fileChooser.getChoosableFileFilters()[1]);
            
            int userSelection = fileChooser.showSaveDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                //System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                String fileName = fileToSave.getAbsolutePath();
                if (! fileName.contains(".")){
                    //user did not add an extension, add the file extension filter currently selected in the window
                    fileName += "." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
                }
                try {
                    writeImage(printScreenImage, fileName);
                } catch (IOException ex) {
                    Logger.getLogger(ScreenImage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }


}
