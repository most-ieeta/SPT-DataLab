The datasets available here contain images of deformable objects in discrete times.
For every 2 images, there is a file (.corr or .wkt) with pixel coordinates for the object in those 2 images 
(1st image is source, 2nd is target). 

The provided datasets are ready to be used by SPTDataLab.

-Iceberg: Discrete observation of 2 icebergs in months. Contains a directory with images of all observations, ordered
from oldest to newest and 3 directories with pixel coordinates of source and target geometries of all observations,
2 for the longer iceberg in the center (one has .wkt file, another has .corr), another one for the smaller iceberg 
(.corr files).
-Fire: Discrete observation of fire spreading through vegetation in a controlled environment. 
Contains a directory with images of all observations and a directory with  with pixel coordinates of source and 
target geometries of all observations.

If you wish to add more datasets, please check the following information in order to make your dataset
compatible with SPTDataLab:
https://github.com/most-ieeta/SPT-DataLab/wiki/SPT-DataLab---GUI-Application
