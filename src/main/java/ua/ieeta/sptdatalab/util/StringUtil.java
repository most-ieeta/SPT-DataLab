package ua.ieeta.sptdatalab.util;

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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.StringReader;
import org.apache.commons.lang3.StringEscapeUtils;

import org.locationtech.jts.util.Assert;


/**
 *  Useful string utilities
 *
 *@author     jaquino
 *@created    June 22, 2001
 *
 * @version 1.7
 */
public class StringUtil 
{
    public final static String newLine = System.getProperty("line.separator");

    
  	/**
  	 * Capitalizes the given string.
  	 * 
  	 * @param s the string to capitalize
  	 * @return the capitalized string
  	 */
  	public static String capitalize(String s)
  	{
  		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
  	}

    /**
     *  Returns an throwable's stack trace
     */
    public static String getStackTrace(Throwable t) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        t.printStackTrace(ps);
        return os.toString();
    }

    public static String getStackTrace(Throwable t, int depth) {
        String stackTrace = "";
        StringReader stringReader = new StringReader(getStackTrace(t));
        LineNumberReader lineNumberReader = new LineNumberReader(stringReader);
        for (int i = 0; i < depth; i++) {
            try {
                stackTrace += lineNumberReader.readLine() + newLine;
            } catch (IOException e) {
                Assert.shouldNeverReachHere();
            }
        }
        return stackTrace;
    }



    /**
     *  Returns true if substring is indeed a substring of string.
     */
    public static boolean contains(String string, String substring) {
        return string.indexOf(substring) > -1;
    }

    /**
     *  Returns a string with all occurrences of oldChar replaced by newStr
     */
    public static String replace(String str, char oldChar, String newStr) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == oldChar) {
                buf.append(newStr);
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }


    /**
     *  Replaces all instances of the String o with the String n in the
     *  StringBuffer orig if all is true, or only the first instance if all is
     *  false. Posted by Steve Chapel <schapel@breakthr.com> on UseNet
     */
    public static void replace(StringBuffer orig, String o, String n, boolean all) {
        if (orig == null || o == null || o.length() == 0 || n == null) {
            throw new IllegalArgumentException("Null or zero-length String");
        }
        int i = 0;
        while (i + o.length() <= orig.length()) {
            if (orig.substring(i, i + o.length()).equals(o)) {
                orig.replace(i, i + o.length(), n);
                if (!all) {
                    break;
                } else {
                    i += n.length();
                }
            } else {
                i++;
            }
        }
    }

    /**
     *  Returns original with all occurrences of oldSubstring replaced by
     *  newSubstring
     */
    public static String replaceAll(String original, String oldSubstring, String newSubstring) {
        return replace(original, oldSubstring, newSubstring, true);
    }


    /**
     *  Returns d as a string truncated to the specified number of decimal places
     */
    public static String format(double d, int decimals) {
        double factor = Math.pow(10, decimals);
        double digits = Math.round(factor * d);
        return ((int) Math.floor(digits / factor)) + "." + ((int) (digits % factor));
    }

    /**
     *  Line-wraps a string s by inserting CR-LF instead of the first space after the nth
     *  columns.
     */
    public static String wrap(String s, int n) {
        StringBuffer b = new StringBuffer();
        boolean wrapPending = false;
        for (int i = 0; i < s.length(); i++) {
            if (i % n == 0 && i > 0) {
                wrapPending = true;
            }
            char c = s.charAt(i);
            if (wrapPending && c == ' ') {
                b.append("\n");
                wrapPending = false;
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }





    /**
     *  Returns original with occurrences of oldSubstring replaced by
     *  newSubstring. Set all to true to replace all occurrences, or false to
     *  replace the first occurrence only.
     */
    public static String replace(
        String original,
        String oldSubstring,
        String newSubstring,
        boolean all) {
        StringBuffer b = new StringBuffer(original);
        replace(b, oldSubstring, newSubstring, all);
        return b.toString();
    }



    @SuppressWarnings("deprecation")
    public static String escapeHTML(String s) {
        return StringEscapeUtils.escapeHtml4(s);
    }


}
