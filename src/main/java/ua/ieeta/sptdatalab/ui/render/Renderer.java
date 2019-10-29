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

import java.awt.Graphics2D;

/**
 * A process object which renders a scene to a graphics context
 * and allows cancellation.
 * A Renderer can be cancelled, which may allow it to 
 * stop or short-circuit processing, thus saving cycles.
 * A cancelled rendering will not be displayed.
 * 
 * @author mbdavis
 *
 */
public interface Renderer 
{
	/**
	 * Renders the scene to the graphics context.
	 * 
	 * @param g the graphics context to render to
	 */
	void render(Graphics2D g);
	
	/**
	 * Informs this process that it can stop rendering,
	 * because the rendered context will not be displayed.
	 *
	 */
	void cancel();
}
