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

package ua.ieeta.sptdatalab.util;

import java.io.*;

public class FileUtil 
{
  public static final String EXTENSION_SEPARATOR = ".";


 
    public static String readText(String filename) 
    throws IOException 
    {
      return readText(new File(filename));
    }
    
    /**
     * Gets the contents of a text file as a single String
     * @param file
     * @return text file contents
     * @throws IOException
     */
 public static String readText(File file) 
  	throws IOException 
  	{
		String thisLine;
		StringBuffer strb = new StringBuffer("");

		FileInputStream fin = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
		while ((thisLine = br.readLine()) != null) {
			strb.append(thisLine + "\r\n");
		}
		String result = strb.toString();
		return result;
	}

    /**
		 * Saves the String with the given filename
		 */
    public static void setContents(String textFileName, String contents) throws IOException {
        FileWriter fileWriter = new FileWriter(textFileName, false);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(contents);
        bufferedWriter.flush();
        bufferedWriter.close();
        fileWriter.close();
    }

}
