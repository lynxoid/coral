CORAL
=====

An integrated suite of visualizations for comparing clusterings.

This tool is described in:

D. Filippova, A. Gadani, C. Kingsford. Coral: An integrated suite of visualizations
for comparing clusterings. To appear: BMC Bioinformatics. 2012

Requirements
-------------
 - Java 1.6 or newer

Running
--------------
 - on MacOS: use Coral.app
 - from command line: <code>./coral</code>

TODO
--------------
+ ~~color scheme for matrix~~
+ ~~clusterings overview (density, mean size, # unique vert)~~
+ ~~dataModel to dispatch change events to listeners (containers)~~
+ ~~row reordering~~
+ ~~2 clusterings comparison table~~
+ ~~slider to cut off J.score in module-module table~~
+ ~~clusterings comparison by metrics~~
+ ~~vertex table~~
- filter by the # of co-occurrences
+ ~~ladder component~~
+ ~~color ladder cells based on score~~
+ ~~add/remove clustering from the view~~
- small zoomable window on the matrix for navigation (lens w/ labels)
- network viz for the co-occured nodes
- labels on the matrix
+ ~~color legend for matrix~~
- save session/load old session
+ ~~matrix -> output for cytoscape (# cooccurences - weig, ts)~~
+ ~~bar chart w/ overview~~
+ ~~labels on the bar chart (overview panel)~~
+ ~~QUIT command not ending the process~~
+ ~~clicking on black area on the matrix - deselect the vertex pair~~
+ ~~style JaccardScore slider~~
+ ~~dockable windows for each panel~~
- data filtering panel - global filtering options on the whole dataset
- small multiples view for the slider above module table (row count 4 that table)
- filtering on table (search string)
+ ~~ladder doesn't show when you load it in some particular way~~
- selecting regions on the matrix to reorder it manually
+ ~~legend: forсe an update (waits for the 2nd coming of Christ)~~
- matrix dragging
+ ~~compute matrix reordering first; show matrix; then compute clustering comparison~~
