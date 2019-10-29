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


package ua.ieeta.sptdatalab.ui;

import java.awt.Color;

import org.locationtech.jts.math.MathUtil;


public class ColorUtil {

  public static Color gray(int grayVal)
  {
    return new Color(grayVal, grayVal, grayVal);
  }
  
  public static Color opaque(Color clr)
  {
    return new Color(clr.getRed(), clr.getGreen(), clr.getBlue());
  }
  
  public static Color lighter(Color clr)
  {
    return lighter(clr, 0.4);
  }
  
  public static Color lighter(Color clr, double saturationFraction)
  {
    float[] hsb = new float[3];
    Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsb);
    hsb[1] = (float) MathUtil.clamp(hsb[1] * saturationFraction, 0, 1);
    Color chsb = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    return new Color(chsb.getRed(), chsb.getGreen(), chsb.getBlue(), clr.getAlpha());
  }
  
  public static Color saturate(Color clr, double saturation)
  {
    float[] hsb = new float[3];
    Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), hsb);
    hsb[1] = (float) MathUtil.clamp(saturation, 0, 1);;
    return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
  }

  public static float getHue(Color clr) {
    float[] hsb = Color.RGBtoHSB(clr.getRed(), clr.getGreen(), clr.getBlue(), null);
    return hsb[0];
  }

  public static Color setAlpha(Color clr, int alpha) {
    return new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), alpha);
  }

}
