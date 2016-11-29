/*
   In this simple drawing applet, the user can draw lines by pressing
   the mouse button and moving the mouse before releasing the button.
   A line is drawn from the point where the mouse button is pressed to the
   point where it is released.  A choice of colors is offered.
*/


import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.*;

public class SimpleDrawApplet extends Applet implements ItemListener {

    static final Color[] colorList = {
        // List of available colors; having this in an array makes
        // it much easier to write the itemStateChanged() routine.
        Color.black, Color.gray, Color.red, Color.green, Color.blue,
        new Color(200,0,0), new Color(0,180,0), new Color(0,0,180),
        Color.cyan, Color.magenta, Color.yellow, new Color(100,90,20)
    };

    Choice colorChoice; // A menu of available colors.

    SimpleDrawCanvas canvas;  // This is where the drawing is actually done.
                             // This applet displayes this canvas with some
                             // controls below it.

    public void init() {
        // Create components and lay out the applet
        Panel top = new Panel();
        Label status = new Label();
        status.setAlignment(Label.CENTER);
        status.setText("Hello world");
        top.add(status);

        canvas = new SimpleDrawCanvas(status);

        Panel bottom = new Panel();
        Button b1 = new Button("Clear");
        b1.addActionListener(canvas);    // Note that canvas will handle button events.
        bottom.add(b1);
        Button b2 = new Button("Close");
        b2.addActionListener(canvas);
        bottom.add(b2);
        Button b3 = new Button("Triangulate");
        b3.addActionListener(canvas);
        bottom.add(b3);
        Button b4 = new Button("Independent");
        b4.addActionListener(canvas);
        bottom.add(b4);
        Button b5 = new Button("Remove");
        b5.addActionListener(canvas);
        bottom.add(b5);
        Button b6 = new Button("Step");
        b6.addActionListener(canvas);
        bottom.add(b6);
        Button b7 = new Button("Locate Another Point");
        b7.addActionListener(canvas);
        bottom.add(b7);


        // colorChoice = new Choice();
        // colorChoice.add("Black");
        // colorChoice.add("Gray");
        // colorChoice.add("Red");
        // colorChoice.add("Green");
        // colorChoice.add("Blue");
        // colorChoice.add("Dark Red");
        // colorChoice.add("Dark Green");
        // colorChoice.add("Dark Blue");
        // colorChoice.add("Cyan");
        // colorChoice.add("Magenta");
        // colorChoice.add("Yellow");
        // colorChoice.add("Brown");
        // colorChoice.addItemListener(this);
        // bottom.add(colorChoice);

        setBackground(Color.lightGray);
        setLayout(new BorderLayout(3,3));
        add("Center",canvas);
        add("South",bottom);
        add("North",top);

    } // end init()

   public Insets getInsets() {
         // Say how much of a border to leave around the edges of the applet.
      return new Insets(3,3,3,3);
   }

   public void itemStateChanged(ItemEvent evt) {
         // This is called when the user selects an item from the colorChoice
         // menu.  The index of the selected item specifies the color to use
         // for drawing.  The canvas is notified of the new drawing color
         // by calling its setDrawing Color method.
      int colorIndex = colorChoice.getSelectedIndex();
      canvas.setDrawingColor(colorList[colorIndex]);
   }

} // end class SimpleDrawApplet


class Point {
    int x, y;
    Point (int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Vertex {
    int id;
    Point point;
    java.util.List<Integer> triangles;
    boolean removed;

    Vertex(int x, int y) {
        this.id = -1;
        this.point = new Point(x, y);
        this.triangles = new ArrayList<Integer>();
        this.removed = false;
    }

    void addTriangle(int triangle_id) {
        if (!this.triangles.contains(triangle_id)) {
            this.triangles.add(triangle_id);
        }
    }

    void removeTriangle(Integer triangle_id) {
        this.triangles.remove(triangle_id);
    }
}

class Edge {
    Point p1, p2;

    Edge(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}

class Triangle {
    int v1, v2, v3;

    Triangle(int v1, int v2, int v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }
}

class PlanarGraph {
    java.util.List<Vertex> vertices;
    java.util.List<java.util.List<Integer>> adj;
    java.util.List<Triangle> all_triangles;
    java.util.List<java.util.List<Integer>> triangulations;
    int numVertices;

    PlanarGraph() {
        this.vertices = new ArrayList<Vertex>();
        this.adj = new ArrayList<java.util.List<Integer>>();
        this.all_triangles = new ArrayList<Triangle>();
        this.triangulations = new ArrayList<java.util.List<Integer>>();
        this.numVertices = 0;
    }

    int addVertex(int x, int y) {
        Vertex v = new Vertex(x, y);
        v.id = this.vertices.size();
        this.vertices.add(v);
        java.util.List<Integer> neighbors = new ArrayList<Integer>();
        this.adj.add(neighbors);
        this.numVertices += 1;

        return v.id;
    }

    void removeVertex(int v, java.util.List<Integer> old_triangle_ids, java.util.List<Integer> polygon) {
        this.vertices.get(v).removed = true;
        this.numVertices -= 1;

        // java.util.List<Integer> neighbors = this.adj.get(v);
        // int size = neighbors.size();
        int size = this.adj.get(v).size();
        int[] neighbors = new int[size];
        for (int i=0; i < size; i++) {
            neighbors[i] = this.adj.get(v).get(i);
        }
        for(int i=0; i < size; i++) {
            int n = neighbors[i];
            this.removeDirectededge(v, n);
            this.removeDirectededge(n, v);
        }

        // old_triangle_ids = this.vertices.get(v).triangles;
        // Copy old triangles
        size = this.vertices.get(v).triangles.size();
        for (int i=0; i < size; i++) {
            old_triangle_ids.add(this.vertices.get(v).triangles.get(i));
        }
        java.util.List<java.util.List<Integer>> triangles = new ArrayList<java.util.List<Integer>>();
        for (int i=0; i < size; i++) {
            int t_id = this.vertices.get(v).triangles.get(i);
            Triangle t = this.all_triangles.get(t_id);
            java.util.List<Integer> tmp = new ArrayList<Integer>();
            if (t.v1 != v) {
                tmp.add(t.v1);
            } 
            if (t.v2 != v) {
                tmp.add(t.v2);
            }
            if (t.v3 != v) {
                tmp.add(t.v3);
            }
            triangles.add(tmp);
        }

        size = triangles.size();
        java.util.List<Integer> next = triangles.get(0);
        int nsize = next.size();
        polygon.add(next.get(0));
        int query = next.get(nsize-1);
        polygon.add(query);
        triangles.remove(0);
        for (int i=0; i < neighbors.length-2; i++) {
            // find element with query
            size = triangles.size();
            for (int j=0; j < size; j++) {
                java.util.List<Integer> q = triangles.get(j);
                if (q.contains(query)) {
                    next = q;
                    break;
                }
            }
            int old_query = query;
            nsize = next.size();
            if (next.get(0) == query) {
                query = next.get(nsize-1);
            } else {
                query = next.get(0);
            }
            polygon.add(query);
            java.util.List<java.util.List<Integer>> old_triangles = triangles;
            triangles = new ArrayList<java.util.List<Integer>>();
            size = old_triangles.size();
            for (int j=0; j < size; j++) {
                java.util.List<Integer> q = old_triangles.get(j);
                if (!q.contains(old_query)) {
                    triangles.add(q);
                }
            }
        }
        size = old_triangle_ids.size();
        for (int i=0; i < size; i++) {
            int t_id = old_triangle_ids.get(i);
            Triangle t = this.all_triangles.get(t_id);
            this.vertices.get(t.v1).removeTriangle(t_id);
            this.vertices.get(t.v2).removeTriangle(t_id);
            this.vertices.get(t.v3).removeTriangle(t_id);
        }
    }

    java.util.List<Integer> neighbors(int v) {
        return this.adj.get(v);
    }

    int degree(int v) {
        return this.adj.get(v).size();
    }

    void connect (int v1, int v2) {
        if ((!this.adj.get(v1).contains(v2)) && (!this.adj.get(v2).contains(v1))) {
            this.addDirectedEdge(v1, v2);
            this.addDirectedEdge(v2, v1);
        }
    }

    void addDirectedEdge(int v1, int v2) {
        this.adj.get(v1).add(v2);
    }

    void removeDirectededge(int v1, Integer v2) {
        this.adj.get(v1).remove(v2);
        // System.out.println(this.adj.get(v1))
    }
}

class Dag {
    TreeMap<Integer, java.util.List<Integer>> adj;
    Dag () {
        this.adj = new TreeMap<Integer, java.util.List<Integer>>();
    }

    void addDirectedEdge(Integer n1, Integer n2) {
        java.util.List<Integer> v = new ArrayList<Integer>();
        v.add(n2);
        if (!this.adj.containsKey(n1)) {
            this.adj.put(n1, v);
        } else {
            java.util.List<Integer> tmp = new ArrayList<Integer>();
            (this.adj.get(n1)).add(n2);
        }
    }

    java.util.List<Integer> children(Integer n) {
        return this.adj.get(n);
    }

    Integer root() {
        return this.adj.lastKey();
    }

}

class SimpleDrawCanvas extends Canvas implements ActionListener, MouseListener {
    // A canvas where the use can draw lines in various colors.


    Color currentColor;  // Color that is currently being used for drawing new lines.
    java.util.List<Point> points;
    // Point mousePoint;
    Point top, bottomleft, bottomright;
    boolean selfClose;
    boolean mouseClick;
    int step_count;
    java.util.List<Edge> edges;
    Dag dag;
    PlanarGraph graph;
    java.util.List<Integer> set;
    int mode; // 0 for clear, 1 for remove
    Point query;
    Integer highlight;
    java.util.List<Integer> triangulation;
    Label tf;
    SimpleDrawCanvas(Label status) { 
        // Construct the canvas, and set it to listen for mouse events.
        setBackground(Color.white);
        currentColor = Color.black;
    	addMouseListener(this);
        top = new Point(375, 5);
        bottomright = new Point(700, 650);
        bottomleft = new Point(55, 650);
        // pointSet.add(top);
        // pointSet.add(bottomleft); 
        // pointSet.add(bottomright);
        points = new ArrayList<Point>();
        edges = new ArrayList<Edge>();
        dag = new Dag();
        set = new ArrayList<Integer>();
        graph = new PlanarGraph();
        tf = status;
        doClear();
    }

    int orientation(Point p, Point q, Point r) {
    	int val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        // Counterclockwise: 1, Clockwise: 2
    	return (val > 0)? 1: 2;
    }
    boolean inside(Point r) {
        // return true;
    	if (orientation(top, bottomleft, r) == 1 &&
    	    orientation(bottomleft, bottomright, r) == 1 &&
    	    orientation(bottomright, top, r) == 1) 
    	    return true;
    	
    	return false;		
    }
    boolean intersect(Point p1, Point q1, Point p2, Point q2) {
	// Find the four orientations needed for general and
        // special cases
    	int o1 = orientation(p1, q1, p2);
    	int o2 = orientation(p1, q1, q2);
    	int o3 = orientation(p2, q2, p1);
    	int o4 = orientation(p2, q2, q1);
 
    	// General case
    	if (o1 != o2 && o3 != o4)
            return true;
	
    	return false;	
    }
    // boolean selfcross(Point r) {
    // 	int size = pointSet.size();
    // 	int i = 0;
    // 	// click 'close'
    // 	if (r.x == pointSet.get(0).x && r.y == pointSet.get(0).y) { 
    // 	    i = 1;
    // 	}
    // 	for (; i<size-2; i++ ) {
    // 	    //return true if added point r incur intersection
    // 	    if (intersect(pointSet.get(i), pointSet.get(i+1), pointSet.get(size-1), r))
    // 		return true;
    // 	}
    // 	return false;
    // }
    int ccw (Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
        // int result = (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
        // return -result;
    }
    boolean triangleContainsPoint(Point p, Point[] tri) {
        Point p1 = tri[0];
        Point p2 = tri[1];
        Point p3 = tri[2];

        return ((ccw(p1, p2, p) > 0) && (ccw(p2, p3, p) > 0) && (ccw(p3, p1, p) > 0)); 
    }
    boolean trianglesIntersect(Integer t_id1, Integer t_id2, PlanarGraph graph) {
        Triangle t1 = graph.all_triangles.get(t_id1);
        Triangle t2 = graph.all_triangles.get(t_id2);

        int[] tri1 = {t1.v1, t1.v2, t1.v3};
        int[] tri2 = {t2.v1, t2.v2, t2.v3};
        Point[] tri1_pts = new Point[3];
        Point[] tri2_pts = new Point[3];
        // java.util.List<Point> tri1_pts = new ArrayList<Point>();
        // java.util.List<Point> tri2_pts = new ArrayList<Point>();
        for (int i=0; i < 3; i++) {
            tri1_pts[i] = graph.vertices.get(tri1[i]).point;
            tri2_pts[i] = graph.vertices.get(tri2[i]).point;
            // tri1_pts.add(graph.vertices.get(tri1[i]).point);
            // tri2_pts.add(graph.vertices.get(tri2[i]).point);
        }

        int ktmp = 0;
        for (int i=0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point a = graph.vertices.get(tri1[i]).point; 
                Point b = graph.vertices.get(tri1[(i == 2) ? 0 : i+1]).point;
                Point c = graph.vertices.get(tri2[j]).point; 
                Point d = graph.vertices.get(tri2[(j == 2) ? 0 : j+1]).point;

                if (sidesIntersect(a, b, c, d)) return true;
                if (triangleContainsPoint(tri1_pts[i], tri2_pts)
                    || triangleContainsPoint(tri2_pts[j], tri1_pts)) 
                    return true;
            }
        }

        return false;
    }
    boolean sidesIntersect(Point a, Point b, Point c, Point d) {
        boolean int1 = (ccw(a, b, c) > 0) ? (ccw(a, b, d) < 0) : (ccw(a, b, d) > 0);
        boolean int2 = (ccw(c, d, a) > 0) ? (ccw(c, d, b) < 0) : (ccw(c, d, b) > 0);
        return int1 && int2;
    }
    public <T> java.util.List<T> union(java.util.List<T> list1, java.util.List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }
    void setDrawingColor(Color c) { 
        // Set current color to c.
        currentColor = c;
    }

    void doClear() {
        // Clear all the lines from the picture.
        points.clear();
        edges.clear();
        selfClose = false;
        mouseClick = false;
        step_count = 0;
        mode = 0;
        query = null;
        repaint();
    }

    void doClose() { 
        // Remove most recently added line from the picture.
        // repaint();
        if (this.points.size() < 3) {
            tf.setText("At least 3 points");
        } else {
            int size = this.points.size();
            Point first = this.points.get(0);
            Point last = this.points.get(size-1);

            boolean selfCrosses = false;
            int esize = this.edges.size();
            for(int i=0; i < esize; i++) {
                Edge e = this.edges.get(i);
                selfCrosses = selfCrosses || sidesIntersect(first, last, e.p1, e.p2);
            }

            if (selfCrosses) {
                tf.setText("No self-crosses allowed!");
            } else {
                Graphics g = getGraphics();
                drawLine(g, first, last);
            }
        }
    }

    void doTriangulate() {
        dag = new Dag();
        graph = new PlanarGraph();
        run(this.points, graph);
        Graphics g = getGraphics();
        drawGraph(g, this.graph, Color.black);
    }

    void doIndependent() {
        this.set = findIndependentSet(this.graph);
        Graphics g = getGraphics();

        int size = this.set.size();
        for(int i=0; i < size; i++) {
            int v = this.set.get(i);
            drawPoint(g, graph.vertices.get(v).point, Color.red);
        }
    }

    void doRemove() {
        removeVertices(this.graph, this.set, this.dag);
        this.mode = 1;
        if (this.graph.numVertices == 3) {
            locatePoint();
        }
        repaint();
        // Graphics g = getGraphics();
        // drawGraph(g, graph);
        // drawPoint(g, new Point(400, 200));
    }

    void doLocateAnother() {
        locatePoint();
        repaint();
    }

    void doStep() {
        int len = this.graph.triangulations.size();
        this.mode = 3; // Start 
        if (this.step_count >= len) {
            this.mode = 4; // Success
        }
        repaint();
    }

    public void actionPerformed(ActionEvent evt) {
        // A button has been clicked; do the appropriate command.
        String command = evt.getActionCommand();
        if (command.equals("Clear"))
            doClear();
        else if (command.equals("Close")) 
            doClose();
        else if (command.equals("Triangulate")) 
            doTriangulate();
        else if (command.equals("Independent"))
            doIndependent();
        else if (command.equals("Remove"))
            doRemove();
        else if (command.equals("Step"))
            doStep();
        else if (command.equals("Locate Another Point"))
            doLocateAnother();
    }

    // public void mousePressed(MouseEvent evt) {
    // }
    public void mouseClicked(MouseEvent evt) {}

    public void mouseReleased(MouseEvent evt) {
    } // end mouseReleased
    public void mousePressed(MouseEvent evt) { 
        Point p = new Point(evt.getX(), evt.getY());
        if (mode != 2) {
            Point last = new Point(0, 0);
            int size = this.points.size();
            if (size > 0) {
                last = this.points.get(size-1);
            }

            if (triangleContainsPoint(p, new Point[]{top, bottomright, bottomleft})) {
                boolean selfCrosses = false;
                if (size > 0) {
                    int esize = this.edges.size();
                    for (int i=0; i < esize; i++) {
                        Edge e = this.edges.get(i);
                        selfCrosses = selfCrosses || sidesIntersect(p, last, e.p1, e.p2);
                    }
                }
                if (!selfCrosses) {
                    Graphics g = getGraphics();
                    drawPoint(g, p, Color.black);

                    if (size > 0) {
                        drawLine(g, p, last);
                        edges.add(new Edge(p, last));
                    }

                    this.points.add(p);
                }
            } else {
                tf.setText("Out of triangle");
            }
        } else {
            if (triangleContainsPoint(p, new Point[]{top, bottomright, bottomleft})) {
                if (this.query == null) {
                    this.query = p;
                    Graphics g = getGraphics();
                    drawPoint(g, p, Color.green);

                    this.highlight = this.dag.root();
                    this.triangulation = this.dag.children(highlight);
                    this.step_count = 1;
                } else {
                    tf.setText("Query point already selected");
                }
            } else {
                tf.setText("Out of triangle");
            }
        }
    }  // Other methods in the MouseListener interface
    public void mouseEntered(MouseEvent evt) {}
    public void mouseExited(MouseEvent evt) {}

    void drawPoint(Graphics g, Point p, Color stroke) {
        g.setColor(stroke);
        g.fillOval(p.x-3, p.y-3, 6, 6);
    }
    void drawLine(Graphics g, Point a, Point b) {
        g.drawLine(a.x, a.y, b.x, b.y);
    }
    void drawTriangle(Graphics g, Point[] pts, Color stroke, Color fill, String title) {
        g.setColor(stroke);
        for(int i=0; i < 3; i ++) {
            drawPoint(g, pts[i], stroke);
        }
        drawLine(g, pts[0], pts[1]);
        drawLine(g, pts[0], pts[2]);
        drawLine(g, pts[2], pts[1]);

        if (fill != null) {
            int[] xpoints = new int[3];
            int[] ypoints = new int[3];
            for(int i=0; i<3; i++) {
                xpoints[i] = pts[i].x;
                ypoints[i] = pts[i].y;
            }
            int npoints = 3;
            g.setColor(fill);
            g.fillPolygon(xpoints, ypoints, npoints);
        } 
        if (title != null) {
            Point center = new Point((pts[0].x+pts[1].x+pts[2].x)/3, (pts[0].y+pts[1].y+pts[2].y)/3);
            g.setColor(stroke);
            g.drawString(title, center.x, center.y);
        }
    }
    void drawTriangulation(Graphics g, java.util.List<Integer> tris, PlanarGraph graph) {
        int size = tris.size();
        for(int i=0; i < size; i++) {
            Triangle tri = graph.all_triangles.get(tris.get(i));
            int[] pt = new int[] {tri.v1, tri.v2, tri.v3};
            Point[] pts = new Point[3];
            for(int j=0; j < 3; j++) {
                pts[j] = graph.vertices.get(pt[j]).point;
            }
            drawTriangle(g, pts, Color.gray, null, Integer.toString(tris.get(i)));
        }
    }
    void drawGraph(Graphics g, PlanarGraph graph, Color stroke) {
        // draw all points
        int size = graph.vertices.size();
        for(int i=0; i < size; i++) {
            Vertex v = graph.vertices.get(i);
            if (!v.removed) {
                drawPoint(g, v.point, stroke);
            }
        }

        // draw all edges
        size = graph.adj.size();
        for(int i=0; i < size; i++) {
            java.util.List<Integer> nbors = graph.adj.get(i);
            Point p1 = graph.vertices.get(i).point;
            Point p2;

            int nsize = nbors.size();
            for(int j=0; j < nsize; j++) {
                Integer n = nbors.get(j);
                p2 = graph.vertices.get(n).point;
                drawLine(g, p1, p2);
            }
        }
    }

    java.util.List<java.util.List<Integer>> getTriangulation (PlanarGraph graph, java.util.List<Integer> polygon, java.util.List<Integer> hole) {
        java.util.List<Integer> input = new ArrayList<Integer>();
        int size = polygon.size();
        for (int i=0; i < size; i++) {
            int index = polygon.get(i);
            input.add(graph.vertices.get(index).point.x);
            input.add(graph.vertices.get(index).point.y);
        }

        java.util.List<Integer> triangulations = new ArrayList<Integer>();
        if (hole != null) {
            int hole_index = input.size() / 2;
            size = hole.size();
            for (int i=0; i < size; i++) {
                int index = hole.get(i);
                input.add(graph.vertices.get(index).point.x);
                input.add(graph.vertices.get(index).point.y);
            }
            size = input.size();
            double[] input_para = new double[size];
            for (int i=0; i < size; i++) {
                input_para[i] = (double)input.get(i);
            }
            triangulations = Earcut.earcut(input_para, new int[] {hole_index}, 2);
        } else {
            size = input.size();
            double[] input_para = new double[size];
            for (int i=0; i < size; i++) {
                input_para[i] = (double)input.get(i);
            }
            triangulations = Earcut.earcut(input_para, null, 2);
        }

        java.util.List<java.util.List<Integer>> output = new ArrayList<java.util.List<Integer>>();
        size = triangulations.size();
        for (int i=0; i < size; i+=3) {
            int id1= triangulations.get(i);
            int id2= triangulations.get(i+1);
            int id3= triangulations.get(i+2);

            int p1, p2, p3;
            if (id1 < polygon.size()) {
                p1 = polygon.get(id1);
            } else {
                p1 = hole.get(id1-polygon.size());
            }
            if (id2 < polygon.size()) {
                p2 = polygon.get(id2);
            } else {
                p2 = hole.get(id2-polygon.size());
            }
            if (id3 < polygon.size()) {
                p3 = polygon.get(id3);
            } else {
                p3 = hole.get(id3-polygon.size());
            }

            java.util.List<Integer> tmp = new ArrayList<Integer>();
            tmp.add(p1);
            tmp.add(p2);
            tmp.add(p3);
            output.add(tmp);
        }
        return output;
    }

    java.util.List<Integer> triangulate(PlanarGraph graph, java.util.List<Integer> polygon, java.util.List<Integer> hole) {
        java.util.List<java.util.List<Integer>> triangles = getTriangulation(graph, polygon, hole);
        java.util.List<Integer> new_triangle_ids = new ArrayList<Integer>();

        int size = triangles.size();
        for(int i=0; i < size; i++) {
            java.util.List<Integer> t = triangles.get(i);
            graph.connect(t.get(0), t.get(1));
            graph.connect(t.get(1), t.get(2));
            graph.connect(t.get(2), t.get(0));

            Triangle tri = new Triangle(t.get(0), t.get(1), t.get(2));
            int triangle_id = graph.all_triangles.size();
            new_triangle_ids.add(triangle_id);
            graph.all_triangles.add(tri);

            graph.vertices.get(t.get(0)).addTriangle(triangle_id);
            graph.vertices.get(t.get(1)).addTriangle(triangle_id);
            graph.vertices.get(t.get(2)).addTriangle(triangle_id);
        }

        return new_triangle_ids;
    }

    void run (java.util.List<Point> pts, PlanarGraph graph) {
        java.util.List<Integer> polygon = new ArrayList<Integer>();
        int size = pts.size();

        for (int i=0; i < size; i++) {
            Point p = pts.get(i);
            graph.addVertex(p.x, p.y);
            if (i != 0) {
                graph.connect(i, i-1);
            }
            polygon.add(i);
        }

        graph.connect(0, size-1);

        java.util.List<Integer> inner_triangles = triangulate(graph, polygon, null);
        // add outer triangle
        // graph.addVertex(325, 5);
        // graph.addVertex(650, 650);
        // graph.addVertex(5, 650);
        graph.addVertex(this.top.x, this.top.y);
        graph.addVertex(this.bottomright.x, this.bottomright.y);
        graph.addVertex(this.bottomleft.x, this.bottomleft.y);

        size = pts.size();
        graph.connect(size, size+1);
        graph.connect(size+1, size+2);
        graph.connect(size, size+2);

        java.util.List<Integer> outer = new ArrayList<Integer>();
        outer.add(size);
        outer.add(size+1);
        outer.add(size+2);
        java.util.List<Integer> outer_triangles = triangulate(graph, outer, polygon);

        graph.triangulations.add(union(inner_triangles, outer_triangles));
    }

    java.util.List<Integer> findIndependentSet (PlanarGraph graph) {
        java.util.List<Integer> set = new ArrayList<Integer>();
        java.util.List<Integer> forbidden = new ArrayList<Integer>();

        for (int i=0; i < graph.vertices.size()-3; i++) {
            if (!graph.vertices.get(i).removed) {
                if (!forbidden.contains(i)) {
                    if (graph.degree(i) <= 8) {
                        set.add(i);
                        java.util.List<Integer> neighbors = graph.neighbors(i);
                        int size = neighbors.size();
                        for (int j=0; j < size; j++) {
                            int n = neighbors.get(j);
                            forbidden.add(n);
                        }
                    }
                }
            }
        }
        return set;
    }

    void removeVertices(PlanarGraph graph, java.util.List<Integer> verts, Dag dag) {
        int size = graph.triangulations.size();
        java.util.List<Integer> triangles = new ArrayList<Integer>();
        java.util.List<Integer> tmp = graph.triangulations.get(size-1);
        int nsize = tmp.size();
        for (int i=0; i < nsize; i++) {
            int v = tmp.get(i);
            triangles.add(v);
        }

        size = verts.size();
        for(int i=0; i < size; i++) {
            int v = verts.get(i);
            java.util.List<Integer> old_triangle_ids = new ArrayList<Integer>();
            java.util.List<Integer> polygon = new ArrayList<Integer>();
            graph.removeVertex(v, old_triangle_ids, polygon);
            java.util.List<Integer> old_triangles = triangles;
            triangles = new ArrayList<Integer>();
            int osize = old_triangles.size();
            for(int j=0; j<osize; j++) {
                int t = old_triangles.get(j);
                if (!old_triangle_ids.contains(t)) {
                    triangles.add(t);
                }
            }
            java.util.List<Integer> new_triangles = triangulate(graph, polygon, null);

            nsize = new_triangles.size();
            for(int j=0; j<nsize; j++) {
                triangles.add(new_triangles.get(j));
            }

            for(int j=0; j< osize; j++) {
                for (int k=0; k < nsize; k++) {
                    int o = old_triangles.get(j);
                    int n = new_triangles.get(k);
                    if (trianglesIntersect(o, n, graph)) {
                        dag.addDirectedEdge(n, o);
                    }
                }
            }
            graph.triangulations.add(triangles);
        }
    }

    public void locatePoint() {
        this.step_count = 0;
        this.mode = 2;
        this.query = null;
    }

    public void paint(Graphics g) {
        drawTriangle(g, new Point[] {top, bottomright, bottomleft}, Color.black, null, null);
        if (this.mode == 1) {
            drawGraph(g, this.graph, Color.black);
        } else if (this.mode == 2) {
            drawTriangulation(g, graph.triangulations.get(0), graph);
        } else if (this.mode == 3) {
            Triangle tri = graph.all_triangles.get(this.highlight);
            int[] ptsIndex = new int[] {tri.v1, tri.v2, tri.v3};
            Point[] pts = new Point[3];
            for (int i=0; i < 3; i++) {
                pts[i] = graph.vertices.get(ptsIndex[i]).point;
            }
            drawTriangle(g, pts, Color.yellow, Color.yellow, null);
            int len = graph.triangulations.size();
            java.util.List<Integer> triangulation = graph.triangulations.get(len - 1 - this.step_count);
            drawTriangulation(g, triangulation, graph);

            // find next highlight 
            if (this.dag.children(highlight) != null) {
                java.util.List<Integer> v = dag.children(highlight);
                int size = v.size();
                int i = 0;
                for (i = 0; i < size; i++) {
                    Integer c = v.get(i);
                    Triangle t = graph.all_triangles.get(c);
                    ptsIndex = new int[] {t.v1, t.v2, t.v3};
                    pts = new Point[3];
                    for (int j=0; j < 3; j++) {
                        pts[j] = graph.vertices.get(ptsIndex[j]).point;
                    }
                    if (triangleContainsPoint(this.query, pts)) {
                        highlight = c;
                        break;
                    }
                }
                if (i == size) {
                    highlight = null;
                }
            }

            if(highlight == null) {
                // this.mode = 5;
                // repaint();
                // refresh();
                drawTriangulation(g, this.graph.triangulations.get(0), graph);
                drawPoint(g, this.query, Color.green);
                // System.out.println("SUCCESS: Point located!");
                tf.setText("SUCCESS: Point located!");
            } else {
                drawPoint(g, this.query, Color.green);
                while (this.graph.triangulations.get(len - 1 - this.step_count).contains(highlight)) {
                    this.step_count += 1;
                    if ((len - 1 - step_count) == -1) {
                        break;
                    }
                }
            }
        } else if (mode == 4) {
            drawTriangulation(g, this.graph.triangulations.get(0), graph);
            Triangle tri = graph.all_triangles.get(this.highlight);
            int[] ptsIndex = new int[] {tri.v1, tri.v2, tri.v3};
            Point[] pts = new Point[3];
            for (int i=0; i < 3; i++){
                pts[i] = this.graph.vertices.get(ptsIndex[i]).point;
            }
            drawTriangle(g, pts, Color.gray, Color.yellow, Integer.toString(highlight));
            drawPoint(g, this.query, Color.green);
            tf.setText("SUCCESS: Point located!");
        }
        // } else if (mode == 5) {
        //     drawTriangulation(g, this.graph.triangulations.get(0), graph);
        //     drawPoint(g, this.query, Color.green);
        //     System.out.println("SUCCESS: Point located!");
        //     // tf.setText("SUCCESS: Point located!");
        // }
    }
    void refresh() {
        repaint();
    }
} // end class SimpleDrawCanvas
