# Computational Geometry Projects
## Topic: Pedagogy (Graphical Applets)

## Description: 
In this project, we developed a java applet to implemented the Kirkpatrick’s point location algorithm and use a graphical interface to show the run-time procedure. 


## Usage:
Unarchive the project file, click the index.html in the “test” folder. And follow the instruction in that webpage.

1. Click inside the triangle to draw your own polygon. After you finish all of your vertices, click “Close” button to get a closed polygon
![](F2AC7BC3-E713-4370-B458-509F8F8D63CC.png)

2. Click ”Triangulate” button to triangulate the entire polygon. 
![](37064BA6-8B9C-4E48-A225-69B69F1F337E.png)

3. Click “Independent” button to find the independent vertex and click “Remove” button to remove it from the polygon. Keep this operation until removing all the independent vertex
![](018E50A1-A4A0-4EC5-8825-A79ACDB70896.png)

4. Click inside the triangle to get the query point (It should be indicated in Green)
![](2EA1C172-86C1-4F1D-A35E-E097073E6DAA.png)

5. Keep click “Step” button to get the location of the Point 
![](00BB738A-5AEA-420E-822A-8374D64CBFB0.png)
![](7E99975F-1F04-49F2-9CEE-4BB245CFCB07.png)
![](B4CEF8D0-BFEA-44FA-B746-018B7509F737.png)


## Implementation 
In our project, we implement the Kirkpatrick’s point location algorithm. There are 2 main problems needs to be solved:
**1. Polygon Triangulation**
The triangulation algorithm is based on the earcut method. We use a java library to do this job. You can find it in [earcut4j](https://github.com/earcut4j/earcut4j)

**2. Point Location**
This part is totally implemented by ourselves. We use the standard Kirkpatrick’s algorithm as the professor Pless has taught in the class. 
We build a PlanarGraph (you can find the all the code in “src” folder) class to represent the whole graph. We store all the vertex, edge, and the corresponding triangle in it and implemented all corresponding method such as the addVertex, removeVertex, addDirectedEdge. 
We also build a DAG class to represent the parent/child relationship between all the triangle inside the PlanarGraph. That’s very useful in point location


## Distribution
SHAO-CHIANG TSAI: Polygon triangulation and the user interface

LINGTONG LU: Implementation of the point location algorithm. 