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
package ua.ieeta.sptdatalab.geomfunction;

import java.lang.annotation.Annotation;

public class MetadataUtil {

  public static String name(Annotation[] anno) {
    for (int i = 0; i < anno.length; i++) {
      if (anno[i] instanceof Metadata) {
        Metadata doc = (Metadata) anno[i];
        if (doc != null)
          return doc.name();
      }
    }
    return null;
  }

  public static String title(Annotation[] anno) {
    for (int i = 0; i < anno.length; i++) {
      if (anno[i] instanceof Metadata) {
        Metadata doc = (Metadata) anno[i];
        if (doc != null)
          return doc.title();
      }
    }
    return null;
  }

}
