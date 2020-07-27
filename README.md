# SPT Data Lab

SPT Data Lab is a tool for the visualization, creation and refinement of Spatio-Temporal Data and for the evaluation of region interpolation methods. It is being developed at IEETA (Institute of Electronics and Informatics Engineering of Aveiro) and DETI (Departament of Electronic, Telecommunications and Informatics) from the University of Aveiro (www.ua.pt) and is partially funded by National Funds through the FCT (Foundation for Science and Technology) in the context of projects UID/CEC/00127/2013 and POCI-01-0145-FEDER-032636.

## Getting Started

Users can use their own files with geometries (e.g. observations of the evolution of a phenomenon) and edit them (refine them) using the GUI interface. The supported region interpolation methods can be used to create moving regions from the data provided by the user and to simulate the continuous evolution of the phenomenon being represented. SPT Data Lab can also be used to compute metrics over the results of the interpolation.

Users can visualize the interpolation and compute metrics, and save the results obtained in a png, csv or wkt representation. SPT Data Lab provides GUI and command line interfaces.

Some of the additional functionalities in the second release:

- Integration with external segmentation tools to create filters, to segment video and images, and to extract frames from videos;
- New geometry simplification tools, including the simplification of a set of geometries using Douglas-Peucker and Visvalingam-Whyatt algorithms, and the Matching-Aware Simplification<sup>1</sup>
- Creation of SQL scripts to load geometries into DBMS using the discrete and the continuous models
- Geometry mathing tools that enable the match of pairs of geometries in batch mode for a set of geometries
- Selection of geometries from a sequence of observations with distinct timestamps, including the selection of key (distance-based) observations and identification of distance-based intervals<sup>2</sup>, and the selection of observations at fixed intervals
- Evaluation of interpolation algorithms (e.g. Secondo interpolation and PySpatioTemporalGeom) comparison of simulated data with real observations
- Algorithms to transform invalid geometries into valid ones
- Additional tools like the creation of statistics for geometries in a file (e.g. number of geometries, avg/max/min area, ...) and removing geometries from a set based on parameters (e.g. area).
 

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

SPTDataLab Key Features Overview: https://drive.google.com/open?id=1_LvlUqpt6xIZcfrIYemj-VbIfFGzJQim

## Citation

This software was first presented at ACM SIGSPATIAL '19 (runner-up demo paper). To cite this work, please use:

> José Duarte, Bruno Silva, José Moreira, Paulo Dias, Enrico Miranda, and Rogério L. C. Costa. 2019. **Towards a qualitative analysis of interpolation methods for deformable moving regions**. In *Proceedings of the 27th ACM SIGSPATIAL International Conference on Advances in Geographic Information Systems (SIGSPATIAL '19)*. ACM, New York, NY, USA, 592-595. DOI: https://doi.org/10.1145/3347146.3359368 

The new features from the second version were used in the following work, which you can cite if you use them:

> Rogério Luís de C. Costa, Enrico Miranda, Paulo Dias and José Moreira. 2020. **Evaluating Preprocessing and Interpolation Strategies to Create Moving Regions from Real-World Observations**. In *ACM SIGAPP Applied Computing Review* 20, 2. (2020), pp 46-58. DOI: https://dl.acm.org/doi/10.1145/3412816.3412820

<sup>1</sup>The matching-aware simplification was proposed in:

> Enrico Miranda, Rogério Luís de C. Costa, Paulo Dias and José Moreira. 2020. **Matching-Aware Shape Simplification**. In *15th International Conference on Computer Graphics Theory and Applications (GRAPP 2020). pp. 279-286*. DOI: https://doi.org/10.5220/0008969402790286 

<sup>2</sup>Distance-based observation selection was proposed in:

> Rogério Luís C. Costa, Enrico Miranda, Paulo Dias and José Moreira. 2020 **Sampling strategies to create moving regions from real world observations**. In *Proceedings of the 35th ACM/SIGAPP Symposium On Applied Computing (ACM SAC 2020)*. pp. 609-616. DOI: https://doi.org/10.1145/3341105.3374019. 




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
