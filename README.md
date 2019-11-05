# SPT Data Lab

SPT Data Lab is a tool for the visualization, creation and refinement of Spatio-Temporal Data and for the evaluation of region interpolation methods. It is being developed at IEETA (Institute of Electronics and Informatics Engineering of Aveiro) and DETI (Departament of Electronic, Telecommunications and Informatics) from the University of Aveiro (www.ua.pt) and is partially funded by National Funds through the FCT (Foundation for Science and Technology) in the context of projects UID/CEC/00127/2013 and POCI-01-0145-FEDER-032636.

## Getting Started

Users can use their own files with geometries (e.g. observations of the evolution of a phenomenon) and edit them (refine them) using the GUI interface. The supported region interpolation methods can be used to create moving regions from the data provided by the user and to simulate the continuous evolution of the phenomenon being represented. SPT Data Lab can also be used to compute metrics over the results of the interpolation.

Users can visualize the interpolation and compute metrics, and save the results obtained in a png, csv or wkt representation.

SPT Data Lab provides a GUI and a command line interfaces. 

### Installation

Installation and user instructions are available at: https://github.com/most-ieeta/SPT-DataLab/wiki.

Instructions on configuration, including how to configure the application to work with third party software (e.g. Secondo and the PySpatioTemporalGeom library) can be found in the installation and user instructions area and in the configuration manual at: https://github.com/most-ieeta/SPT-DataLab/wiki/SPT-DataLab---Installation-notes.

### Source Code

The source code can be found at: https://github.com/most-ieeta/SPT-DataLab.

### Releases

You can download jar files for available releases at: https://github.com/most-ieeta/SPT-DataLab/releases.

### Example Datasets

Some example datasets are available at: https://github.com/most-ieeta/SPT-DataLab/tree/master/datasets.

### Videos

Old Videos:
Installing SPT Data Lab: https://drive.google.com/file/d/18eHRYa8c3AVx0DcrwLAuU_ffkWD6e63X/view?usp=sharing

Note:
You need to manually install the PySpatiotemporalGeom library.
SPTDataLab Key Features Overview: https://drive.google.com/file/d/1l97tkwmHq0eLaGpSinCjf1A8kvj_WLUj/view?usp=sharing

New Videos:
SPTDataLab Key Features Overview: https://drive.google.com/open?id=1_LvlUqpt6xIZcfrIYemj-VbIfFGzJQim
Setting up and using SPTDataLab from source code: https://drive.google.com/open?id=1zXVf5i3Xe57vj87yqX8kz2HXMpLY9qut
Additional configuration to use the Region Interpolation Methods: https://drive.google.com/open?id=1dWnvkC60HV9Sw3_5t_Wc-7oMdO8YVxOM
Using the latest release of SPTDataLab: https://drive.google.com/open?id=1oG-E3-rBvtQNyVCgLE0lPrwzYOEzcswX



## License

It is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

SPT Data Lab is distributed "AS IS" in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

SPT Data Lab includes or uses the following third party source code or binaries:

1. JTS (https://github.com/locationtech/jts) is licensed under the:

- Eclipse Public License 1.0 (https://www.eclipse.org/legal/epl-v10.html).
- Eclipse Distribution License 1.0 (a BSD Style License) (https://www.eclipse.org/org/documents/edl-v10.php).

2. Secondo (http://dna.fernuni-hagen.de/secondo/) is licensed under the GNU General Public License. 

3. The PySpatiotemporalGeom library (https://pypi.org/project/pyspatiotemporalgeom/) is licensed under the OSI Approved, MIT License.

4. The geomet library (https://pypi.org/project/geomet/) is licensed under the Apache Software License (Apache 2.0).

5. JFreeChart (http://www.jfree.org/jfreechart/) is distributed under the terms of the GNU Lesser General Public Licence (LGPL), which permits use in proprietary applications.

6. The GEOS library (https://trac.osgeo.org/geos/) is distributed under the terms of the GNU Lesser General Public License (LGPL).

7. Eigen (http://eigen.tuxfamily.org/) is licensed under the MPL2 license.

8. Libicp (http://www.cvlibs.net/software/libicp/) is distributed under the GNU General Public License.

All third party source code modified in SPT Data Lab team includes a notice. Please note that redistribution and use in source and binary forms, with or without modification, are permitted but you must retain copyright notices. You should also consider third party software terms and conditions of use.

# Acknowledgments

We want to thank Mark McKenney (http://www.cs.siue.edu/~marmcke/index.html) for helping us connect the application with the PySpatiotemporalGeom library.
