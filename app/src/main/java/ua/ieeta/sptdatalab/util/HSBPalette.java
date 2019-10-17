package ua.ieeta.sptdatalab.util;

import java.awt.Color;

import ua.ieeta.sptdatalab.ui.ColorUtil;

public class HSBPalette {
  
  public static HSBPalette createRainbowSequential(int numHue, float s, float b) {
    return new HSBPalette(numHue, 0, 1,
        1, s, s,
        1, b, b
        ); 
  }

  public static HSBPalette createRainbowIncremental(float hInc, float s, float b) {
    HSBPalette pal = new HSBPalette(50, 0, 1,
        1, s, s,
        1, b, b
        ); 
    pal.setHueInc(hInc);
    return pal;
  }

  private void setHueInc(float hInc) {
      this.hInc = hInc;
   }

  int numH = 5;
  int numS = 3;
  int numB = 3;
  
  float hInc = 0.03f;
  float sInc = 0.1f;
  float bInc = 0.1f;
  
  private int numEntries;
  private float sLo;
  private float bLo;
  private float hBase;
  private float hRange;
  
  private float h1;
  
  public HSBPalette(
    int numH, float hBase, float hRange,
    int numS, float sLo, float sHi,
    int numB, float bLo, float bHi) 
  {
    this.numH = numH;
    this.numS = numS;
    this.numB = numB;
    this.hBase = hBase;
    this.hRange = hRange;
    this.h1 = hBase - hRange / 2;
    this.sLo = sLo;
    this.bLo = bLo;
    // TODO: make this mod 1
    this.hInc = (numH < 2) ? 0 : (hRange) / (numH - 1);
    this.sInc = (numS < 2) ? 0 : (sHi - sLo) / (numS - 1);
    this.bInc = (numB < 2) ? 0 : (bHi - bLo) / (numB - 1);
    numEntries = numH * numS * numB;
  }
  public int numEntries() { return numEntries; }
  
  public Color color(int index, int alpha) {
    int i = index % numEntries;
    int iH = i / (numS * numB);
    int iSB = i - iH * (numS * numB);
    int iS = iSB / numB;
    int iB = iSB - iS * numB;
    float h = (h1 + iH * hInc) % 1.0f;
    float s = sLo + iS * sInc;
    float b = bLo + iB * bInc;
    Color chsb = Color.getHSBColor(h, s, b);
    return ColorUtil.setAlpha(chsb, alpha);
  }
  
}